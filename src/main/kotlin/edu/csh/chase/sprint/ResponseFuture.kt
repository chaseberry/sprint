package edu.csh.chase.sprint

import okhttp3.Call
import okhttp3.OkHttpClient
import java.io.IOException
import java.util.concurrent.*

class ResponseFuture(val request: Request,
                     val client: OkHttpClient,
                     val retries: BackoffTimeout = BackoffTimeout.Exponential(500, 2, 300000L, 6)) : Future<Response> {

    private var currentCall: Call? = null

    private var response: Response? = null

    private var canceled = false

    private val worker = Thread {

        while (retries.shouldRetry() && !canceled) {
            invoke()

            if (response !is Response.ConnectionError) {
                break
            }

            Thread.sleep(retries.getNextDelay())
        }

    }.also { it.start() }

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

    override fun isDone(): Boolean = currentCall?.isExecuted ?: false

    override fun isCancelled(): Boolean = canceled

    override fun get(): Response {
        if (isCancelled) {
            throw CancellationException()
        }

        worker.join()

        return response ?: throw ExecutionException("Response was null", null)
    }

    override fun get(timeout: Long, unit: TimeUnit): Response {
        if (isCancelled) {
            throw CancellationException()
        }

        worker.join(unit.toMillis(timeout))

        return response ?: throw TimeoutException("Timed out after $timeout ms")
    }

    override fun cancel(mayInterruptIfRunning: Boolean): Boolean {
        if (isDone) {
            return false
        }

        currentCall?.cancel()
        canceled = true

        return true
    }

}