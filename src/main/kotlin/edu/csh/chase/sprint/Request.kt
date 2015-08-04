package edu.csh.chase.sprint

import com.squareup.okhttp.RequestBody
import java.util.*

data class Request(val url: String, val requestType: RequestType, val body: RequestBody, var extraData: Any? = null) {

}