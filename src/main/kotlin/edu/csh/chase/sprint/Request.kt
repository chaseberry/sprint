package edu.csh.chase.sprint

import java.util.*

data class Request(val url: String, val requestType: RequestType, var extraData: Any? = null) {

}