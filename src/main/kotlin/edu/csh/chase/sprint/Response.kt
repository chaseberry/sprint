package edu.csh.chase.sprint

import edu.csh.chase.kjson.Json
import edu.csh.chase.kjson.JsonBase
import okhttp3.Headers
import java.io.IOException
import okhttp3.Response as OkResponse

sealed class Response(val request: Request) {

    abstract override fun toString(): String

    class Success(request: Request, val statusCode: Int, val body: ByteArray?, val headers: Headers?) : Response(request) {

        constructor(request: Request, response: OkResponse) : this(request, response.code(), response.body()?.bytes(), response.headers())

        val bodyAsJson: JsonBase?
            get() = bodyAsString?.let { Json.parse(it) }

        val bodyAsString: String?
            get() = body?.let { String(it) }

        override fun toString(): String = bodyAsString ?: "null"

    }

    class Failure(request: Request, val statusCode: Int, val body: ByteArray?, val headers: Headers?) : Response(request) {

        constructor(request: Request, response: OkResponse) : this(request, response.code(), response.body()?.bytes(), response.headers())

        val bodyAsJson: JsonBase?
            get() = bodyAsString?.let { Json.parse(it) }

        val bodyAsString: String?
            get() = body?.let { String(it) }

        override fun toString(): String = bodyAsString ?: "null"

    }

    class ConnectionError(request: Request, val error: IOException) : Response(request) {

        override fun toString(): String = error.message ?: "null"
    }

    val code: Int?
        get() = when (this) {
            is Success -> statusCode
            is Failure -> statusCode
            else -> null
        }

    companion object {
        fun from(response: okhttp3.Response, request: Request): Response {
            val code = response.code()
            return if (code in 200..299) {
                Response.Success(
                    request = request,
                    statusCode = code,
                    body = response.body()?.use { it.bytes() },
                    headers = response.headers()
                )

            } else {
                Response.Failure(
                    request = request,
                    statusCode = code,
                    body = response.body()?.use { it.bytes() },
                    headers = response.headers()
                )

            }
        }
    }

}