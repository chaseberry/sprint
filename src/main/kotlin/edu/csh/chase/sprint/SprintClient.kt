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

    //Maybe make this NOT have a return?
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
        return RequestProcessor(request, client, listener).executeRequest()
    }

    fun executeRequest(request: Request, listener: ((Request, Response) -> Unit)?):
            RequestProcessor {

        return executeRequest(
                request = request,
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

    fun get(endpoint: String, urlParameters: UrlParameters? = null, headers: Headers.Builder = Headers.Builder(),
            extraData: Any? = null, listener: SprintListener?): RequestProcessor {

        return executeRequest(GetRequest(
                url = buildEndpoint(urlBase ?: "", endpoint),
                urlParams = urlParameters,
                headers = headers,
                extraData = extraData),
                listener)
    }

    fun get(endpoint: String, urlParameters: UrlParameters? = null, headers: Headers.Builder = Headers.Builder(),
            extraData: Any? = null, listener: ((Request, Response) -> Unit)?):
            RequestProcessor {

        return executeRequest(GetRequest(
                url = buildEndpoint(urlBase ?: "", endpoint),
                urlParams = urlParameters,
                headers = headers,
                extraData = extraData),
                listener = listener)
    }

    fun post(endpoint: String, urlParameters: UrlParameters? = null, headers: Headers.Builder = Headers.Builder(),
             body: Any? = null, serializer: RequestSerializer? = null, extraData: Any? = null,
             listener: SprintListener? = null): RequestProcessor {

        return executeRequest(PostRequest(
                url = buildEndpoint(urlBase ?: "", endpoint),
                urlParams = urlParameters,
                headers = headers,
                extraData = extraData,
                body = serializeBody(serializer, body)),
                listener)
    }

    fun post(endpoint: String, urlParameters: UrlParameters? = null, headers: Headers.Builder = Headers.Builder(),
             serializer: RequestSerializer? = null, body: Any? = null, extraData: Any? = null,
             listener: ((Request, Response) -> Unit)? = null): RequestProcessor {

        return executeRequest(PostRequest(
                url = buildEndpoint(urlBase ?: "", endpoint),
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
                url = buildEndpoint(urlBase ?: "", endpoint),
                urlParams = urlParameters,
                headers = headers,
                extraData = extraData,
                body = serializeBody(serializer, body)),
                listener)
    }

    fun put(endpoint: String, urlParameters: UrlParameters? = null, headers: Headers.Builder = Headers.Builder(),
            serializer: RequestSerializer? = null, body: Any? = null, extraData: Any? = null,
            listener: ((Request, Response) -> Unit)? = null): RequestProcessor {

        return executeRequest(PutRequest(
                url = buildEndpoint(urlBase ?: "", endpoint),
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
                url = buildEndpoint(urlBase ?: "", endpoint),
                urlParams = urlParameters,
                headers = headers,
                extraData = extraData,
                body = serializeBody(serializer, body)),
                listener)
    }

    fun delete(endpoint: String, urlParameters: UrlParameters? = null, headers: Headers.Builder = Headers.Builder(),
               serializer: RequestSerializer? = null, body: Any? = null, extraData: Any? = null,
               listener: ((Request, Response) -> Unit)? = null): RequestProcessor {

        return executeRequest(DeleteRequest(
                url = buildEndpoint(urlBase ?: "", endpoint),
                urlParams = urlParameters,
                headers = headers,
                extraData = extraData,
                body = serializeBody(serializer, body)),
                listener = listener)
    }

}