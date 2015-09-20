package sprint

import edu.csh.chase.sprint.buildEndpoint
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class EndpointTest {

    @Test fun testBuildEndpoint() {
        val base = "https://google.com/"
        val endpoint = "test"
        assertEquals("https://google.com/test", buildEndpoint(base, endpoint))
        assertNotEquals("https://google.comtest", buildEndpoint(base, endpoint))
    }

}