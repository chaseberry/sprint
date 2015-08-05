package edu.csh.chase.sprint

import com.squareup.okhttp.Headers

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
    }
    return builtString
}