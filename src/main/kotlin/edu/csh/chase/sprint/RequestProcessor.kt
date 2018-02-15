package edu.csh.chase.sprint

import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import java.io.IOException
import okhttp3.Request as OkRequest
import okhttp3.Response as OkResponse

class RequestProcessor(val request: Request,
                       val client: OkHttpClient,
                       private val listener: SprintListener?,
                       val retryLimit: Int = 0) : Callback {

    private var attemptCount = 0

    private var sleepTime = 1

    private var currentCall: Call? = null

    fun executeRequest(): RequestProcessor {
        val okRequest = request.okHttpRequest
        currentCall = client.newCall(okRequest)
        currentCall!!.enqueue(this)
        if (attemptCount == 0) {
            listener?.sprintRequestQueued(request)
        }
        return this
    }

    fun cancelRequest() {
        currentCall?.cancel()
        listener?.sprintRequestCanceled(request)
        currentCall = null
    }

    private fun retry() {
        attemptCount++
        Thread.sleep((sleepTime * 1000).toLong())
        sleepTime *= 2
    }

    override fun onFailure(request: Call?, e: IOException) {
        if (attemptCount < retryLimit) {
            retry()
            return
        }

        listener?.sprintFailure(Response.Failure(this.request, e))
    }

    override fun onResponse(request: Call, response: OkResponse) {
        val statusCode = response.code()
        val body = response.body()?.use { it.bytes() }
        val headers = response.headers()

        listener?.sprintSuccess(Response.Success(this.request, statusCode, body, headers))

    }

}