package sprint

import edu.csh.chase.sprint.JsonRequestSerializer
import edu.csh.chase.sprint.RequestSerializer
import edu.csh.chase.sprint.Response
import edu.csh.chase.sprint.SprintClient
import okhttp3.OkHttpClient
import org.junit.Assert.*
import org.junit.Test

class SprintClientTest : SprintClient("https://reqres.in/api/") {

    override fun configureClient(client: OkHttpClient.Builder) {

    }

    override val defaultRequestSerializer: RequestSerializer = JsonRequestSerializer()

    @Test fun getUserOne() {
        get(
            "users/1"
        ) {
            assertTrue("Request should be a ServerResponse", it is Response.ServerResponse)

            when (it) {
                is Response.ServerResponse -> {
                    assertEquals("StatusCode was not 200", 200, it.statusCode)

                    it.bodyAsJson ?: fail("Body was not Json")
                }
                is Response.ConnectionError -> {

                }
            }

        }
    }

    @Test fun fourOFour() {
        get(
            "unknown/23"
        ) {
            assertTrue("Request should be a ServerResponse", it is Response.ServerResponse)

            when (it) {
                is Response.ServerResponse -> {
                    assertEquals("StatusCode was not 404", 404, it.statusCode)

                }
                is Response.ConnectionError -> {

                }
            }
        }
    }

}