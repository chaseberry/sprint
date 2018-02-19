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
                         val retryCount: Int = 4,
                         val autoConnect: Boolean = false) : WebSocketCallbacks {

    private val listeners: ArrayList<WebSocketCallbacks> = ArrayList()
    private var socket: OkWebSocket? = null
    private var currentRetry: Int = retryCount
    private val client: OkHttpClient

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
        }

        override fun onClosed(webSocket: okhttp3.WebSocket, code: Int, reason: String) {
            this@WebSocket.onClose(code, reason)
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
        Resetting,
        Errored//Functions the same as Disconnected, just notes that an error has occurred
    }

    constructor(url: String,
                client: OkHttpClient = Sprint.webSocketClient,
                urlParameters: UrlParameters? = null,
                headers: Headers.Builder = Headers.Builder(),
                extraData: Any? = null,
                retryCount: Int = 4,
                autoConnect: Boolean = false) : this(GetRequest(url, urlParameters, headers, extraData),
        client, retryCount, autoConnect)

    init {
        listeners.add(this)

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
        } catch(e: IOException) {
            socket!!.close(WebSocketDisconnect.protocolError, e.message)
        } catch(e: IllegalArgumentException) {

        }
    }

    //TODO consider returning a boolean success/failure
    //Success for will attempt to connect
    //Error for already connected/already attempting to connect
    fun connect() {
        if (state == State.Connected || state == State.Connecting) {
            //Already connected or attempting to connect
            return
        }

        if (request.requestType != RequestType.Get) {
            throw IllegalArgumentException("WebSocket requests must have a HTTP Method of GET, got ${request.requestType}.")
        }

        if (!request.url.startsWith("ws://") && !request.url.startsWith("wss://")) {
            throw IllegalArgumentException("WebSocket requests must have the URL Schema of ws:// or wss://.")
        }

        state = State.Connecting
        socket = client.newWebSocket(request.okHttpRequest, listenerCallBacks)
    }

    fun disconnect(code: Int, reason: String?) {
        if (state != State.Connected && state != State.Connecting) {
            //Already closed
            return
        }
        state = State.Disconnecting
        socket!!.close(code, reason)
        socket = null
    }

    fun resetConnection() {
        disconnect(WebSocketDisconnect.normalClosure, null)
        state = State.Resetting
        connect()
    }

    private fun onOpen(webSocket: OkWebSocket, okResponse: OkResponse) {
        state = State.Connected
        socket = webSocket
        currentRetry = retryCount //Reset the retry count as a new connection was established

        val response = Response.Success(this.request, okResponse)

        safeListeners.forEach {
            it.onConnect(response)
        }
    }

    private fun onPong(payload: Buffer?) {
        safeListeners.forEach { it.pongReceived(payload) }
    }

    private fun onClose(code: Int, reason: String?) {
        if (state == State.Resetting) {
            return
        }

        safeListeners.forEach { it.onDisconnect(code, reason) }

        //If the server closes the connection, state will be Connected here
        //If the client disconnects closed will be true
        if (shouldRetry(code, reason, State.Disconnected) && state == State.Connected) {
            state = State.Disconnected//We are not disconnected
            doRetry()
        }

        state = State.Disconnected
        socket = null
    }

    private fun onFailure(exception: IOException, response: OkResponse?) {
        val res = Response.ConnectionError(this.request, exception)
        safeListeners.forEach { it.onError(exception, res) }
        state = State.Errored
        socket = null

        if (shouldRetry(-1, "WS-Error", state)) {
            doRetry()
        }
    }

    private fun onMessage(message: String) {
        safeListeners.forEach { it.messageReceived(message) }
    }

    private fun doRetry() {
        if (currentRetry == 0) {
            return
        }

        //Backoff using Thread.sleep
        //Backs off 2^(#ofAttempts) seconds
        Thread.sleep(Math.pow(2.0, (retryCount - currentRetry).toDouble()).toLong() * 1000L)

        currentRetry--

        connect()
    }

    open fun shouldRetry(code: Int, reason: String?, state: State): Boolean {
        return (state == State.Errored || code !in listOf(1000, 1004, 1010)) && retryCount == WebSocket.noRetry
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

    companion object {

        val infiniteRetry = -1
        val noRetry = 0

    }

}