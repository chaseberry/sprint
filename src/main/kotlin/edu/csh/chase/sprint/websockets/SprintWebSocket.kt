package edu.csh.chase.sprint.websockets

import edu.csh.chase.sprint.Request
import edu.csh.chase.sprint.RequestProcessor
import edu.csh.chase.sprint.RequestType
import edu.csh.chase.sprint.Response
import edu.csh.chase.sprint.parameters.UrlParameters
import okhttp3.Headers
import okhttp3.OkHttpClient
import okio.Buffer
import java.io.IOException

object SprintWebSocket {

    fun create(url: String,
               urlParameters: UrlParameters? = null,
               headers: Headers.Builder = Headers.Builder(),
               client: OkHttpClient = OkHttpClient(),
               retryCount: Int = 4,
               extraData: Any? = null,
               listener: (WebSocketEvent, Any?, Any?) -> Unit): WebSocket {

        return create(url = url,
                urlParameters = urlParameters,
                headers = headers,
                client = client,
                retryCount = retryCount,
                extraData = extraData,
                onConnect = { response -> listener(WebSocketEvent.Connect, response, null) },
                onDisconnect = { code, reason -> listener(WebSocketEvent.Disconnect, code, reason) },
                onError = { exception, response -> listener(WebSocketEvent.Error, exception, response) },
                onMessage = { response -> listener(WebSocketEvent.Message, response, null) },
                onPong = { payload -> listener(WebSocketEvent.Pong, payload, null) })
    }

    fun create(url: String,
               urlParameters: UrlParameters? = null,
               headers: Headers.Builder = Headers.Builder(),
               client: OkHttpClient = OkHttpClient(),
               retryCount: Int = 4,
               extraData: Any? = null,
               onConnect: (Response) -> Unit,
               onDisconnect: (Int, String?) -> Unit,
               onError: (IOException, Response?) -> Unit,
               onMessage: (response: Response) -> Unit,
               onPong: ((Buffer?) -> Unit)?): WebSocket {

        return create(
                url = url,
                urlParameters = urlParameters,
                headers = headers,
                client = client,
                retryCount = retryCount,
                extraData = extraData,
                callbacks = object : WebSocketCallbacks {

                    override fun onConnect(response: Response) {
                        onConnect(response)
                    }

                    override fun onDisconnect(disconnectCode: Int, reason: String?) {
                        onDisconnect(disconnectCode, reason)
                    }

                    override fun onError(exception: IOException, response: Response?) {
                        onError(exception, response)
                    }

                    override fun onMessage(response: Response) {
                        onMessage(response)
                    }

                    override fun onPong(payload: Buffer?) {
                        onPong?.invoke(payload)
                    }

                }
        )

    }

    fun create(url: String,
               urlParameters: UrlParameters? = null,
               headers: Headers.Builder = Headers.Builder(),
               client: OkHttpClient = OkHttpClient(),
               retryCount: Int = 4,
               extraData: Any? = null,
               callbacks: WebSocketCallbacks): WebSocket {

    }

}