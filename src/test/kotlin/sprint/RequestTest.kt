package sprint

import com.squareup.okhttp.OkHttpClient
import edu.csh.chase.sprint.GetRequest
import edu.csh.chase.sprint.RequestProcessor
import edu.csh.chase.sprint.parameters.UrlParameters
import org.junit.Test
import kotlin.test.assertEquals

class RequestTest() {

    @Test fun buildUrlRequestTest() {
        val request = GetRequest(
                url = "https://test.com",
                urlParams = UrlParameters("key" to "value")
        )
        val proccessor = RequestProcessor(request, OkHttpClient(), null)
        val builtRequest = proccessor.buildOkRequest()
        assertEquals("https://test.com/?key=value&", builtRequest.urlString())
    }

    @Test fun buildUrlNoParametersTest() {
        val request = GetRequest(
                url = "https://test.com"
        )
        val proccessor = RequestProcessor(request, OkHttpClient(), null)
        val builtRequest = proccessor.buildOkRequest()
        assertEquals("https://test.com/", builtRequest.urlString())
    }

}