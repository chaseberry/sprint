package edu.csh.chase.sprint.websockets

import edu.csh.chase.sprint.Request
import edu.csh.chase.sprint.Response
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.Response as OkResponse
import okhttp3.ResponseBody
import okhttp3.ws.WebSocketCall
import okhttp3.ws.WebSocketListener
import okio.Buffer
import java.io.IOException
import java.util.*
import okhttp3.ws.WebSocket as OkWebSocket

open class WebSocket(protected val request: Request,
                     callback: WebSocketCallbacks?,
                     private val client: OkHttpClient,
                     val retryCount: Int = 4,
                     val retryOnServerClose: Boolean = false,
                     val autoConnect: Boolean = true) : WebSocketListener {

    private val listeners: ArrayList<WebSocketCallbacks> = ArrayList<WebSocketCallbacks>()
    private var socket: OkWebSocket? = null
    private var currentRetry: Int = retryCount
    private var call: WebSocketCall? = null
    val closed: Boolean
        get() = socket == null

    init {
        callback?.let { listeners.add(it) }
        if (autoConnect) {
            connect()
        }
    }

    fun sendText(text: String) {
        if (closed) {
            return
        }

        try {
            socket?.sendMessage(RequestBody.create(OkWebSocket.TEXT, text))
        } catch(e: IOException) {
            socket?.close(WebSocketDisconnect.protocolError, e.message)
        } catch(e: IllegalArgumentException) {

        }
    }

    fun sendPing(payload: Buffer?) {
        if (closed) {
            return
        }
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

    override fun onOpen(webSocket: OkWebSocket, response: OkResponse) {
        socket = webSocket
        currentRetry = retryCount //Reset the retry count as a new connection was established
        listeners.forEach {
            it.onConnect(Response(response))
        }
    }

    override fun onPong(payload: Buffer?) {
        listeners.forEach { it.onPong(payload) }
    }

    override fun onClose(code: Int, reason: String?) {
        listeners.forEach { it.onDisconnect(code, reason) }
        socket = null
        if (retryOnServerClose) {
            doRetry()
        }
    }

    override fun onFailure(exception: IOException, response: OkResponse?) {
        val res = response?.let { Response(it) }
        listeners.forEach { it.onError(exception, res) }
        socket = null

        doRetry()
    }

    override fun onMessage(message: ResponseBody?) {
        val response = Response(200, message?.bytes(), null)
        listeners.forEach { it.onMessage(response) }
    }

    private fun doRetry() {
        if (retryCount == noRetry || currentRetry == 0) {
            return
        }

        connect()
    }

    companion object {

        val infinteRetry = -1
        val noRetry = 0

    }

}