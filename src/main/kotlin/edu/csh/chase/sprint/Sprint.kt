package edu.csh.chase.sprint

import edu.csh.chase.sprint.parameters.UrlParameters
import okhttp3.Headers
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import java.util.concurrent.TimeUnit

object Sprint {

    private val client: OkHttpClient by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        OkHttpClient().newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS).build()
    }

    fun executeRequest(request: Request, requestFinished: ((Request, Response) -> Unit )): RequestProcessor {

        return executeRequest(request, object : SprintListener {
            override fun sprintSuccess(request: Request, response: Response) {
                requestFinished(request, response)
            }

            override fun sprintFailure(request: Request, response: Response) {
                requestFinished(request, response)
            }
        })
    }

    fun executeRequest(request: Request, sprintListener: SprintListener?): RequestProcessor {
        return RequestProcessor(request, client, sprintListener).executeRequest()
    }

    fun get(url: String, urlParameters: UrlParameters? = null, headers: Headers.Builder = Headers.Builder(),
            extraData: Any? = null, requestFinished: ((Request, Response) -> Unit )): RequestProcessor {

        return executeRequest(Request(
                url = url,
                requestType = RequestType.Get,
                urlParams = urlParameters,
                extraData = extraData,
                headers = headers),
                requestFinished)
    }

    fun get(url: String, urlParameters: UrlParameters? = null, headers: Headers.Builder = Headers.Builder(),
            extraData: Any? = null, requestFinished: SprintListener): RequestProcessor {

        return executeRequest(Request(
                url = url,
                requestType = RequestType.Get,
                urlParams = urlParameters,
                extraData = extraData,
                headers = headers),
                requestFinished)
    }

    fun post(url: String, urlParameters: UrlParameters ? = null, headers: Headers.Builder = Headers.Builder(),
             body: RequestBody? = null, extraData: Any ? = null, requestFinished: ((Request, Response) -> Unit )):
            RequestProcessor {

        return executeRequest(Request(
                url = url,
                requestType = RequestType.Post,
                urlParams = urlParameters,
                headers = headers,
                body = body,
                extraData = extraData),
                requestFinished)
    }

    fun post(url: String, urlParameters: UrlParameters ? = null, headers: Headers.Builder = Headers.Builder(),
             body: RequestBody? = null, extraData: Any ? = null, requestFinished: SprintListener):
            RequestProcessor {

        return executeRequest(Request(
                url = url, requestType = RequestType.Post,
                urlParams = urlParameters,
                headers = headers,
                body = body,
                extraData = extraData),
                requestFinished)
    }

    fun put(url: String, urlParameters: UrlParameters ? = null, headers: Headers.Builder = Headers.Builder(),
            body: RequestBody? = null, extraData: Any ? = null, requestFinished: ((Request, Response) -> Unit )):
            RequestProcessor {

        return executeRequest(Request(
                url = url,
                requestType = RequestType.Put,
                urlParams = urlParameters,
                headers = headers,
                body = body,
                extraData = extraData),
                requestFinished)
    }

    fun put(url: String, urlParameters: UrlParameters ? = null, headers: Headers.Builder = Headers.Builder(),
            body: RequestBody? = null, extraData: Any ? = null, requestFinished: SprintListener):
            RequestProcessor {

        return executeRequest(Request(
                url = url,
                requestType = RequestType.Put,
                urlParams = urlParameters,
                headers = headers,
                body = body,
                extraData = extraData),
                requestFinished)
    }

    fun delete(url: String, urlParameters: UrlParameters ? = null, headers: Headers.Builder = Headers.Builder(),
               body: RequestBody? = null, extraData: Any ? = null, requestFinished: ((Request, Response) -> Unit )):
            RequestProcessor {

        return executeRequest(Request(
                url = url,
                requestType = RequestType.Delete,
                urlParams = urlParameters,
                headers = headers,
                body = body,
                extraData = extraData),
                requestFinished)
    }

    fun delete(url: String, urlParameters: UrlParameters ? = null, headers: Headers.Builder = Headers.Builder(),
               body: RequestBody? = null, extraData: Any ? = null, requestFinished: SprintListener):
            RequestProcessor {

        return executeRequest(Request(
                url = url,
                requestType = RequestType.Delete,
                urlParams = urlParameters,
                headers = headers,
                body = body,
                extraData = extraData),
                requestFinished)
    }

}