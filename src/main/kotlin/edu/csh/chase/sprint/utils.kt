package edu.csh.chase.sprint

import edu.csh.chase.kjson.JsonBase
import edu.csh.chase.sprint.parameters.JsonBody
import edu.csh.chase.sprint.parameters.UrlParameters
import edu.csh.chase.sprint.websockets.WebSocketCallbacks
import edu.csh.chase.sprint.websockets.WebSocketEvent
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

val JsonBase.requestBody: RequestBody
    get() {
        return JsonBody(this)
    }

fun ((WebSocketEvent) -> Unit).toWebSocketCallback(): WebSocketCallbacks {
    return object : WebSocketCallbacks {
        override fun onConnect(response: Response) {
            this@toWebSocketCallback(WebSocketEvent.Connect(response))
        }

        override fun onDisconnect(disconnectCode: Int, reason: String?) {
            this@toWebSocketCallback(WebSocketEvent.Disconnect(disconnectCode, reason))
        }

        override fun onError(exception: IOException, response: Response?) {
            this@toWebSocketCallback(WebSocketEvent.Error(exception, response))
        }

        override fun messageReceived(message: String) {
            this@toWebSocketCallback(WebSocketEvent.Message(message))
        }

        override fun pongReceived(payload: Buffer?) {
            this@toWebSocketCallback(WebSocketEvent.Pong(payload))
        }
    }
}

typealias RequestFinished = (Response) -> Unit

fun getRequest(url: String,
               urlParams: UrlParameters? = null,
               headers: Headers.Builder = Headers.Builder(),
               extraData: Any? = null): Request {

    return Request(
        url = url,
        requestType = RequestType.Get,
        urlParams = urlParams,
        headers = headers,
        extraData = extraData
    )
}

fun postRequest(url: String,
                urlParams: UrlParameters? = null,
                headers: Headers.Builder = Headers.Builder(),
                body: RequestBody? = null,
                extraData: Any? = null): Request {

    return Request(
        url = url,
        requestType = RequestType.Post,
        urlParams = urlParams,
        headers = headers,
        body = body,
        extraData = extraData
    )
}

fun putRequest(url: String,
               urlParams: UrlParameters? = null,
               headers: Headers.Builder = Headers.Builder(),
               body: RequestBody? = null,
               extraData: Any? = null): Request {

    return Request(
        url = url,
        requestType = RequestType.Put,
        urlParams = urlParams,
        headers = headers,
        body = body,
        extraData = extraData
    )
}

fun deleteRequest(url: String,
                  urlParams: UrlParameters? = null,
                  headers: Headers.Builder = Headers.Builder(),
                  body: RequestBody? = null,
                  extraData: Any? = null): Request {

    return Request(
        url = url,
        requestType = RequestType.Delete,
        urlParams = urlParams,
        headers = headers,
        body = body,
        extraData = extraData
    )
}

inline fun queitly(block: () -> Unit) = try {
    block()
} catch (_: Exception) {
}