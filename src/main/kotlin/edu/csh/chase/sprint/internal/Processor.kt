package edu.csh.chase.sprint.internal

import edu.csh.chase.sprint.Request
import edu.csh.chase.sprint.RequestType
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request as OkRequest
import okhttp3.RequestBody

abstract class Processor(val request: Request, protected val client: OkHttpClient) {

    fun buildOkRequest(): OkRequest {
        val builder = OkRequest.Builder()
        //TODO make sure the URL is still valid if the urlParams is null
        builder.url(if (request.urlParams != null) {
            request.url + request.urlParams.toString()
        } else {
            request.url
        })
        when (request.requestType) {
            RequestType.Get -> builder.get()
            RequestType.Post -> builder.post(request.body ?: RequestBody.create(MediaType.parse("text/plain"), ""))
            RequestType.Put -> builder.put(request.body ?: RequestBody.create(MediaType.parse("text/plain"), ""))
            RequestType.Delete -> builder.delete(request.body)
        }
        builder.headers(request.headers.build())

        return builder.build()
    }

}