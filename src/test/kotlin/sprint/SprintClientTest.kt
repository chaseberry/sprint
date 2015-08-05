package sprint

import com.squareup.okhttp.OkHttpClient
import edu.csh.chase.sprint.JsonRequestSerializer
import edu.csh.chase.sprint.RequestSerializer
import edu.csh.chase.sprint.SprintClient

class SprintClientTest : SprintClient("Some url here") {

    override fun configureClient(client: OkHttpClient) {

    }

    override val defaultRequestSerializer: RequestSerializer = JsonRequestSerializer()
    
}