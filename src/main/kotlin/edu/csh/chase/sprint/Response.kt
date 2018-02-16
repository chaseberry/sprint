package edu.csh.chase.sprint

import edu.csh.chase.kjson.Json
import edu.csh.chase.kjson.JsonBase
import okhttp3.Headers
import java.io.IOException
import okhttp3.Response as OkResponse

sealed class Response(val request: Request) {

    abstract override fun toString(): String

    class Success(request: Request, statusCode: Int, body: ByteArray?, headers: Headers?) : ServerResponse(request, statusCode, body, headers) {

        constructor(request: Request, response: OkResponse) : this(request, response.code(), response.body()?.bytes(), response.headers())
    }

    class Failure(request: Request, statusCode: Int, body: ByteArray?, headers: Headers?) : ServerResponse(request, statusCode, body, headers) {

        constructor(request: Request, response: OkResponse) : this(request, response.code(), response.body()?.bytes(), response.headers())
    }

    class ConnectionError(request: Request, val error: IOException) : Response(request) {


        override fun toString(): String = error.message ?: "null"
    }

    abstract class ServerResponse(request: Request, val statusCode: Int, val body: ByteArray?, val headers: Headers?) : Response(request) {

        constructor(request: Request, response: OkResponse) : this(request, response.code(), response.body()?.bytes(), response.headers())

        val bodyAsJson: JsonBase?
            get() = bodyAsString?.let { Json.parse(it) }

        val bodyAsString: String?
            get() = body?.let { String(it) }

        override fun toString(): String = bodyAsString ?: "null"

    }

}