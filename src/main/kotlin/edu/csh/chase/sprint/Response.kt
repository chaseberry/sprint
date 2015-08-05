package edu.csh.chase.sprint

import com.squareup.okhttp.Headers
import com.squareup.okhttp.ResponseBody
import edu.csh.chase.sprint.json.JsonBase
import edu.csh.chase.sprint.json.parseJsonString

data class Response(val statusCode: Int, val responseBody: ResponseBody?, val headers: Headers?) {

    val bodyAsJson: JsonBase?
        get() {
            if (responseBody == null) {
                return null
            }
            return parseJsonString(String(responseBody.bytes()))
        }

    val bodyAsString: String? = if (responseBody == null) null else String(responseBody.bytes())

    val successful: Boolean
        get() {
            return statusCode in 200..299
        }

}