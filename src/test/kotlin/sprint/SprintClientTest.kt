package sprint

import edu.csh.chase.sprint.JsonRequestSerializer
import edu.csh.chase.sprint.RequestSerializer
import edu.csh.chase.sprint.Response
import edu.csh.chase.sprint.SprintClient
import okhttp3.OkHttpClient
import org.junit.Test

class SprintClientTest : SprintClient("https://reqres.in/api/") {

    override fun configureClient(client: OkHttpClient.Builder) {

    }

    override val defaultRequestSerializer: RequestSerializer = JsonRequestSerializer()

    @Test fun getUserOne() {
        get(
            "users/1"
        ) {
            assert(it is Response.Success) { "Request should be a Success" }

            when (it) {
                is Response.Success -> {
                    assert(200 == it.statusCode) { "StatusCode was not 200" }

                    it.bodyAsJson ?: error("Body was not Json")
                }
                is Response.Error -> {

                }
            }

        }
    }

    @Test fun fourOFour() {
        get(
            "unknown/23"
        ) {
            assert(it is Response.Success) { "Request should be a Success" }

            when (it) {
                is Response.Success -> {
                    assert(404 == it.statusCode) { "StatusCode was not 200" }

                }
                is Response.Error -> {

                }
            }
        }
    }

}