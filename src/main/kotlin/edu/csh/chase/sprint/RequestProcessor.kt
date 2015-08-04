package edu.csh.chase.sprint

import com.squareup.okhttp
import com.squareup.okhttp.Call
import com.squareup.okhttp.Callback
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Response
import java.io.IOException
import com.squareup.okhttp.Request as OkRequest
import com.squareup.okhttp.Response as OkResponse

class RequestProcessor(val request: Request, private val client: OkHttpClient, val successListener: SprintSuccess?,
                       val failureListener: SprintFailure?) : Callback {

    var currentCall: Call? = null

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

    fun executeRequest() {
        val okRequest = buildOkRequest()
        currentCall = client.newCall(okRequest)
        currentCall!!.enqueue(this)
    }

    fun cancelRequest() {
        currentCall?.cancel()
        currentCall = null
    }

    override fun onFailure(request: okhttp.Request?, e: IOException?) {
    }

    override fun onResponse(response: Response?) {
    }

}