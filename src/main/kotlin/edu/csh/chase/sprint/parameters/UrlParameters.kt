package edu.csh.chase.sprint.parameters

import java.net.URLEncoder

class UrlParameters(vararg val pairs: Pair<String, Any>) {

    override fun toString(): String {
        //TODO add an escape function for characters that require escaping
        return "?" + pairs.map {
            "${URLEncoder.encode(it.first, "UTF-8")}=${URLEncoder.encode(it.second.toString(), "UTF-8")}"
        }.joinToString("&")
    }

}

fun urlParams(params: () -> Array<Pair<String, String>>): UrlParameters {
    return UrlParameters(*params())
}

fun urlParams(vararg pairs: Pair<String, Any>): UrlParameters = UrlParameters(*pairs)