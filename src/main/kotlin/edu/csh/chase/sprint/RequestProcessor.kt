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

    private var executed = false

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

    fun syncExecute(): Response {
        if (executed) {
            return Response.ConnectionError(request, IOException("Request has already been executed"))
        }

        executed = true

        return internSyncExecute()
    }

    private fun internSyncExecute(): Response {
        currentCall = client.newCall(request.okHttpRequest)

        return try {
            currentCall!!.execute().let {
                Response.Success(request, it.code(), it.body()?.use { it.bytes() }, it.headers())
            }
        } catch (e: IOException) {

            if (retries.shouldRetry()) {
                Thread.sleep(retries.getNextDelay())
                return internSyncExecute()
            }

            Response.ConnectionError(request, e)
        }
    }

    fun cancelRequest() {
        currentCall?.cancel()
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
        with(response) {
            val code = response.code()
            if (code in 200..299) {
                listener?.sprintSuccess(
                    Response.Success(
                        request = this@RequestProcessor.request,
                        statusCode = code,
                        body = body()?.use { it.bytes() },
                        headers = headers()
                    )
                )
            } else {
                listener?.sprintFailure(
                    Response.Failure(
                        request = this@RequestProcessor.request,
                        statusCode = code,
                        body = body()?.use { it.bytes() },
                        headers = headers()
                    )
                )
            }
        }

    }

}