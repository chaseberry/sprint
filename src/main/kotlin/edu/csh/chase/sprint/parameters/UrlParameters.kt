package edu.csh.chase.sprint.parameters

import java.util.*

class UrlParameters(vararg val pairs: Pair<String, Any>) {

    override fun toString(): String {
        //TODO add an escape function for characters that require escaping
        var urlQueryString = "?"
        pairs.forEach { pair -> urlQueryString += "${pair.first}=${pair.second}&" }
        return urlQueryString
    }

}