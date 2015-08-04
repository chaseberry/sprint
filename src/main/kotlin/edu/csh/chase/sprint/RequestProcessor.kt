package edu.csh.chase.sprint

import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request as OkRequest

class RequestProcessor(val request: Request, val client: OkHttpClient, val successListener: SprintSuccess?,
                       val failureListener: SprintFailure?) {

    private fun buildOkRequest(): OkRequest {
        val builder = OkRequest.Builder()
        //TODO make sure the URL is still valid if the urlParams is null
        builder.url(request.url + request.urlParams?.toString())
        when (request.requestType) {
            RequestType.Get -> builder.get()
            RequestType.Post -> builder.post(request.body)
        }
        request.headers?.forEach { builder.addHeader(it.name, it.value) }
        return builder.build()
    }

}