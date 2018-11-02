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
    fun testSyncGet() {
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


    @Test
    fun sameResponse() {
        var response: Response? = null
        val future = Sprint.get(
            "https://reqres.in/api/users/1"
        ) {
            response = it
        }

        Assert.assertSame("Sync and Async Responses were different", future.get(), response)
    }

    @Test
    fun responseOrder() {
        var asyncFinished = false

        Sprint.get(
            "https://reqres.in/api/users/1"
        ) {
            asyncFinished = true
        }.get()

        Assert.assertTrue("Sync finished before async was executed", asyncFinished)

    }

}