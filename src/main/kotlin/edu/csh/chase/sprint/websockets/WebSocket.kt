package edu.csh.chase.sprint.websockets

import edu.csh.chase.sprint.Request
import edu.csh.chase.sprint.Response
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

abstract class WebSocket(protected val request: Request,
                         private val client: OkHttpClient,
                         val retryCount: Int = 4,
                         val retryOnServerClose: Boolean = false,
                         val autoConnect: Boolean = true) : WebSocketListener, WebSocketCallbacks {

    private val listeners: ArrayList<WebSocketCallbacks> = ArrayList()
    private var socket: OkWebSocket? = null
    private var currentRetry: Int = retryCount
    private var call: WebSocketCall? = null

    val closed: Boolean
        get() = socket == null

    init {
        listeners.add(this)

        if (autoConnect) {
            connect()
        }
    }

    fun sendText(text: String) {
        if (closed) {
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
        if (closed) {
            return
        }
        socket!!.sendPing(payload)
    }

    fun connect() {
        if (!closed) {
            //Already connected
            return
        }

        call = WebSocketCall.create(client, request.okHttpRequest)
        call!!.enqueue(this)

    }

    fun disconnect(code: Int, reason: String?) {
        if (closed) {
            //Already closed
            return
        }
        socket!!.close(code, reason)
        socket = null
    }

    final override fun onOpen(webSocket: OkWebSocket, response: OkResponse) {
        socket = webSocket
        currentRetry = retryCount //Reset the retry count as a new connection was established
        listeners.forEach {
            it.onConnect(Response(response))
        }
    }

    final override fun onPong(payload: Buffer?) {
        listeners.forEach { it.pongReceived(payload) }
    }

    final override fun onClose(code: Int, reason: String?) {
        listeners.forEach { it.onDisconnect(code, reason) }
        socket = null
        if (retryOnServerClose) {
            doRetry()
        }
    }

    final override fun onFailure(exception: IOException, response: OkResponse?) {
        val res = response?.let { Response(it) }
        listeners.forEach { it.onError(exception, res) }
        socket = null

        doRetry()
    }

    final override fun onMessage(message: ResponseBody?) {
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
        listeners.remove(cb)
    }

    companion object {

        val infinteRetry = -1
        val noRetry = 0

    }

}