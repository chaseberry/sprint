package edu.csh.chase.sprint.websockets

import edu.csh.chase.sprint.*
import edu.csh.chase.sprint.parameters.UrlParameters
import okhttp3.Headers
import okhttp3.OkHttpClient
import okio.ByteString
import java.io.IOException

class BasicWebSocket(request: Request,
                     private val callbacks: WebSocketCallbacks,
                     client: OkHttpClient,
                     retries: BackoffTimeout = BackoffTimeout.Exponential(500, 2, 300000L, 5),
                     autoConnect: Boolean = false) : WebSocket(request, client, retries, autoConnect) {

    constructor(url: String,
                client: OkHttpClient = Sprint.webSocketClient,
                callbacks: WebSocketCallbacks,
                urlParameters: UrlParameters? = null,
                headers: Headers.Builder = Headers.Builder(),
                extraData: Any? = null,
                retries: BackoffTimeout = BackoffTimeout.Exponential(500, 2, 300000L, 5),
                autoConnect: Boolean = false) : this(getRequest(url, urlParameters, headers, extraData), callbacks,
        client, retries, autoConnect)

    override fun onConnect(response: Response) {
        callbacks.onConnect(response)
    }

    override fun onDisconnect(disconnectCode: Int, reason: String?) {
        callbacks.onDisconnect(disconnectCode, reason)
    }

    override fun onError(exception: IOException, response: Response?) {
        callbacks.onError(exception, response)
    }

    override fun messageReceived(message: String) {
        callbacks.messageReceived(message)
    }

    override fun messageReceived(message: ByteString) {
        callbacks.messageReceived(message)
    }
}