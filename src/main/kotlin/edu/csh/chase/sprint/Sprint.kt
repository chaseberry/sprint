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
            .readTimeout(30L, TimeUnit.SECONDS)
            .pingInterval(10, TimeUnit.SECONDS).build()
    }

    val webSocketClient: OkHttpClient by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        OkHttpClient.Builder()
            .connectTimeout(30L, TimeUnit.SECONDS)
            .writeTimeout(30L, TimeUnit.SECONDS)
            .readTimeout(0L, TimeUnit.MILLISECONDS)
            .pingInterval(10, TimeUnit.SECONDS).build()
    }

    fun executeRequest(request: Request, requestFinished: RequestFinished): ResponseFuture {

        return executeRequest(request, object : SprintListener {

            override fun sprintFailure(response: Response.Failure) {
                requestFinished(response)
            }

            override fun sprintSuccess(response: Response.Success) {
                requestFinished(response)
            }

            override fun sprintConnectionError(response: Response.ConnectionError) {
                requestFinished(response)
            }
        })
    }

    fun executeRequest(request: Request): ResponseFuture {
        return ResponseFuture(request, client, null)
    }

    fun executeRequest(request: Request, sprintListener: SprintListener): ResponseFuture {
        return ResponseFuture(request, client, sprintListener)
    }

    fun get(url: String, urlParameters: UrlParameters? = null, headers: Headers.Builder = Headers.Builder(),
            extraData: Any? = null): ResponseFuture {

        return executeRequest(
            Request(
                url = url,
                requestType = RequestType.Get,
                urlParams = urlParameters,
                extraData = extraData,
                headers = headers
            )
        )
    }

    fun get(url: String, urlParameters: UrlParameters? = null, headers: Headers.Builder = Headers.Builder(),
            extraData: Any? = null, requestFinished: RequestFinished): ResponseFuture {

        return executeRequest(Request(
            url = url,
            requestType = RequestType.Get,
            urlParams = urlParameters,
            extraData = extraData,
            headers = headers),
            requestFinished)
    }

    fun get(url: String, urlParameters: UrlParameters? = null, headers: Headers.Builder = Headers.Builder(),
            extraData: Any? = null, requestFinished: SprintListener): ResponseFuture {

        return executeRequest(Request(
            url = url,
            requestType = RequestType.Get,
            urlParams = urlParameters,
            extraData = extraData,
            headers = headers),
            requestFinished)
    }

    fun post(url: String, urlParameters: UrlParameters? = null, headers: Headers.Builder = Headers.Builder(),
             body: RequestBody? = null, extraData: Any? = null): ResponseFuture {

        return executeRequest(
            Request(
                url = url,
                requestType = RequestType.Post,
                urlParams = urlParameters,
                headers = headers,
                body = body,
                extraData = extraData)
        )
    }

    fun post(url: String, urlParameters: UrlParameters? = null, headers: Headers.Builder = Headers.Builder(),
             body: RequestBody? = null, extraData: Any? = null, requestFinished: RequestFinished):
        ResponseFuture {

        return executeRequest(Request(
            url = url,
            requestType = RequestType.Post,
            urlParams = urlParameters,
            headers = headers,
            body = body,
            extraData = extraData),
            requestFinished)
    }

    fun post(url: String, urlParameters: UrlParameters? = null, headers: Headers.Builder = Headers.Builder(),
             body: RequestBody? = null, extraData: Any? = null, requestFinished: SprintListener):
        ResponseFuture {

        return executeRequest(Request(
            url = url, requestType = RequestType.Post,
            urlParams = urlParameters,
            headers = headers,
            body = body,
            extraData = extraData),
            requestFinished)
    }

    fun put(url: String, urlParameters: UrlParameters? = null, headers: Headers.Builder = Headers.Builder(),
            body: RequestBody? = null, extraData: Any? = null): ResponseFuture {

        return executeRequest(
            Request(
                url = url,
                requestType = RequestType.Put,
                urlParams = urlParameters,
                extraData = extraData,
                body = body,
                headers = headers
            )
        )
    }

    fun put(url: String, urlParameters: UrlParameters? = null, headers: Headers.Builder = Headers.Builder(),
            body: RequestBody? = null, extraData: Any? = null, requestFinished: RequestFinished):
        ResponseFuture {

        return executeRequest(Request(
            url = url,
            requestType = RequestType.Put,
            urlParams = urlParameters,
            headers = headers,
            body = body,
            extraData = extraData),
            requestFinished)
    }

    fun put(url: String, urlParameters: UrlParameters? = null, headers: Headers.Builder = Headers.Builder(),
            body: RequestBody? = null, extraData: Any? = null, requestFinished: SprintListener):
        ResponseFuture {

        return executeRequest(Request(
            url = url,
            requestType = RequestType.Put,
            urlParams = urlParameters,
            headers = headers,
            body = body,
            extraData = extraData),
            requestFinished)
    }

    fun delete(url: String, urlParameters: UrlParameters? = null, headers: Headers.Builder = Headers.Builder(),
               body: RequestBody? = null, extraData: Any? = null): ResponseFuture {

        return executeRequest(
            Request(
                url = url,
                requestType = RequestType.Delete,
                urlParams = urlParameters,
                extraData = extraData,
                body = body,
                headers = headers
            )
        )
    }

    fun delete(url: String, urlParameters: UrlParameters? = null, headers: Headers.Builder = Headers.Builder(),
               body: RequestBody? = null, extraData: Any? = null, requestFinished: RequestFinished):
        ResponseFuture {

        return executeRequest(Request(
            url = url,
            requestType = RequestType.Delete,
            urlParams = urlParameters,
            headers = headers,
            body = body,
            extraData = extraData),
            requestFinished)
    }

    fun delete(url: String, urlParameters: UrlParameters? = null, headers: Headers.Builder = Headers.Builder(),
               body: RequestBody? = null, extraData: Any? = null, requestFinished: SprintListener):
        ResponseFuture {

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
                        client: OkHttpClient = webSocketClient,
                        retries: BackoffTimeout = BackoffTimeout.Exponential(500, 2, 300000L, 5),
                        extraData: Any? = null,
                        listener: (WebSocketEvent) -> Unit): WebSocket {

        return createWebSocket(url = url,
            urlParameters = urlParameters,
            headers = headers,
            client = client,
            retries = retries,
            extraData = extraData,
            onConnect = { response -> listener(WebSocketEvent.Connect(response)) },
            onDisconnect = { code, reason -> listener(WebSocketEvent.Disconnect(code, reason)) },
            onError = { exception, response -> listener(WebSocketEvent.Error(exception, response)) },
            onPong = { payload -> listener(WebSocketEvent.Pong(payload)) },
            onMessage = { response -> listener(WebSocketEvent.Message(response)) })
    }

    fun createWebSocket(url: String,
                        urlParameters: UrlParameters? = null,
                        headers: Headers.Builder = Headers.Builder(),
                        client: OkHttpClient = webSocketClient,
                        retries: BackoffTimeout = BackoffTimeout.Exponential(500, 2, 300000L, 5),
                        extraData: Any? = null,
                        onConnect: ((Response) -> Unit)? = null,
                        onDisconnect: ((Int, String?) -> Unit)? = null,
                        onError: ((IOException, Response?) -> Unit)? = null,
                        onPong: ((Buffer?) -> Unit)? = null,
                        onMessage: ((String) -> Unit)? = null): WebSocket {

        return createWebSocket(
            url = url,
            urlParameters = urlParameters,
            headers = headers,
            client = client,
            retries = retries,
            extraData = extraData,
            callbacks = object : WebSocketCallbacks {

                override fun onConnect(response: Response) {
                    onConnect?.invoke(response)
                }

                override fun onDisconnect(disconnectCode: Int, reason: String?) {
                    onDisconnect?.invoke(disconnectCode, reason)
                }

                override fun onError(exception: IOException, response: Response?) {
                    onError?.invoke(exception, response)
                }

                override fun messageReceived(message: String) {
                    onMessage?.invoke(message)
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
                        client: OkHttpClient = webSocketClient,
                        retries: BackoffTimeout = BackoffTimeout.Exponential(500, 2, 300000L, 5),
                        extraData: Any? = null,
                        callbacks: WebSocketCallbacks): WebSocket {
        return BasicWebSocket(getRequest(url, urlParameters, headers, extraData), callbacks, client, retries)
    }


}