package edu.csh.chase.sprint

import com.squareup.okhttp.Headers
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.RequestBody
import edu.csh.chase.sprint.parameters.UrlParameters

abstract class SprintClient(val urlBase: String? = null) {

    private val client = OkHttpClient()

    init {
        configureClient(client)
    }

    abstract fun configureClient(client: OkHttpClient)

    abstract val defaultRequestSerializer: RequestSerializer

    open fun configureRequest(request: Request): Request {
        return request
    }

    private fun serializeBody(serializer: RequestSerializer?, body: Any?): RequestBody? {
        return if (serializer != null && serializer.isValidType(body)) {
            serializer.serialize(body)
        } else {
            defaultRequestSerializer.serialize(body)
        }
    }

    fun get(endpoint: String, urlParameters: UrlParameters? = null, headers: Headers.Builder? = null,
            extraData: Any? = null, listener: SprintListener? = null): RequestProcessor {

        return get(Request(
                url = buildEndpoint(urlBase ?: "", endpoint),
                requestType = RequestType.Get,
                urlParams = urlParameters,
                headers = headers,
                extraData = extraData),
                listener)
    }

    fun get(request: Request, listener: SprintListener?): RequestProcessor {
        return RequestProcessor(configureRequest(request), client, listener).executeRequest()
    }

    fun post(endpoint: String, urlParameters: UrlParameters? = null, headers: Headers.Builder? = null,
             body: Any? = null, serializer: RequestSerializer? = null, extraData: Any? = null,
             listener: SprintListener? = null): RequestProcessor {

        return post(Request(
                url = buildEndpoint(urlBase ?: "", endpoint),
                urlParams = urlParameters,
                headers = headers,
                extraData = extraData,
                requestType = RequestType.Post,
                body = serializeBody(serializer, body)),
                listener)
    }

    fun post(request: Request, listener: SprintListener?): RequestProcessor {
        return RequestProcessor(request, client, listener).executeRequest()
    }

}