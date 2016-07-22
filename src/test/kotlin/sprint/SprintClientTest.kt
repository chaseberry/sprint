package sprint

import edu.csh.chase.sprint.JsonRequestSerializer
import edu.csh.chase.sprint.RequestSerializer
import edu.csh.chase.sprint.SprintClient
import okhttp3.OkHttpClient

class SprintClientTest : SprintClient("Some url here") {

    override fun configureClient(client: OkHttpClient.Builder) {

    }

    override val defaultRequestSerializer: RequestSerializer = JsonRequestSerializer()
    
}