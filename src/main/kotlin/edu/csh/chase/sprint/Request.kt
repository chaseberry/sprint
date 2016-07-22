package edu.csh.chase.sprint

import edu.csh.chase.sprint.parameters.UrlParameters
import okhttp3.Headers
import okhttp3.RequestBody

data class Request(val url: String, val requestType: RequestType,
                   var urlParams: UrlParameters? = null, var body: RequestBody? = null,
                   val headers: Headers.Builder = Headers.Builder(), var extraData: Any? = null)

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