package edu.csh.chase.sprint

import okhttp3.Call
import okhttp3.OkHttpClient
import java.io.IOException
import java.util.concurrent.*

class ResponseFuture(val request: Request,
                     val client: OkHttpClient,
                     private val listener: SprintListener?,
                     val retries: BackoffTimeout = BackoffTimeout.NoRetry()) : Future<Response> {

    private var currentCall: Call? = null

    private var response: Response? = null

    private var canceled = false

    private var workerThread: Thread? = null

    private val worker = client.dispatcher.executorService.submit {
        workerThread = Thread.currentThread()
        listener?.sprintRequestQueued(request)

        do {
            invoke()

            // If the response is not a connection error, we get data and can move on
            // If the retry mechanism says no retrying, we want to bail out
            // If the request has been canceled, don't even wait for the next retry sleep, just bail
            if ((response != null && response !is Response.ConnectionError) || !retries.shouldRetry() || canceled) {
                break
            }

            // No need to wait if the delay is less than 3ms, just skip it.
            retries.getNextDelay().takeIf { it > 3 }?.let {
                Thread.sleep(it)
            }
            // Double-check the request hasn't been cancelled during the sleep
        } while (!canceled)

        if (!canceled) {
            response?.let {
                when (it) {
                    is Response.Success -> listener?.sprintSuccess(it)
                    is Response.Failure -> listener?.sprintFailure(it)
                    is Response.ConnectionError -> listener?.sprintConnectionError(it)
                }
            }
        }
        
    }

    private fun invoke() {
        if (canceled) {
            return
        }
        currentCall = client.newCall(request.okHttpRequest)

        response = try {
            Response.from(currentCall!!.execute(), request)
        } catch (e: IOException) {
            Response.ConnectionError(this.request, e)
        }
    }

    override fun isDone(): Boolean = worker.isDone

    override fun isCancelled(): Boolean = canceled

    override fun get(): Response {
        checkThread()
        if (isCancelled) {
            throw CancellationException()
        }

        worker.get()

        return response ?: throw ExecutionException("Response was null", null)
    }

    override fun get(timeout: Long, unit: TimeUnit): Response {
        checkThread()
        if (isCancelled) {
            throw CancellationException()
        }

        worker.get(timeout, unit)

        return response ?: throw TimeoutException("Timed out after $timeout ms")
    }

    private fun checkThread() {
        if (Thread.currentThread() == workerThread) {
            throw IllegalStateException("Calling get() from the responses async block")
        }
    }

    override fun cancel(mayInterruptIfRunning: Boolean): Boolean {
        if (isDone || !mayInterruptIfRunning) {
            return false
        }
        listener?.sprintRequestCanceled(request)
        currentCall?.cancel()
        canceled = true

        return true
    }

}