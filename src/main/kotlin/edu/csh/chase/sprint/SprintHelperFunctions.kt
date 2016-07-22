package edu.csh.chase.sprint

import edu.csh.chase.kjson.JsonBase
import edu.csh.chase.sprint.parameters.JsonBody
import okhttp3.Headers
import okhttp3.RequestBody
import java.net.URL

fun headers(vararg headers: Pair<String, String>): Headers.Builder {
    val builder = Headers.Builder()
    headers.forEach { builder.add(it.first, it.second) }
    return builder
}

fun buildEndpoint(urlBase: String, endpoint: String): String {
    return URL(URL(urlBase), endpoint).toString()
}

val JsonBase.toRequestBody: RequestBody
    get() {
        return JsonBody(this)
    }