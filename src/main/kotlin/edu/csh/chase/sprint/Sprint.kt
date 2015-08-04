package edu.csh.chase.sprint

import com.squareup.okhttp.Headers
import com.squareup.okhttp.OkHttpClient
import edu.csh.chase.sprint.parameters.UrlBody
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

    public fun get(url: String, urlParameters: UrlBody?, headers: Headers.Builder,
                   requestFinished: ((Request, Response) -> Unit)): RequestProcessor {
        return get(Request(url = url, requestType = RequestType.Get, urlParams = urlParameters, headers = headers),
                object : SprintSuccess {
                    override fun sprintSuccess(request: Request, response: Response) {
                        requestFinished(request, response)

                    }
                },
                object : SprintFailure {
                    override fun sprintFailure(request: Request, response: Response) {
                        requestFinished(request, response)

                    }

                })
    }

    public fun get(request: Request, successListener: SprintSuccess?, failureListener: SprintFailure): RequestProcessor {
        val processor = RequestProcessor(request, client, successListener, failureListener)
        processor.executeRequest()
        return processor
    }

}