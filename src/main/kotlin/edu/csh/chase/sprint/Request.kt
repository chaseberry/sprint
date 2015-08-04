package edu.csh.chase.sprint

import com.squareup.okhttp.RequestBody
import edu.csh.chase.sprint.parameters.UrlBody
import java.util.*

data class Request(val url: String, urlParams: UrlBody?, val requestType: RequestType, val body: RequestBody?, var extraData: Any? = null)