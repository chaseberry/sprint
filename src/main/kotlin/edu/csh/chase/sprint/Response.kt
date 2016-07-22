package edu.csh.chase.sprint

import edu.csh.chase.kjson.Json
import edu.csh.chase.kjson.JsonBase
import okhttp3.Headers

data class Response(val statusCode: Int, val body: ByteArray?, val headers: Headers?) {

    val bodyAsJson: JsonBase?
        get() {
            if (body == null) {
                return null
            }
            return Json.parse(String(body))
        }

    val bodyAsString: String? = if (body == null) null else String(body)

    val successful: Boolean
        get() {
            return statusCode in 200..299
        }

}