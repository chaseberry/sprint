package edu.csh.chase.sprint.parameters

class UrlParameters(vararg val pairs: Pair<String, Any>) {

    override fun toString(): String {
        //TODO add an escape function for characters that require escaping
        var urlQueryString = "?"
        pairs.forEach { pair -> urlQueryString += "${pair.first}=${pair.second}&" }
        return urlQueryString
    }

}

fun urlParams(params: () -> Array<Pair<String, String>>): UrlParameters {
    return UrlParameters(*params())
}

fun urlParams(vararg pairs: Pair<String, Any>): UrlParameters = UrlParameters(*pairs)