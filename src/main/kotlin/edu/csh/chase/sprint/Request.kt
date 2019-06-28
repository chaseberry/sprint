package edu.csh.chase.sprint

import com.sun.javafx.fxml.builder.URLBuilder
import edu.csh.chase.sprint.parameters.UrlParameters
import okhttp3.Headers
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.URI
import java.net.URL
import okhttp3.Request as OkRequest

data class Request(val url: String, val requestType: RequestType,
                   var urlParams: UrlParameters? = null, var body: RequestBody? = null,
                   val headers: Headers.Builder = Headers.Builder(), var extraData: Any? = null) {

    val okHttpRequest: OkRequest by lazy {
        val builder = OkRequest.Builder()
        //TODO make sure the URL is still valid if the urlParams is null
        builder.url(if (urlParams != null) {
            url + urlParams.toString()
        } else {
            url
        })
        when (requestType) {
            RequestType.Get -> builder.get()
            RequestType.Post -> builder.post(body ?: "".toRequestBody("text/plain".toMediaTypeOrNull()))
            RequestType.Put -> builder.put(body ?: "".toRequestBody("text/plain".toMediaTypeOrNull()))
            RequestType.Delete -> builder.delete(body)
        }
        builder.headers(headers.build())

        builder.build()
    }

}

fun GetRequest(url: String,
               urlParams: UrlParameters? = null,
               headers: Headers.Builder = Headers.Builder(),
               extraData: Any? = null): Request {

    return Request(
            url = url,
            requestType = RequestType.Get,
            urlParams = urlParams,
            headers = headers,
            extraData = extraData
    )
}

fun PostRequest(url: String,
                urlParams: UrlParameters? = null,
                headers: Headers.Builder = Headers.Builder(),
                body: RequestBody? = null,
                extraData: Any? = null): Request {

    return Request(
            url = url,
            requestType = RequestType.Post,
            urlParams = urlParams,
            headers = headers,
            body = body,
            extraData = extraData
    )
}

fun PutRequest(url: String,
               urlParams: UrlParameters? = null,
               headers: Headers.Builder = Headers.Builder(),
               body: RequestBody? = null,
               extraData: Any? = null): Request {

    return Request(
            url = url,
            requestType = RequestType.Put,
            urlParams = urlParams,
            headers = headers,
            body = body,
            extraData = extraData
    )
}

fun DeleteRequest(url: String,
                  urlParams: UrlParameters? = null,
                  headers: Headers.Builder = Headers.Builder(),
                  body: RequestBody? = null,
                  extraData: Any? = null): Request {

    return Request(
            url = url,
            requestType = RequestType.Delete,
            urlParams = urlParams,
            headers = headers,
            body = body,
            extraData = extraData
    )
}