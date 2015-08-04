package edu.csh.chase.sprint

import com.squareup.okhttp.Headers
import com.squareup.okhttp.RequestBody
import edu.csh.chase.sprint.parameters.UrlBody

data class Request(val url: String, val requestType: RequestType,
                   val urlParams: UrlBody? = null, val body: RequestBody? = null,
                   val headers: Headers.Builder? = null, var extraData: Any? = null) {
}