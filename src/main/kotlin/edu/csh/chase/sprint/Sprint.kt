package edu.csh.chase.sprint

import edu.csh.chase.sprint.parameters.UrlParameters
import edu.csh.chase.sprint.websockets.*
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

    fun executeRequest(request: Request, requestFinished: ((Request, Response) -> Unit)): RequestProcessor {

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
            extraData: Any? = null, requestFinished: ((Request, Response) -> Unit)): RequestProcessor {

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

    fun post(url: String, urlParameters: UrlParameters? = null, headers: Headers.Builder = Headers.Builder(),
             body: RequestBody? = null, extraData: Any? = null, requestFinished: ((Request, Response) -> Unit)):
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

    fun post(url: String, urlParameters: UrlParameters? = null, headers: Headers.Builder = Headers.Builder(),
             body: RequestBody? = null, extraData: Any? = null, requestFinished: SprintListener):
        RequestProcessor {

        return executeRequest(Request(
            url = url, requestType = RequestType.Post,
            urlParams = urlParameters,
            headers = headers,
            body = body,
            extraData = extraData),
            requestFinished)
    }

    fun put(url: String, urlParameters: UrlParameters? = null, headers: Headers.Builder = Headers.Builder(),
            body: RequestBody? = null, extraData: Any? = null, requestFinished: ((Request, Response) -> Unit)):
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

    fun put(url: String, urlParameters: UrlParameters? = null, headers: Headers.Builder = Headers.Builder(),
            body: RequestBody? = null, extraData: Any? = null, requestFinished: SprintListener):
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

    fun delete(url: String, urlParameters: UrlParameters? = null, headers: Headers.Builder = Headers.Builder(),
               body: RequestBody? = null, extraData: Any? = null, requestFinished: ((Request, Response) -> Unit)):
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

    fun delete(url: String, urlParameters: UrlParameters? = null, headers: Headers.Builder = Headers.Builder(),
               body: RequestBody? = null, extraData: Any? = null, requestFinished: SprintListener):
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
                        client: OkHttpClient = webSocketClient,
                        retryCount: Int = 4,
                        extraData: Any? = null,
                        listener: (WebSocketEvent) -> Unit): WebSocket {

        return createWebSocket(url = url,
            urlParameters = urlParameters,
            headers = headers,
            client = client,
            retryCount = retryCount,
            extraData = extraData,
            onConnect = { response -> listener(ConnectEvent(response)) },
            onDisconnect = { code, reason -> listener(DisconnectEvent(code, reason)) },
            onError = { exception, response -> listener(ErrorEvent(exception, response)) },
            onPong = { payload -> listener(PongEvent(payload)) },
            onMessage = { response -> listener(MessageEvent(response)) })
    }

    fun createWebSocket(url: String,
                        urlParameters: UrlParameters? = null,
                        headers: Headers.Builder = Headers.Builder(),
                        client: OkHttpClient = webSocketClient,
                        retryCount: Int = 4,
                        extraData: Any? = null,
                        onConnect: ((Response) -> Unit)? = null,
                        onDisconnect: ((Int, String?) -> Unit)? = null,
                        onError: ((IOException, Response?) -> Unit)? = null,
                        onPong: ((Buffer?) -> Unit)? = null,
                        onMessage: ((response: Response) -> Unit)? = null): WebSocket {

        return createWebSocket(
            url = url,
            urlParameters = urlParameters,
            headers = headers,
            client = client,
            retryCount = retryCount,
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

                override fun messageReceived(response: Response) {
                    onMessage?.invoke(response)
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
                        retryCount: Int = 4,
                        extraData: Any? = null,
                        callbacks: WebSocketCallbacks): WebSocket {
        return BasicWebSocket(GetRequest(url, urlParameters, headers, extraData), callbacks, client, retryCount)
    }


}