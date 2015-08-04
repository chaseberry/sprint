package edu.csh.chase.sprint

import com.squareup.okhttp.Headers
import com.squareup.okhttp.OkHttpClient
import edu.csh.chase.sprint.parameters.UrlParameters

abstract class SprintClient(val urlBase: String? = null) {

    private val client = OkHttpClient()

    init {
        configureClient(client)
    }

    abstract fun configureClient(client: OkHttpClient)

    abstract fun defaultRequestSerializer()

    open fun configureRequest(request: Request): Request {
        return request
    }

    fun get(endpoint: String, urlParameters: UrlParameters? = null, headers: Headers.Builder? = null,
            listener: SprintListener? = null, extraData: Any? = null): RequestProcessor {
        
        return get(Request(url = buildEndpoint(urlBase, endpoint), requestType = RequestType.Get,
                urlParams = urlParameters, headers = headers, extraData = extraData), listener)
    }

    fun get(request: Request, listener: SprintListener?): RequestProcessor {
        return RequestProcessor(configureRequest(request), client, listener).executeRequest()
    }


}