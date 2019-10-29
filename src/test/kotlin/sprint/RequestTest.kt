package sprint

import edu.csh.chase.sprint.getRequest
import edu.csh.chase.sprint.ResponseFuture
import edu.csh.chase.sprint.parameters.UrlParameters
import okhttp3.OkHttpClient
import org.junit.Test
import org.junit.Assert.*

class RequestTest() {

    @Test fun buildUrlRequestTest() {
        val request = getRequest(
                url = "https://test.com",
                urlParams = UrlParameters("key" to "value")
        )
        val proccessor = ResponseFuture(request, OkHttpClient(), null)
        val builtRequest = proccessor.request.okHttpRequest
        assertEquals("https://test.com/?key=value", builtRequest.url.toString())
    }

    @Test fun buildUrlNoParametersTest() {
        val request = getRequest(
                url = "https://test.com"
        )
        val proccessor = ResponseFuture(request, OkHttpClient(), null)
        val builtRequest = proccessor.request.okHttpRequest
        assertEquals("https://test.com/", builtRequest.url.toString())
    }

}