package edu.csh.chase.sprint.websockets

import edu.csh.chase.sprint.Request
import edu.csh.chase.sprint.internal.Processor
import okhttp3.OkHttpClient
import okhttp3.ws.WebSocketCall

class WebSocketProcessor(request: Request,
                         okClient: OkHttpClient,
                         private val callbacks: WebSocketCallbacks,
                         val retryCount: Int) : Processor(request, okClient) {

    fun execute(): WebSocket {

        val okRequest = buildOkRequest()

        val webSocket = WebSocket(request, callbacks, retryCount, false)

        WebSocketCall.create(client, okRequest).enqueue(webSocket)

        return webSocket
    }

}