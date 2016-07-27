package edu.csh.chase.sprint

import edu.csh.chase.sprint.parameters.UrlParameters
import edu.csh.chase.sprint.websockets.BasicWebSocket
import edu.csh.chase.sprint.websockets.WebSocket
import edu.csh.chase.sprint.websockets.WebSocketCallbacks
import edu.csh.chase.sprint.websockets.WebSocketEvent
import okhttp3.Headers
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okio.Buffer
import java.io.IOException
import java.util.concurrent.TimeUnit

object Sprint {

    private val client: OkHttpClient by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        OkHttpClient.Builder()
                .connectTimeout(30L, TimeUnit.SECONDS)
                .writeTimeout(30L, TimeUnit.SECONDS)
                .readTimeout(30L, TimeUnit.SECONDS).build()
    }

    fun executeRequest(request: Request, requestFinished: ((Request, Response) -> Unit )): RequestProcessor {

        return executeRequest(request, object : SprintListener {
            override fun sprintSuccess(request: Request, response: Response) {
                requestFinished(request, response)
            }

            override fun sprintFailure(request: Request, response: Response) {
                requestFinished(request, response)
            }
        })
    }

    fun executeRequest(request: Request, sprintListener: SprintListener?): RequestProcessor {
        return RequestProcessor(request, client, sprintListener).executeRequest()
    }

    fun get(url: String, urlParameters: UrlParameters? = null, headers: Headers.Builder = Headers.Builder(),
            extraData: Any? = null, requestFinished: ((Request, Response) -> Unit )): RequestProcessor {

        return executeRequest(Request(
                url = url,
                requestType = RequestType.Get,
                urlParams = urlParameters,
                extraData = extraData,
                headers = headers),
                requestFinished)
    }

    fun get(url: String, urlParameters: UrlParameters? = null, headers: Headers.Builder = Headers.Builder(),
            extraData: Any? = null, requestFinished: SprintListener): RequestProcessor {

        return executeRequest(Request(
                url = url,
                requestType = RequestType.Get,
                urlParams = urlParameters,
                extraData = extraData,
                headers = headers),
                requestFinished)
    }

    fun post(url: String, urlParameters: UrlParameters ? = null, headers: Headers.Builder = Headers.Builder(),
             body: RequestBody? = null, extraData: Any ? = null, requestFinished: ((Request, Response) -> Unit )):
            RequestProcessor {

        return executeRequest(Request(
                url = url,
                requestType = RequestType.Post,
                urlParams = urlParameters,
                headers = headers,
                body = body,
                extraData = extraData),
                requestFinished)
    }

    fun post(url: String, urlParameters: UrlParameters ? = null, headers: Headers.Builder = Headers.Builder(),
             body: RequestBody? = null, extraData: Any ? = null, requestFinished: SprintListener):
            RequestProcessor {

        return executeRequest(Request(
                url = url, requestType = RequestType.Post,
                urlParams = urlParameters,
                headers = headers,
                body = body,
                extraData = extraData),
                requestFinished)
    }

    fun put(url: String, urlParameters: UrlParameters ? = null, headers: Headers.Builder = Headers.Builder(),
            body: RequestBody? = null, extraData: Any ? = null, requestFinished: ((Request, Response) -> Unit )):
            RequestProcessor {

        return executeRequest(Request(
                url = url,
                requestType = RequestType.Put,
                urlParams = urlParameters,
                headers = headers,
                body = body,
                extraData = extraData),
                requestFinished)
    }

    fun put(url: String, urlParameters: UrlParameters ? = null, headers: Headers.Builder = Headers.Builder(),
            body: RequestBody? = null, extraData: Any ? = null, requestFinished: SprintListener):
            RequestProcessor {

        return executeRequest(Request(
                url = url,
                requestType = RequestType.Put,
                urlParams = urlParameters,
                headers = headers,
                body = body,
                extraData = extraData),
                requestFinished)
    }

    fun delete(url: String, urlParameters: UrlParameters ? = null, headers: Headers.Builder = Headers.Builder(),
               body: RequestBody? = null, extraData: Any ? = null, requestFinished: ((Request, Response) -> Unit )):
            RequestProcessor {

        return executeRequest(Request(
                url = url,
                requestType = RequestType.Delete,
                urlParams = urlParameters,
                headers = headers,
                body = body,
                extraData = extraData),
                requestFinished)
    }

    fun delete(url: String, urlParameters: UrlParameters ? = null, headers: Headers.Builder = Headers.Builder(),
               body: RequestBody? = null, extraData: Any ? = null, requestFinished: SprintListener):
            RequestProcessor {

        return executeRequest(Request(
                url = url,
                requestType = RequestType.Delete,
                urlParams = urlParameters,
                headers = headers,
                body = body,
                extraData = extraData),
                requestFinished)
    }


    fun createWebSocket(url: String,
                        urlParameters: UrlParameters? = null,
                        headers: Headers.Builder = Headers.Builder(),
                        client: OkHttpClient = OkHttpClient.Builder().readTimeout(0L, TimeUnit.MILLISECONDS).build(),
                        retryCount: Int = 4,
                        extraData: Any? = null,
                        listener: (WebSocketEvent, Any?, Any?) -> Unit): WebSocket {

        return createWebSocket(url = url,
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

    fun createWebSocket(url: String,
                        urlParameters: UrlParameters? = null,
                        headers: Headers.Builder = Headers.Builder(),
                        client: OkHttpClient = OkHttpClient.Builder().readTimeout(0L, TimeUnit.MILLISECONDS).build(),
                        retryCount: Int = 4,
                        extraData: Any? = null,
                        onConnect: (Response) -> Unit,
                        onDisconnect: (Int, String?) -> Unit,
                        onError: (IOException, Response?) -> Unit,
                        onMessage: (response: Response) -> Unit,
                        onPong: ((Buffer?) -> Unit)?): WebSocket {

        return createWebSocket(
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

                    override fun messageReceived(response: Response) {
                        onMessage(response)
                    }

                    override fun pongReceived(payload: Buffer?) {
                        onPong?.invoke(payload)
                    }
                }
        )

    }

    fun createWebSocket(url: String,
                        urlParameters: UrlParameters? = null,
                        headers: Headers.Builder = Headers.Builder(),
                        client: OkHttpClient = OkHttpClient.Builder().readTimeout(0L, TimeUnit.MILLISECONDS).build(),
                        retryCount: Int = 4,
                        extraData: Any? = null,
                        callbacks: WebSocketCallbacks): WebSocket {
        return BasicWebSocket(GetRequest(url, urlParameters, headers, extraData), callbacks, client, retryCount)
    }


}