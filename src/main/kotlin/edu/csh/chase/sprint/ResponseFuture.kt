package edu.csh.chase.sprint

import okhttp3.Call
import okhttp3.OkHttpClient
import java.io.IOException
import java.util.concurrent.*

class ResponseFuture(val request: Request,
                     val client: OkHttpClient,
                     private val listener: SprintListener?,
                     val retries: BackoffTimeout = BackoffTimeout.Exponential(500, 2, 300000L, 6)) : Future<Response> {

    private var currentCall: Call? = null

    private var response: Response? = null

    private var canceled = false

    private var workerThread: Thread? = null

    private val worker = client.dispatcher.executorService.submit {
        workerThread = Thread.currentThread()
        listener?.sprintRequestQueued(request)

        while (retries.shouldRetry() && !canceled) {
            invoke()

            if (response !is Response.ConnectionError) {
                break
            }

            Thread.sleep(retries.getNextDelay())
        }

        response?.let {
            when (it) {
                is Response.Success -> listener?.sprintSuccess(it)
                is Response.Failure -> listener?.sprintFailure(it)
                is Response.ConnectionError -> listener?.sprintConnectionError(it)
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