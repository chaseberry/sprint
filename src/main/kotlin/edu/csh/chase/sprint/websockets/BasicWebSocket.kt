package edu.csh.chase.sprint.websockets

import edu.csh.chase.sprint.GetRequest
import edu.csh.chase.sprint.Request
import edu.csh.chase.sprint.Response
import edu.csh.chase.sprint.Sprint
import edu.csh.chase.sprint.parameters.UrlParameters
import okhttp3.Headers
import okhttp3.OkHttpClient
import okio.Buffer
import java.io.IOException

class BasicWebSocket(request: Request,
                     private val callbacks: WebSocketCallbacks,
                     client: OkHttpClient,
                     retryCount: Int = 4,
                     autoConnect: Boolean = false) : WebSocket(request, client, retryCount, autoConnect) {

    constructor(url: String,
                client: OkHttpClient = Sprint.webSocketClient,
                callbacks: WebSocketCallbacks,
                urlParameters: UrlParameters? = null,
                headers: Headers.Builder = Headers.Builder(),
                extraData: Any? = null,
                retryCount: Int = 4,
                autoConnect: Boolean = false) : this(GetRequest(url, urlParameters, headers, extraData), callbacks,
        client, retryCount, autoConnect)

    override fun onConnect(response: Response) {
        callbacks.onConnect(response)
    }

    override fun onDisconnect(disconnectCode: Int, reason: String?) {
        callbacks.onDisconnect(disconnectCode, reason)
    }

    override fun onError(exception: IOException, response: Response?) {
        callbacks.onError(exception, response)
    }

    override fun messageReceived(response: Response) {
        callbacks.messageReceived(response)
    }

    override fun pongReceived(payload: Buffer?) {
        callbacks.pongReceived(payload)
    }
}