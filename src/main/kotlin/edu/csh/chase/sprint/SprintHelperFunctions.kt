package edu.csh.chase.sprint

import com.squareup.okhttp.Headers
import com.squareup.okhttp.RequestBody
import edu.csh.chase.sprint.json.JsonBase
import edu.csh.chase.sprint.parameters.JsonBody

fun headers(vararg headers: Pair<String, String>): Headers.Builder {
    val builder = Headers.Builder()
    headers.forEach { builder.add(it.first, it.second) }
    return builder
}

fun buildEndpoint(urlBase: String, endpoint: String): String {
    var builtString = urlBase
    if (urlBase.last() != '/') {
        builtString += '/'
    }
    if (endpoint.first() == '/') {
        builtString += endpoint.substring(1)
    } else {
        builtString += endpoint
    }
    return builtString
}

val JsonBase.toRequestBody: RequestBody
    get() {
        return JsonBody(this)
    }