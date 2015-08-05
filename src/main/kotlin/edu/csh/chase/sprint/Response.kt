package edu.csh.chase.sprint

import com.squareup.okhttp.Headers
import com.squareup.okhttp.ResponseBody
import edu.csh.chase.sprint.json.JsonBase
import edu.csh.chase.sprint.json.parseJsonString

data class Response(val statusCode: Int, val body: ResponseBody?, val headers: Headers?) {

    val bodyAsJson: JsonBase?
        get() {
            if (body == null) {
                return null
            }
            return parseJsonString(String(body.bytes()))
        }

    val bodyAsString: String? = if (body == null) null else String(body.bytes())

    val successful: Boolean
        get() {
            return statusCode in 200..299
        }

}