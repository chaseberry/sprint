package edu.csh.chase.sprint

import com.squareup.okhttp.Headers
import com.squareup.okhttp.OkHttpClient
import edu.csh.chase.sprint.parameters.UrlBody

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

    fun get(endpoint: String, urlParameters: UrlBody?, headers: Headers.Builder, listeners: SprintListener): RequestProcessor {

    }

    fun get(request: Request): RequestProcessor {
        return RequestProcessor(configureRequest(request), client).executeRequest()
    }


}