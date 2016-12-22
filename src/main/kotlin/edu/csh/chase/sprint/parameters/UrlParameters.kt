package edu.csh.chase.sprint.parameters

class UrlParameters(vararg val pairs: Pair<String, Any>) {

    override fun toString(): String {
        //TODO add an escape function for characters that require escaping
        return "?" + pairs.map { "${it.first}=${it.second}" }.joinToString("&")
    }

}

fun urlParams(params: () -> Array<Pair<String, String>>): UrlParameters {
    return UrlParameters(*params())
}

fun urlParams(vararg pairs: Pair<String, Any>): UrlParameters = UrlParameters(*pairs)