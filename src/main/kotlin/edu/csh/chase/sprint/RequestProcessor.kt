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
                       val retries: BackoffTimeout = BackoffTimeout.Exponential(500, 2, 300000L, 6)) : Callback {

    private var currentCall: Call? = null

    private var future: ResponseFuture? = null

    var executed = false
        private set

    @Deprecated("use asyncExecute", ReplaceWith("asyncExecute()"))
    fun executeRequest(): RequestProcessor {
        return asyncExecute()
    }

    fun asyncExecute(): RequestProcessor {
        if (executed) {
            return this
        }

        executed = true

        listener?.sprintRequestQueued(request)

        internalAsyncExecute()

        return this
    }

    private fun internalAsyncExecute() {
        currentCall = client.newCall(request.okHttpRequest)

        currentCall!!.enqueue(this)
    }

    fun syncExecute(): ResponseFuture {
        if (executed) {
            return future ?: throw Exception("---")
        }

        executed = true

        return ResponseFuture(request, client, retries)
    }

    fun cancelRequest() {
        currentCall?.cancel()
        future?.cancel(true)
        listener?.sprintRequestCanceled(request)
        currentCall = null
    }

    override fun onFailure(request: Call?, e: IOException) {
        if (retries.shouldRetry()) {
            Thread.sleep(retries.getNextDelay())
            internalAsyncExecute()
            return
        }

        listener?.sprintConnectionError(Response.ConnectionError(this.request, e))
    }

    override fun onResponse(request: Call, response: OkResponse) {
        val r = Response.from(response, this.request)

        when (r) {
            is Response.Success -> listener?.sprintSuccess(r)
            is Response.Failure -> listener?.sprintFailure(r)
        }

    }

}