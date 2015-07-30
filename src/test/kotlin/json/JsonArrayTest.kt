package json

import edu.csh.chase.sprint.json.JsonArray
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

class JsonArrayTest() {

    Test fun emptyJsonArrayTest() {
        val array = JsonArray()

        assertEquals(0, array.size)

        assertNull(array[0])
        assertEquals(1024, array[0, 1024])
        assertFalse(0 in array)
        assertEquals("[]", array.toString())
    }

}