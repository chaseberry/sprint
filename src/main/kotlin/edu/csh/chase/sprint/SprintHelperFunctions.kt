package edu.csh.chase.sprint

import edu.csh.chase.kjson.JsonBase
import edu.csh.chase.sprint.parameters.JsonBody
import edu.csh.chase.sprint.websockets.*
import okhttp3.Headers
import okhttp3.RequestBody
import okio.Buffer
import java.io.IOException
import java.net.URL

fun headers(vararg headers: Pair<String, String>): Headers.Builder {
    val builder = Headers.Builder()
    headers.forEach { builder.add(it.first, it.second) }
    return builder
}

fun buildEndpoint(urlBase: String, endpoint: String): String {
    return URL(URL(urlBase), endpoint).toString()
}

val JsonBase.toRequestBody: RequestBody
    get() {
        return JsonBody(this)
    }

fun ((WebSocketEvent) -> Unit).toWebSocketCallback(): WebSocketCallbacks {
    return object : WebSocketCallbacks {
        override fun onConnect(response: Response) {
            this@toWebSocketCallback(ConnectEvent(response))
        }

        override fun onDisconnect(disconnectCode: Int, reason: String?) {
            this@toWebSocketCallback(DisconnectEvent(disconnectCode, reason))
        }

        override fun onError(exception: IOException, response: Response?) {
            this@toWebSocketCallback(ErrorEvent(exception, response))
        }

        override fun messageReceived(response: Response) {
            this@toWebSocketCallback(MessageEvent(response))
        }

        override fun pongReceived(payload: Buffer?) {
            this@toWebSocketCallback(PongEvent(payload))
        }
    }
}