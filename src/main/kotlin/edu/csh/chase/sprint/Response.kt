package edu.csh.chase.sprint

import com.squareup.okhttp.Headers
import edu.csh.chase.kjson.JsonBase
import edu.csh.chase.kjson.parseJsonString

data class Response(val statusCode: Int, val body: ByteArray?, val headers: Headers?) {

    val bodyAsJson: JsonBase?
        get() {
            if (body == null) {
                return null
            }
            return parseJsonString(String(body))
        }

    val bodyAsString: String? = if (body == null) null else String(body)

    val successful: Boolean
        get() {
            return statusCode in 200..299
        }

}