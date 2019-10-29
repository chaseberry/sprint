package edu.csh.chase.sprint

import edu.csh.chase.sprint.parameters.UrlParameters
import okhttp3.Headers
import okhttp3.OkHttpClient
import okhttp3.RequestBody

abstract class SprintClient(val urlBase: String) {

    private val client = OkHttpClient.Builder().let { configureClient(it); it.build() }

    abstract fun configureClient(client: OkHttpClient.Builder)

    abstract val defaultRequestSerializer: RequestSerializer

    open val defaultBackoffTimeout: BackoffTimeout
        get() = BackoffTimeout.Exponential(500, 2, 300000L, 6)

    open fun configureRequest(request: Request) {

    }

    private fun serializeBody(serializer: RequestSerializer?, body: Any?): RequestBody? {
        return if (serializer != null && serializer.isValidType(body)) {
            serializer.serialize(body)
        } else {
            defaultRequestSerializer.serialize(body)
        }
    }

    fun executeRequest(request: Request): ResponseFuture {
        configureRequest(request)
        return ResponseFuture(request, client, null, defaultBackoffTimeout)
    }

    fun executeRequest(request: Request, listener: SprintListener?): ResponseFuture {
        configureRequest(request)
        return ResponseFuture(request, client, listener, defaultBackoffTimeout)
    }

    fun executeRequest(request: Request, listener: RequestFinished?):
        ResponseFuture {

        return executeRequest(
            request = request,
            listener = object : SprintListener {
                override fun sprintFailure(response: Response.Failure) {
                    listener?.invoke(response)
                }

                override fun sprintSuccess(response: Response.Success) {
                    listener?.invoke(response)
                }

                override fun sprintConnectionError(response: Response.ConnectionError) {
                    listener?.invoke(response)
                }
            }
        )
    }

    fun get(endpoint: String, urlParameters: UrlParameters? = null, headers: Headers.Builder = Headers.Builder(),
            extraData: Any? = null): ResponseFuture {

        return executeRequest(
            getRequest(
                url = buildEndpoint(urlBase, endpoint),
                urlParams = urlParameters,
                headers = headers,
                extraData = extraData
            )
        )
    }

    fun get(endpoint: String, urlParameters: UrlParameters? = null, headers: Headers.Builder = Headers.Builder(),
            extraData: Any? = null, listener: SprintListener?): ResponseFuture {

        return executeRequest(getRequest(
            url = buildEndpoint(urlBase, endpoint),
            urlParams = urlParameters,
            headers = headers,
            extraData = extraData),
            listener = listener)
    }

    fun get(endpoint: String, urlParameters: UrlParameters? = null, headers: Headers.Builder = Headers.Builder(),
            extraData: Any? = null, listener: RequestFinished?):
        ResponseFuture {

        return executeRequest(getRequest(
            url = buildEndpoint(urlBase, endpoint),
            urlParams = urlParameters,
            headers = headers,
            extraData = extraData),
            listener = listener)
    }

    fun post(endpoint: String, urlParameters: UrlParameters? = null, headers: Headers.Builder = Headers.Builder(),
             body: Any? = null, serializer: RequestSerializer? = null, extraData: Any? = null): ResponseFuture {

        return executeRequest(
            postRequest(
                url = buildEndpoint(urlBase, endpoint),
                urlParams = urlParameters,
                headers = headers,
                extraData = extraData,
                body = serializeBody(serializer, body)
            )
        )
    }

    fun post(endpoint: String, urlParameters: UrlParameters? = null, headers: Headers.Builder = Headers.Builder(),
             body: Any? = null, serializer: RequestSerializer? = null, extraData: Any? = null,
             listener: SprintListener? = null): ResponseFuture {

        return executeRequest(postRequest(
            url = buildEndpoint(urlBase, endpoint),
            urlParams = urlParameters,
            headers = headers,
            extraData = extraData,
            body = serializeBody(serializer, body)),
            listener)
    }

    fun post(endpoint: String, urlParameters: UrlParameters? = null, headers: Headers.Builder = Headers.Builder(),
             serializer: RequestSerializer? = null, body: Any? = null, extraData: Any? = null,
             listener: RequestFinished? = null): ResponseFuture {

        return executeRequest(postRequest(
            url = buildEndpoint(urlBase, endpoint),
            urlParams = urlParameters,
            headers = headers,
            extraData = extraData,
            body = serializeBody(serializer, body)),
            listener = listener)
    }

    fun put(endpoint: String, urlParameters: UrlParameters? = null, headers: Headers.Builder = Headers.Builder(),
            body: Any? = null, serializer: RequestSerializer? = null, extraData: Any? = null): ResponseFuture {

        return executeRequest(
            putRequest(
                url = buildEndpoint(urlBase, endpoint),
                urlParams = urlParameters,
                headers = headers,
                extraData = extraData,
                body = serializeBody(serializer, body)
            )
        )
    }

    fun put(endpoint: String, urlParameters: UrlParameters? = null, headers: Headers.Builder = Headers.Builder(),
            body: Any? = null, serializer: RequestSerializer? = null, extraData: Any? = null,
            listener: SprintListener? = null): ResponseFuture {

        return executeRequest(putRequest(
            url = buildEndpoint(urlBase, endpoint),
            urlParams = urlParameters,
            headers = headers,
            extraData = extraData,
            body = serializeBody(serializer, body)),
            listener)
    }

    fun put(endpoint: String, urlParameters: UrlParameters? = null, headers: Headers.Builder = Headers.Builder(),
            serializer: RequestSerializer? = null, body: Any? = null, extraData: Any? = null,
            listener: RequestFinished? = null): ResponseFuture {

        return executeRequest(putRequest(
            url = buildEndpoint(urlBase, endpoint),
            urlParams = urlParameters,
            headers = headers,
            extraData = extraData,
            body = serializeBody(serializer, body)),
            listener = listener)
    }

    fun delete(endpoint: String, urlParameters: UrlParameters? = null, headers: Headers.Builder = Headers.Builder(),
               body: Any? = null, serializer: RequestSerializer? = null, extraData: Any? = null): ResponseFuture {

        return executeRequest(
            deleteRequest(
                url = buildEndpoint(urlBase, endpoint),
                urlParams = urlParameters,
                headers = headers,
                extraData = extraData,
                body = serializeBody(serializer, body)
            )
        )
    }

    fun delete(endpoint: String, urlParameters: UrlParameters? = null, headers: Headers.Builder = Headers.Builder(),
               body: Any? = null, serializer: RequestSerializer? = null, extraData: Any? = null,
               listener: SprintListener? = null): ResponseFuture {

        return executeRequest(deleteRequest(
            url = buildEndpoint(urlBase, endpoint),
            urlParams = urlParameters,
            headers = headers,
            extraData = extraData,
            body = serializeBody(serializer, body)),
            listener)
    }

    fun delete(endpoint: String, urlParameters: UrlParameters? = null, headers: Headers.Builder = Headers.Builder(),
               serializer: RequestSerializer? = null, body: Any? = null, extraData: Any? = null,
               listener: RequestFinished? = null): ResponseFuture {

        return executeRequest(deleteRequest(
            url = buildEndpoint(urlBase, endpoint),
            urlParams = urlParameters,
            headers = headers,
            extraData = extraData,
            body = serializeBody(serializer, body)),
            listener = listener)
    }

}