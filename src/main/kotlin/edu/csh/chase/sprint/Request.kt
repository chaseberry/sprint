package edu.csh.chase.sprint

import com.squareup.okhttp.RequestBody
import edu.csh.chase.sprint.parameters.Header
import edu.csh.chase.sprint.parameters.UrlBody
import java.util.ArrayList

data class Request(val url: String, val requestType: RequestType,
                   val urlParams: UrlBody? = null, val body: RequestBody? = null,
                   val headers: ArrayList<Header>? = null, var extraData: Any? = null) {
}