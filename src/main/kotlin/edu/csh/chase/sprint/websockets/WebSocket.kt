package edu.csh.chase.sprint.websockets

import edu.csh.chase.sprint.*
import edu.csh.chase.sprint.parameters.UrlParameters
import okhttp3.Headers
import okhttp3.OkHttpClient
import okhttp3.WebSocketListener
import okio.Buffer
import okio.ByteString
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit
import okhttp3.Response as OkResponse
import okhttp3.WebSocket as OkWebSocket

abstract class WebSocket(protected val request: Request,
                         client: OkHttpClient = Sprint.webSocketClient,
                         val retries: BackoffTimeout = BackoffTimeout.Exponential(500L, 2, 300000L, 5),
                         autoConnect: Boolean = false) : WebSocketCallbacks {

    private val listeners: ArrayList<WebSocketCallbacks> = ArrayList()
    private var socket: OkWebSocket? = null
    private val client: OkHttpClient

    private var shouldReconnect = false

    private val safeListeners: List<WebSocketCallbacks>
        get() = synchronized(listeners) { listeners.toList() }

    private val listenerCallBacks = object : WebSocketListener() {

        override fun onOpen(webSocket: OkWebSocket, response: OkResponse) {
            this@WebSocket.onOpen(webSocket, response)
        }

        override fun onMessage(webSocket: okhttp3.WebSocket, text: String) {
            this@WebSocket.onMessage(text)
        }

        override fun onMessage(webSocket: okhttp3.WebSocket, bytes: ByteString) {
        }

        override fun onClosing(webSocket: okhttp3.WebSocket, code: Int, reason: String) {
            this@WebSocket.onClose(code, reason, webSocket)
        }

        override fun onClosed(webSocket: okhttp3.WebSocket, code: Int, reason: String) {
        }

        override fun onFailure(webSocket: okhttp3.WebSocket?, t: Throwable?, response: okhttp3.Response?) {
            this@WebSocket.onFailure(IOException(t), response)
        }
    }

    var state: State = State.Disconnected
        private set

    enum class State {
        Connecting,
        Connected,
        Disconnecting,
        Disconnected,
        Errored//Functions the same as Disconnected, just notes that an error has occurred
    }

    constructor(url: String,
                client: OkHttpClient = Sprint.webSocketClient,
                urlParameters: UrlParameters? = null,
                headers: Headers.Builder = Headers.Builder(),
                extraData: Any? = null,
                retries: BackoffTimeout = BackoffTimeout.Exponential(500L, 2, 300000L, 5),
                autoConnect: Boolean = false) : this(GetRequest(url, urlParameters, headers, extraData),
        client, retries, autoConnect)

    init {
        addCallback(this)

        if (client.readTimeoutMillis() == 0) {
            this.client = client
        } else {
            this.client = client.newBuilder().readTimeout(0L, TimeUnit.MILLISECONDS).build()
        }

        if (autoConnect) {
            connect()
        }
    }

    fun sendText(text: String) {
        if (state != State.Connected) {
            return
        }

        try {
            socket!!.send(text)
        } catch (e: IOException) {
            socket!!.close(WebSocketDisconnect.protocolError, e.message)
        } catch (e: IllegalArgumentException) {

        }
    }

    fun sendBytes(bytes: ByteArray) {
        if (state != State.Connected) {
            return
        }

        try {
            socket!!.send(ByteString.of(*bytes))
        } catch (e: IOException) {
            socket!!.close(WebSocketDisconnect.protocolError, e.message)
        } catch (e: IllegalArgumentException) {

        }
    }

    //TODO consider returning a boolean success/failure
    //Success for will attempt to connect
    //Error for already connected/already attempting to connect
    fun connect() {
        synchronized(this) {
            if (state == State.Connected || state == State.Connecting || state == State.Disconnecting) {
                //Already connected or attempting to connect
                return
            }

            if (request.requestType != RequestType.Get) {
                throw IllegalArgumentException("WebSocket requests must have a HTTP Method of GET, got ${request.requestType}.")
            }

            if (!request.url.startsWith("ws://") && !request.url.startsWith("wss://")) {
                throw IllegalArgumentException("WebSocket requests must have the URL Schema of ws:// or wss://.")
            }

            socket = client.newWebSocket(request.okHttpRequest, listenerCallBacks)
            state = State.Connecting
        }
    }

    fun disconnect(code: Int, reason: String?, reconnect: Boolean = false) {
        synchronized(this) {
            this.shouldReconnect = reconnect

            if (state != State.Connected && state != State.Connecting) {
                //Already closed
                return
            }

            socket!!.close(code, reason)
            socket = null

            state = State.Disconnecting
        }
    }

    @Deprecated(
        message = "disconnect now takes a reconnect",
        replaceWith = ReplaceWith("disconnect(WebSocketDisconnect.normalClosure, null, true)")
    )
    fun resetConnection() {
        disconnect(WebSocketDisconnect.normalClosure, null, true)

    }

    private fun onOpen(webSocket: OkWebSocket, okResponse: OkResponse) {
        synchronized(this) {
            if (socket != webSocket) {
                socket?.cancel()//Cancel the old one.
                socket = webSocket
            }

            state = State.Connected

            shouldReconnect = false
            retries.reset() //Reset the retry count as a new connection was established
        }

        val response = Response.Success(this.request, okResponse)

        safeListeners.forEach {
            it.onConnect(response)
        }
    }

    private fun onPong(payload: Buffer?) {
        safeListeners.forEach { it.pongReceived(payload) }
    }

    private fun onClose(code: Int, reason: String?, webSocket: okhttp3.WebSocket) {
        safeListeners.forEach { it.onDisconnect(code, reason) }

        synchronized(this) {
            val oldState = state

            //Clean up everything
            webSocket.cancel()
            socket?.cancel()
            socket = null

            state = State.Disconnected

            //If the server closes the connection, state will be Connected here
            //If the client disconnects closed will be true
            if ((shouldRetry(RetryReason.Disconnect(code, reason)) && oldState == State.Connected) || shouldReconnect) {
                doRetry(RetryReason.Disconnect(code, reason))
            }
        }
    }

    private fun onFailure(exception: IOException, response: OkResponse?) {
        val res = Response.ConnectionError(this.request, exception)
        safeListeners.forEach { it.onError(exception, res) }

        synchronized(this) {
            socket?.cancel()
            socket = null
            state = State.Errored

            if (shouldRetry(RetryReason.Error(exception))) {
                doRetry(RetryReason.Error(exception))
            }
        }
    }

    private fun onMessage(message: String) {
        safeListeners.forEach { it.messageReceived(message) }
    }

    private fun doRetry(reason: RetryReason) {
        if (!retries.shouldRetry()) {
            return
        }

        Thread.sleep(retries.getNextDelay())

        if (shouldRetry(reason)) {

            connect()
        }
    }

    open fun shouldRetry(reason: RetryReason): Boolean {
        return when (reason) {
            is RetryReason.Error -> true
            is RetryReason.Disconnect -> reason.code !in listOf(1000, 1004, 1008, 1010)
        }
    }

    fun addCallback(cb: WebSocketCallbacks) {
        synchronized(listeners) {
            listeners.add(cb)
        }
    }

    fun removeCallback(cb: WebSocketCallbacks) {
        if (cb == this) {
            //Can't remove yourself from webSocket callbacks
            return
        }
        synchronized(listeners) {
            listeners.remove(cb)
        }
    }

    fun addCallBack(cb: (WebSocketEvent) -> Unit): WebSocketCallbacks {
        val _cb = cb.toWebSocketCallback()

        synchronized(listeners) {
            listeners.add(_cb)
        }

        return _cb
    }

}