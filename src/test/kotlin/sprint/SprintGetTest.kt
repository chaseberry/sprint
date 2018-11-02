package sprint

import edu.csh.chase.sprint.Response
import edu.csh.chase.sprint.Sprint
import org.junit.Assert
import org.junit.Test

class SprintGetTest() {

    @Test
    fun testGet() {

        Sprint.get(
            "https://reqres.in/api/users/1"
        ) {
            Assert.assertTrue("Request should be a Success", it is Response.Success)

            when (it) {
                is Response.Success -> {
                    Assert.assertEquals("StatusCode was not 200", 200, it.statusCode)

                    it.bodyAsJson ?: Assert.fail("Body was not Json")
                }
                is Response.ConnectionError -> {

                }
            }
        }

    }

    @Test
    fun testSyncGet(){
        val r = Sprint.get(
            "https://reqres.in/api/users/1"
        ).get()

        Assert.assertTrue("Request should be a Success", r is Response.Success)

        when (r) {
            is Response.Success -> {
                Assert.assertEquals("StatusCode was not 200", 200, r.statusCode)

                r.bodyAsJson ?: Assert.fail("Body was not Json")
            }
            is Response.ConnectionError -> {

            }
        }
    }

}