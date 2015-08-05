package edu.csh.chase.sprint

import com.squareup.okhttp
import com.squareup.okhttp.Call
import com.squareup.okhttp.Callback
import com.squareup.okhttp.OkHttpClient
import java.io.IOException
import com.squareup.okhttp.Request as OkRequest
import com.squareup.okhttp.Response as OkResponse

class RequestProcessor(val request: Request, private val client: OkHttpClient, private val listener: SprintListener?) :
        Callback {

    var currentCall: Call? = null

    private fun buildOkRequest(): OkRequest {
        val builder = OkRequest.Builder()
        //TODO make sure the URL is still valid if the urlParams is null
        builder.url(request.url + request.urlParams?.toString())
        when (request.requestType) {
            RequestType.Get -> builder.get()
            RequestType.Post -> builder.post(request.body)
            RequestType.Put -> builder.put(request.body)
            RequestType.Delete -> builder.delete(request.body)
        }
        if (request.headers != null) {
            builder.headers(request.headers.build())
        }
        return builder.build()
    }

    fun executeRequest(): RequestProcessor {
        val okRequest = buildOkRequest()
        currentCall = client.newCall(okRequest)
        currentCall!!.enqueue(this)
        return this
    }

    fun cancelRequest() {
        currentCall?.cancel()
        currentCall = null
    }

    override fun onFailure(request: okhttp.Request?, e: IOException) {
        e.printStackTrace()
        listener?.sprintFailure(this.request, Response(-1, null, null))
    }

    override fun onResponse(response: OkResponse) {
        val statusCode = response.code()
        val body = response.body()
        val headers = response.headers()
        if (statusCode in 200..299) {
            listener?.sprintSuccess(request, Response(statusCode, body, headers))
        } else {
            listener?.sprintFailure(request, Response(statusCode, body, headers))
        }
    }

}