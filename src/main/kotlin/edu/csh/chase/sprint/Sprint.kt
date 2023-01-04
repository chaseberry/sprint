package edu.csh.chase.sprint

import edu.csh.chase.sprint.parameters.UrlParameters
import edu.csh.chase.sprint.websockets.BasicWebSocket
import edu.csh.chase.sprint.websockets.WebSocket
import edu.csh.chase.sprint.websockets.WebSocketCallbacks
import edu.csh.chase.sprint.websockets.WebSocketEvent
import okhttp3.Headers
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okio.ByteString
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

    private fun wrap(requestFinished: RequestFinished): SprintListener = object : SprintListener {
        override fun sprintFailure(response: Response.Failure) { requestFinished(response) }
        override fun sprintSuccess(response: Response.Success) { requestFinished(response) }
        override fun sprintConnectionError(response: Response.ConnectionError) { requestFinished(response) }
    }

    fun executeRequest(request: Request, retries: BackoffTimeout, requestFinished: RequestFinished): ResponseFuture {
        return executeRequest(request, retries, wrap(requestFinished))
    }

    fun executeRequest(request: Request, retries: BackoffTimeout, sprintListener: SprintListener? = null): ResponseFuture {
        return ResponseFuture(request, client, sprintListener, retries)
    }

    fun get(url: String,
            urlParameters: UrlParameters? = null,
            headers: Headers.Builder = Headers.Builder(),
            extraData: Any? = null,
            retries: BackoffTimeout = BackoffTimeout.NoRetry()): ResponseFuture {

        return executeRequest(
            Request(
                url = url,
                requestType = RequestType.Get,
                urlParams = urlParameters,
                extraData = extraData,
                headers = headers,
            ),
            retries
        )
    }

    fun get(url: String,
            urlParameters: UrlParameters? = null,
            headers: Headers.Builder = Headers.Builder(),
            extraData: Any? = null,
            retries: BackoffTimeout = BackoffTimeout.NoRetry(),
            requestFinished: RequestFinished): ResponseFuture {

        return executeRequest(
            Request(
                url = url,
                requestType = RequestType.Get,
                urlParams = urlParameters,
                extraData = extraData,
                headers = headers
            ),
            retries,
            requestFinished,
        )
    }

    fun get(url: String,
            urlParameters: UrlParameters? = null,
            headers: Headers.Builder = Headers.Builder(),
            extraData: Any? = null,
            retries: BackoffTimeout = BackoffTimeout.NoRetry(),
            requestFinished: SprintListener): ResponseFuture {

        return executeRequest(
            Request(
                url = url,
                requestType = RequestType.Get,
                urlParams = urlParameters,
                extraData = extraData,
                headers = headers
            ),
            retries,
            requestFinished
        )
    }

    fun post(url: String,
             urlParameters: UrlParameters? = null,
             headers: Headers.Builder = Headers.Builder(),
             body: RequestBody? = null,
             retries: BackoffTimeout = BackoffTimeout.NoRetry(),
             extraData: Any? = null): ResponseFuture {

        return executeRequest(
            Request(
                url = url,
                requestType = RequestType.Post,
                urlParams = urlParameters,
                headers = headers,
                body = body,
                extraData = extraData
            ),
            retries
        )
    }

    fun post(url: String,
             urlParameters: UrlParameters? = null,
             headers: Headers.Builder = Headers.Builder(),
             body: RequestBody? = null,
             retries: BackoffTimeout = BackoffTimeout.NoRetry(),
             extraData: Any? = null, requestFinished: RequestFinished):
        ResponseFuture {

        return executeRequest(
            Request(
                url = url,
                requestType = RequestType.Post,
                urlParams = urlParameters,
                headers = headers,
                body = body,
                extraData = extraData
            ),
            retries,
            requestFinished
        )
    }

    fun post(url: String,
             urlParameters: UrlParameters? = null,
             headers: Headers.Builder = Headers.Builder(),
             body: RequestBody? = null,
             retries: BackoffTimeout = BackoffTimeout.NoRetry(),
             extraData: Any? = null, requestFinished: SprintListener):
        ResponseFuture {

        return executeRequest(
            Request(
                url = url, requestType = RequestType.Post,
                urlParams = urlParameters,
                headers = headers,
                body = body,
                extraData = extraData
            ),
            retries,
            requestFinished,
        )
    }

    fun put(url: String,
            urlParameters: UrlParameters? = null,
            headers: Headers.Builder = Headers.Builder(),
            body: RequestBody? = null,
            retries: BackoffTimeout = BackoffTimeout.NoRetry(),
            extraData: Any? = null): ResponseFuture {

        return executeRequest(
            Request(
                url = url,
                requestType = RequestType.Put,
                urlParams = urlParameters,
                extraData = extraData,
                body = body,
                headers = headers
            ),
            retries
        )
    }

    fun put(url: String,
            urlParameters: UrlParameters? = null,
            headers: Headers.Builder = Headers.Builder(),
            body: RequestBody? = null,
            retries: BackoffTimeout = BackoffTimeout.NoRetry(),
            extraData: Any? = null, requestFinished: RequestFinished):
        ResponseFuture {

        return executeRequest(
            Request(
                url = url,
                requestType = RequestType.Put,
                urlParams = urlParameters,
                headers = headers,
                body = body,
                extraData = extraData
            ),
            retries,
            requestFinished
        )
    }

    fun put(url: String,
            urlParameters: UrlParameters? = null,
            headers: Headers.Builder = Headers.Builder(),
            body: RequestBody? = null,
            retries: BackoffTimeout = BackoffTimeout.NoRetry(),
            extraData: Any? = null, requestFinished: SprintListener):
        ResponseFuture {

        return executeRequest(
            Request(
                url = url,
                requestType = RequestType.Put,
                urlParams = urlParameters,
                headers = headers,
                body = body,
                extraData = extraData
            ),
            retries,
            requestFinished,
        )
    }

    fun delete(url: String,
               urlParameters: UrlParameters? = null,
               headers: Headers.Builder = Headers.Builder(),
               body: RequestBody? = null,
               retries: BackoffTimeout = BackoffTimeout.NoRetry(),
               extraData: Any? = null): ResponseFuture {

        return executeRequest(
            Request(
                url = url,
                requestType = RequestType.Delete,
                urlParams = urlParameters,
                extraData = extraData,
                body = body,
                headers = headers
            ),
            retries
        )
    }

    fun delete(url: String,
               urlParameters: UrlParameters? = null,
               headers: Headers.Builder = Headers.Builder(),
               body: RequestBody? = null,
               extraData: Any? = null,
               retries: BackoffTimeout = BackoffTimeout.NoRetry(),
               requestFinished: RequestFinished):
        ResponseFuture {

        return executeRequest(
            Request(
                url = url,
                requestType = RequestType.Delete,
                urlParams = urlParameters,
                headers = headers,
                body = body,
                extraData = extraData
            ),
            retries,
            requestFinished,
        )
    }

    fun delete(url: String,
               urlParameters: UrlParameters? = null,
               headers: Headers.Builder = Headers.Builder(),
               body: RequestBody? = null,
               extraData: Any? = null,
               retries: BackoffTimeout = BackoffTimeout.NoRetry(),
               requestFinished: SprintListener):
        ResponseFuture {

        return executeRequest(
            Request(
                url = url,
                requestType = RequestType.Delete,
                urlParams = urlParameters,
                headers = headers,
                body = body,
                extraData = extraData
            ),
            retries,
            requestFinished,
        )
    }

    fun createWebSocket(url: String,
                        urlParameters: UrlParameters? = null,
                        headers: Headers.Builder = Headers.Builder(),
                        client: OkHttpClient = webSocketClient,
                        retries: BackoffTimeout = BackoffTimeout.Exponential(500, 2, 300000L, 5),
                        extraData: Any? = null,
                        listener: (WebSocketEvent) -> Unit): WebSocket {

        return createWebSocket(
            url = url,
            urlParameters = urlParameters,
            headers = headers,
            client = client,
            retries = retries,
            extraData = extraData,
            onConnect = { response -> listener(WebSocketEvent.Connect(response)) },
            onDisconnect = { code, reason -> listener(WebSocketEvent.Disconnect(code, reason)) },
            onError = { exception, response -> listener(WebSocketEvent.Error(exception, response)) },
            onMessage = { response -> listener(WebSocketEvent.Message(response)) },
            onByteMessage = { message -> listener(WebSocketEvent.ByteMessage(message)) }
        )
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
                        onMessage: ((String) -> Unit)? = null,
                        onByteMessage: ((ByteString) -> Unit)? = null): WebSocket {

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

                override fun messageReceived(message: ByteString) {
                    onByteMessage?.invoke(message)
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