package edu.csh.chase.sprint.websockets

import edu.csh.chase.sprint.Request
import edu.csh.chase.sprint.Response
import okhttp3.RequestBody
import okhttp3.Response as OkResponse
import okhttp3.ResponseBody
import okhttp3.ws.WebSocket
import okhttp3.ws.WebSocketListener
import okio.Buffer
import java.io.IOException
import java.util.*
import okhttp3.ws.WebSocket as OkWebSocket

class WebSocket(protected val request: Request, callback: WebSocketCallbacks?, val retryCount: Int, val retryOnServerClose: Boolean) : WebSocketListener {

    private val listeners: ArrayList<WebSocketCallbacks> = ArrayList<WebSocketCallbacks>()
    private var socket: OkWebSocket? = null

    private var currentRetry: Int = retryCount

    init {
        callback?.let { listeners.add(it) }
    }

    fun sendText(text: String) {
        try {
            socket?.sendMessage(RequestBody.create(OkWebSocket.TEXT, text))
        } catch(e: IOException) {
            socket?.close(WebSocketDisconnect.protocolError, e.message)
        } catch(e: IllegalArgumentException) {

        }
    }

    fun sendPing(payload: Buffer?) {

    }

    fun close(code: Int, reason: String?) {

    }

    override fun onOpen(webSocket: OkWebSocket, response: OkResponse) {
        socket = webSocket
        listeners.forEach {
            it.onConnect(Response(response))
        }
    }

    override fun onPong(payload: Buffer?) {
        listeners.forEach { it.onPong(payload) }
    }

    override fun onClose(code: Int, reason: String?) {
        listeners.forEach { it.onDisconnect(code, reason) }
    }

    override fun onFailure(exception: IOException, response: OkResponse?) {
        listeners.forEach { it.onError(exception, response?.let { Response(it) }) }
        doRetry()
    }

    override fun onMessage(message: ResponseBody?) {
        listeners.forEach { it.onMessage(Response(200, message?.bytes(), null)) }
    }

    private fun doRetry() {

    }

    companion object {

        val infinteRetry = -1
        val noRetry = 0

    }

}