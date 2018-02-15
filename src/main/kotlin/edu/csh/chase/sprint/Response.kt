package edu.csh.chase.sprint

import edu.csh.chase.kjson.Json
import edu.csh.chase.kjson.JsonBase
import okhttp3.Headers
import java.io.IOException
import okhttp3.Response as OkResponse

sealed class Response(val request: Request) {

    abstract val successful: Boolean

    class Success(request: Request, val statusCode: Int, val body: ByteArray?, val headers: Headers?) : Response(request) {

        constructor(request: Request, response: OkResponse) : this(request, response.code(), response.body()?.bytes(), response.headers())

        val bodyAsJson: JsonBase?
            get() {
                if (body == null) {
                    return null
                }
                return Json.parse(String(body))
            }

        val bodyAsString: String? = if (body == null) null else String(body)

        override val successful: Boolean
            get() {
                return statusCode in 200..299
            }

    }

    class Failure(request: Request, val error: IOException) : Response(request) {

        override val successful: Boolean = false

    }

}