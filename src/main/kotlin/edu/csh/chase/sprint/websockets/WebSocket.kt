package edu.csh.chase.sprint.websockets

import edu.csh.chase.sprint.Request
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.ws.WebSocket
import okhttp3.ws.WebSocketListener
import okio.Buffer
import java.io.IOException
import java.util.*
import okhttp3.ws.WebSocket as OkWebSocket

class WebSocket(protected val request: Request, private val webSocket: OkWebSocket, callback: WebSocketCallbacks?) : WebSocketListener {

    private val listeners = ArrayList<WebSocketCallbacks>()

    init {
        callback?.let { listeners.add(it) }
    }

    fun sendText(text: String) {
        try {
            webSocket.sendMessage(RequestBody.create(OkWebSocket.TEXT, text))
        } catch(e: IOException) {
            webSocket.close()
        } catch(e: IllegalArgumentException) {

        }
    }

    fun sendPing(payload: Buffer?) {

    }

    fun close(code: Int, reason: String?) {

    }

    override fun onOpen(webSocket: WebSocket?, response: Response?) {
        listeners.forEach {
            it.onConnect()
        }
    }

    override fun onPong(payload: Buffer?) {
        listeners.forEach { it.onPong() }
    }

    override fun onClose(code: Int, reason: String?) {
        listeners.forEach { it.onDisconnect() }
    }

    override fun onFailure(e: IOException?, response: Response?) {
        listeners.forEach { it.onError() }
    }

    override fun onMessage(message: ResponseBody?) {
    }


}