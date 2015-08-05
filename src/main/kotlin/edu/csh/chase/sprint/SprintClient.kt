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

    fun executeRequest(request: Request, listener: SprintListener?, callConfigure: Boolean = true): RequestProcessor {
        return RequestProcessor(if (callConfigure) configureRequest(request) else request, client, listener).executeRequest()
    }

    fun executeRequest(request: Request, callConfigure: Boolean = true, listener: ((Request, Response) -> Unit)? = null):
            RequestProcessor {

        return executeRequest(
                request = request,
                callConfigure = callConfigure,
                listener = object : SprintListener {
                    override fun sprintSuccess(request: Request, response: Response) {
                        listener?.invoke(request, response)
                    }

                    override fun sprintFailure(request: Request, response: Response) {
                        listener?.invoke(request, response)
                    }
                }
        )
    }

    fun get(endpoint: String, urlParameters: UrlParameters? = null, headers: Headers.Builder? = null,
            extraData: Any? = null, listener: SprintListener? = null, callConfigure: Boolean = true): RequestProcessor {

        return executeRequest(GetRequest(
                url = buildEndpoint(urlBase ?: "", endpoint),
                urlParams = urlParameters,
                headers = headers,
                extraData = extraData),
                listener,
                callConfigure = callConfigure)
    }

    fun get(endpoint: String, urlParameters: UrlParameters? = null, headers: Headers.Builder? = null,
            extraData: Any? = null, callConfigure: Boolean = true, listener: ((Request, Response) -> Unit)? = null):
            RequestProcessor {

        return executeRequest(GetRequest(
                url = buildEndpoint(urlBase ?: "", endpoint),
                urlParams = urlParameters,
                headers = headers,
                extraData = extraData),
                listener = listener,
                callConfigure = callConfigure)
    }

    fun post(endpoint: String, urlParameters: UrlParameters? = null, headers: Headers.Builder? = null,
             body: Any? = null, serializer: RequestSerializer? = null, extraData: Any? = null,
             listener: SprintListener? = null, callConfigure: Boolean = true): RequestProcessor {

        return executeRequest(PostRequest(
                url = buildEndpoint(urlBase ?: "", endpoint),
                urlParams = urlParameters,
                headers = headers,
                extraData = extraData,
                body = serializeBody(serializer, body)),
                listener,
                callConfigure = callConfigure)
    }

    fun post(endpoint: String, urlParameters: UrlParameters? = null, headers: Headers.Builder? = null,
             serializer: RequestSerializer? = null, body: Any? = null, extraData: Any? = null,
             callConfigure: Boolean = true, listener: ((Request, Response) -> Unit)? = null): RequestProcessor {

        return executeRequest(PostRequest(
                url = buildEndpoint(urlBase ?: "", endpoint),
                urlParams = urlParameters,
                headers = headers,
                extraData = extraData,
                body = serializeBody(serializer, body)),
                listener = listener,
                callConfigure = callConfigure)
    }

    fun put(endpoint: String, urlParameters: UrlParameters? = null, headers: Headers.Builder? = null,
            body: Any? = null, serializer: RequestSerializer? = null, extraData: Any? = null,
            listener: SprintListener? = null, callConfigure: Boolean = true): RequestProcessor {

        return executeRequest(PutRequest(
                url = buildEndpoint(urlBase ?: "", endpoint),
                urlParams = urlParameters,
                headers = headers,
                extraData = extraData,
                body = serializeBody(serializer, body)),
                listener,
                callConfigure = callConfigure)
    }

    fun put(endpoint: String, urlParameters: UrlParameters? = null, headers: Headers.Builder? = null,
            serializer: RequestSerializer? = null, body: Any? = null, extraData: Any? = null,
            callConfigure: Boolean = true, listener: ((Request, Response) -> Unit)? = null): RequestProcessor {

        return executeRequest(PutRequest(
                url = buildEndpoint(urlBase ?: "", endpoint),
                urlParams = urlParameters,
                headers = headers,
                extraData = extraData,
                body = serializeBody(serializer, body)),
                listener = listener,
                callConfigure = callConfigure)
    }

    fun delete(endpoint: String, urlParameters: UrlParameters? = null, headers: Headers.Builder? = null,
               body: Any? = null, serializer: RequestSerializer? = null, extraData: Any? = null,
               listener: SprintListener? = null, callConfigure: Boolean = true): RequestProcessor {

        return executeRequest(DeleteRequest(
                url = buildEndpoint(urlBase ?: "", endpoint),
                urlParams = urlParameters,
                headers = headers,
                extraData = extraData,
                body = serializeBody(serializer, body)),
                listener,
                callConfigure = callConfigure)
    }

    fun delete(endpoint: String, urlParameters: UrlParameters? = null, headers: Headers.Builder? = null,
               serializer: RequestSerializer? = null, body: Any? = null, extraData: Any? = null,
               callConfigure: Boolean = true, listener: ((Request, Response) -> Unit)? = null): RequestProcessor {

        return executeRequest(DeleteRequest(
                url = buildEndpoint(urlBase ?: "", endpoint),
                urlParams = urlParameters,
                headers = headers,
                extraData = extraData,
                body = serializeBody(serializer, body)),
                listener = listener,
                callConfigure = callConfigure)
    }

}