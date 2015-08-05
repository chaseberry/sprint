package edu.csh.chase.sprint

import com.squareup.okhttp.Headers
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.RequestBody
import edu.csh.chase.sprint.parameters.UrlParameters
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

public object Sprint {

    private val client: OkHttpClient by Delegates.lazy {
        val client = OkHttpClient()
        client.setConnectTimeout(30, TimeUnit.SECONDS)
        client.setWriteTimeout(30, TimeUnit.SECONDS)
        client.setReadTimeout(30, TimeUnit.SECONDS)
        client
    }

    public fun executeRequest(request: Request, requestFinished: ((Request, Response) -> Unit )): RequestProcessor {
        return executeRequest(request, object : SprintListener {
            override fun sprintSuccess(request: Request, response: Response) {
                requestFinished(request, response)
            }

            override fun sprintFailure(request: Request, response: Response) {
                requestFinished(request, response)
            }
        })
    }

    public fun executeRequest(request: Request, sprintListener: SprintListener?): RequestProcessor {
        return RequestProcessor(request, client, sprintListener).executeRequest()
    }

    public fun get(url: String, urlParameters: UrlParameters? = null, headers: Headers.Builder? = null,
                   extraData: Any? = null, requestFinished: ((Request, Response) -> Unit )): RequestProcessor {
        return executeRequest(Request(url = url, requestType = RequestType.Get, urlParams = urlParameters, headers = headers),
                requestFinished)
    }

    public fun get(url: String, urlParameters: UrlParameters? = null, headers: Headers.Builder? = null,
                   extraData: Any? = null, requestFinished: SprintListener): RequestProcessor {
        return executeRequest(Request(url = url, requestType = RequestType.Get, urlParams = urlParameters, headers = headers), requestFinished)
    }

    public fun post(url: String, urlParameters: UrlParameters ? = null, headers: Headers.Builder ? = null,
                    body: RequestBody? = null, extraData: Any ? = null, requestFinished: ((Request, Response) -> Unit )):
            RequestProcessor {
        return executeRequest(Request(url = url, requestType = RequestType.Post, urlParams = urlParameters, headers = headers,
                body = body, extraData = extraData), requestFinished)
    }

    public fun post(url: String, urlParameters: UrlParameters ? = null, headers: Headers.Builder ? = null,
                    body: RequestBody? = null, extraData: Any ? = null, requestFinished: SprintListener):
            RequestProcessor {
        return executeRequest(Request(url = url, requestType = RequestType.Post, urlParams = urlParameters, headers = headers,
                body = body, extraData = extraData), requestFinished)
    }

    public fun put(url: String, urlParameters: UrlParameters ? = null, headers: Headers.Builder ? = null,
                   body: RequestBody? = null, extraData: Any ? = null, requestFinished: ((Request, Response) -> Unit )):
            RequestProcessor {
        return executeRequest(Request(url = url, requestType = RequestType.Put, urlParams = urlParameters, headers = headers,
                body = body, extraData = extraData), requestFinished)
    }

    public fun put(url: String, urlParameters: UrlParameters ? = null, headers: Headers.Builder ? = null,
                   body: RequestBody? = null, extraData: Any ? = null, requestFinished: SprintListener):
            RequestProcessor {
        return executeRequest(Request(url = url, requestType = RequestType.Put, urlParams = urlParameters, headers = headers,
                body = body, extraData = extraData), requestFinished)
    }

}