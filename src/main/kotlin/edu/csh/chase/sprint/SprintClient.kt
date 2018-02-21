package edu.csh.chase.sprint

import edu.csh.chase.sprint.parameters.UrlParameters
import okhttp3.Headers
import okhttp3.OkHttpClient
import okhttp3.RequestBody

abstract class SprintClient(val urlBase: String) {

    private val client = OkHttpClient.Builder().let { configureClient(it); it.build() }

    abstract fun configureClient(client: OkHttpClient.Builder)

    abstract val defaultRequestSerializer: RequestSerializer

    open fun configureRequest(request: Request) {

    }

    private fun serializeBody(serializer: RequestSerializer?, body: Any?): RequestBody? {
        return if (serializer != null && serializer.isValidType(body)) {
            serializer.serialize(body)
        } else {
            defaultRequestSerializer.serialize(body)
        }
    }

    fun executeRequest(request: Request, listener: SprintListener?): RequestProcessor {
        configureRequest(request)
        return RequestProcessor(request, client, listener).asyncExecute()
    }

    fun executeRequest(request: Request, listener: RequestFinished?):
        RequestProcessor {

        return executeRequest(
            request = request,
            listener = object : SprintListener {
                override fun sprintResponseReceived(response: Response.ServerResponse) {
                    listener?.invoke(response)
                }

                override fun springRequestError(response: Response.ConnectionError) {
                    listener?.invoke(response)
                }
            }
        )
    }

    fun get(endpoint: String, urlParameters: UrlParameters? = null, headers: Headers.Builder = Headers.Builder(),
            extraData: Any? = null, listener: SprintListener?): RequestProcessor {

        return executeRequest(GetRequest(
            url = buildEndpoint(urlBase, endpoint),
            urlParams = urlParameters,
            headers = headers,
            extraData = extraData),
            listener = listener)
    }

    fun get(endpoint: String, urlParameters: UrlParameters? = null, headers: Headers.Builder = Headers.Builder(),
            extraData: Any? = null, listener: RequestFinished?):
        RequestProcessor {

        return executeRequest(GetRequest(
            url = buildEndpoint(urlBase, endpoint),
            urlParams = urlParameters,
            headers = headers,
            extraData = extraData),
            listener = listener)
    }

    fun post(endpoint: String, urlParameters: UrlParameters? = null, headers: Headers.Builder = Headers.Builder(),
             body: Any? = null, serializer: RequestSerializer? = null, extraData: Any? = null,
             listener: SprintListener? = null): RequestProcessor {

        return executeRequest(PostRequest(
            url = buildEndpoint(urlBase, endpoint),
            urlParams = urlParameters,
            headers = headers,
            extraData = extraData,
            body = serializeBody(serializer, body)),
            listener)
    }

    fun post(endpoint: String, urlParameters: UrlParameters? = null, headers: Headers.Builder = Headers.Builder(),
             serializer: RequestSerializer? = null, body: Any? = null, extraData: Any? = null,
             listener: RequestFinished? = null): RequestProcessor {

        return executeRequest(PostRequest(
            url = buildEndpoint(urlBase, endpoint),
            urlParams = urlParameters,
            headers = headers,
            extraData = extraData,
            body = serializeBody(serializer, body)),
            listener = listener)
    }

    fun put(endpoint: String, urlParameters: UrlParameters? = null, headers: Headers.Builder = Headers.Builder(),
            body: Any? = null, serializer: RequestSerializer? = null, extraData: Any? = null,
            listener: SprintListener? = null): RequestProcessor {

        return executeRequest(PutRequest(
            url = buildEndpoint(urlBase, endpoint),
            urlParams = urlParameters,
            headers = headers,
            extraData = extraData,
            body = serializeBody(serializer, body)),
            listener)
    }

    fun put(endpoint: String, urlParameters: UrlParameters? = null, headers: Headers.Builder = Headers.Builder(),
            serializer: RequestSerializer? = null, body: Any? = null, extraData: Any? = null,
            listener: RequestFinished? = null): RequestProcessor {

        return executeRequest(PutRequest(
            url = buildEndpoint(urlBase, endpoint),
            urlParams = urlParameters,
            headers = headers,
            extraData = extraData,
            body = serializeBody(serializer, body)),
            listener = listener)
    }

    fun delete(endpoint: String, urlParameters: UrlParameters? = null, headers: Headers.Builder = Headers.Builder(),
               body: Any? = null, serializer: RequestSerializer? = null, extraData: Any? = null,
               listener: SprintListener? = null): RequestProcessor {

        return executeRequest(DeleteRequest(
            url = buildEndpoint(urlBase, endpoint),
            urlParams = urlParameters,
            headers = headers,
            extraData = extraData,
            body = serializeBody(serializer, body)),
            listener)
    }

    fun delete(endpoint: String, urlParameters: UrlParameters? = null, headers: Headers.Builder = Headers.Builder(),
               serializer: RequestSerializer? = null, body: Any? = null, extraData: Any? = null,
               listener: RequestFinished? = null): RequestProcessor {

        return executeRequest(DeleteRequest(
            url = buildEndpoint(urlBase, endpoint),
            urlParams = urlParameters,
            headers = headers,
            extraData = extraData,
            body = serializeBody(serializer, body)),
            listener = listener)
    }

}