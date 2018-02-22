package sprint

import edu.csh.chase.sprint.BackoffTimeout
import org.junit.Assert.assertEquals
import org.junit.Test

class BackoffTest {

    @Test fun testExponentialBackoff() {
        val backoff = BackoffTimeout.Exponential(500, 2, 300000L, 6)

        //should output the values 500, 1000, 2000, 4000, 8000, 16000
        listOf(500L, 1000L, 2000L, 4000L, 8000L, 16000L).forEachIndexed { index, i ->
            assertEquals(i, backoff.getNextDelay(index))
        }

    }

    @Test fun testLinearBackoff() {
        val backoff = BackoffTimeout.Linear(0, 100, 300000L, 6)

        listOf(0L, 100L, 200L, 300L, 400L, 500L).forEachIndexed { index, i ->
            assertEquals(i, backoff.getNextDelay(index))
        }
    }

}