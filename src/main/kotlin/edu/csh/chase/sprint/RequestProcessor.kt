package edu.csh.chase.sprint

import edu.csh.chase.sprint.internal.Processor
import okhttp3.*
import okhttp3.OkHttpClient
import java.io.IOException
import okhttp3.Request as OkRequest
import okhttp3.Response as OkResponse

class RequestProcessor(request: Request,
                       client: OkHttpClient,
                       private val listener: SprintListener?,
                       val retryLimit: Int = 0) : Processor(request, client),
        Callback {

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
        //TODO create status codes for all potential IOExceptions
        listener?.sprintFailure(this.request, Response(-1, null, null))
    }

    override fun onResponse(request: Call, response: OkResponse) {
        val statusCode = response.code()
        val body = response.body()?.bytes()
        val headers = response.headers()
        if (statusCode in 200..299) {
            listener?.sprintSuccess(this.request, Response(statusCode, body, headers))
        } else {
            listener?.sprintFailure(this.request, Response(statusCode, body, headers))
        }
    }

}