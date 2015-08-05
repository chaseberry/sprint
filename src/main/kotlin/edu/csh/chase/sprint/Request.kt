package edu.csh.chase.sprint

import com.squareup.okhttp.Headers
import com.squareup.okhttp.RequestBody
import edu.csh.chase.sprint.parameters.UrlParameters

data class Request(val url: String, val requestType: RequestType,
                   val urlParams: UrlParameters? = null, val body: RequestBody? = null,
                   val headers: Headers.Builder? = null, var extraData: Any? = null) {
}

fun GetRequest(url: String,
               urlParams: UrlParameters? = null,
               headers: Headers.Builder? = null, extraData: Any? = null): Request {
    return Request(
            url = url,
            requestType = RequestType.Get,
            urlParams = urlParams,
            headers = headers,
            extraData = extraData
    )
}