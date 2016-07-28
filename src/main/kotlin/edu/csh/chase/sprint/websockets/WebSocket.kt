package edu.csh.chase.sprint.websockets

import edu.csh.chase.sprint.GetRequest
import edu.csh.chase.sprint.Request
import edu.csh.chase.sprint.Response
import edu.csh.chase.sprint.parameters.UrlParameters
import okhttp3.Headers
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.Response as OkResponse
import okhttp3.ws.WebSocket as OkWebSocket
import okhttp3.ResponseBody
import okhttp3.ws.WebSocketCall
import okhttp3.ws.WebSocketListener
import okio.Buffer
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

abstract class WebSocket(protected val request: Request,
                         client: OkHttpClient,
                         val retryCount: Int = 4,
                         val retryOnServerClose: Boolean = false,
                         val autoConnect: Boolean = false) : WebSocketCallbacks {

    private val listeners: ArrayList<WebSocketCallbacks> = ArrayList()
    private var socket: OkWebSocket? = null
    private var currentRetry: Int = retryCount
    private var call: WebSocketCall? = null
    private val client: OkHttpClient

    private val listenerCallBacks = object : WebSocketListener {

        override fun onOpen(webSocket: OkWebSocket, response: OkResponse) {
            this@WebSocket.onOpen(webSocket, response)
        }

        override fun onPong(payload: Buffer?) {
            this@WebSocket.onPong(payload)
        }

        override fun onClose(code: Int, reason: String?) {
            this@WebSocket.onClose(code, reason)
        }

        override fun onFailure(e: IOException, response: OkResponse?) {
            this@WebSocket.onFailure(e, response)
        }

        override fun onMessage(message: ResponseBody?) {
            this@WebSocket.onMessage(message)
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
                client: OkHttpClient,
                urlParameters: UrlParameters? = null,
                headers: Headers.Builder = Headers.Builder(),
                extraData: Any? = null,
                retryCount: Int = 4,
                retryOnServerClose: Boolean = false,
                autoConnect: Boolean = true) : this(GetRequest(url, urlParameters, headers, extraData),
            client, retryCount, retryOnServerClose, autoConnect)

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
            socket!!.sendMessage(RequestBody.create(OkWebSocket.TEXT, text))
        } catch(e: IOException) {
            socket!!.close(WebSocketDisconnect.protocolError, e.message)
        } catch(e: IllegalArgumentException) {

        }
    }

    fun ping(payload: Buffer? = null) {
        if (state != State.Connected) {
            return
        }
        socket!!.sendPing(payload)
    }

    //TODO consider returning a boolean success/failure
    //Success for will attempt to connect
    //Failure for already connected/already attempting to connect
    fun connect() {
        if (state == State.Connected || state == State.Connecting) {
            //Already connected or attempting to connect
            return
        }
        state = State.Connecting
        call = WebSocketCall.create(client, request.okHttpRequest)
        call!!.enqueue(listenerCallBacks)
    }

    fun disconnect(code: Int, reason: String?) {
        if (state == State.Disconnected || state == State.Disconnecting || state == State.Errored) {
            //Already closed
            return
        }
        state = State.Disconnecting
        socket!!.close(code, reason)
        socket = null
    }

    private fun onOpen(webSocket: OkWebSocket, response: OkResponse) {
        state = State.Connected
        socket = webSocket
        currentRetry = retryCount //Reset the retry count as a new connection was established

        listeners.forEach {
            it.onConnect(Response(response))
        }
    }

    private fun onPong(payload: Buffer?) {
        listeners.forEach { it.pongReceived(payload) }
    }

    private fun onClose(code: Int, reason: String?) {
        listeners.forEach { it.onDisconnect(code, reason) }

        //If the server closes the connection, State will be Connected here
        //If the client disconnects closed will be true
        if (retryOnServerClose && state == State.Connected) {
            state = State.Disconnected//We are not disconnected
            doRetry()
        }

        state = State.Disconnected
        socket = null
    }

    private fun onFailure(exception: IOException, response: OkResponse?) {
        val res = response?.let { Response(it) }
        listeners.forEach { it.onError(exception, res) }
        state = State.Errored
        socket = null

        doRetry()
    }

    private fun onMessage(message: ResponseBody?) {
        val response = Response(200, message?.bytes(), null)
        listeners.forEach { it.messageReceived(response) }
    }

    private fun doRetry() {
        if (retryCount == noRetry || currentRetry == 0) {
            return
        }

        //Backoff using Thread.sleep
        //Backs off 2^(#ofAttempts) seconds
        Thread.sleep(Math.pow(2.0, (retryCount - currentRetry).toDouble()).toLong() * 1000L)

        currentRetry--

        connect()
    }

    fun addCallback(cb: WebSocketCallbacks) {
        listeners.add(cb)
    }

    fun removeCallback(cb: WebSocketCallbacks) {
        if (cb == this) {
            //Can't remove yourself from webSocket callbacks
            return
        }
        listeners.remove(cb)
    }

    companion object {

        val infinteRetry = -1
        val noRetry = 0

    }

}