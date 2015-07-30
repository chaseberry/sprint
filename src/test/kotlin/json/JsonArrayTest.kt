package json

import edu.csh.chase.sprint.json.JsonArray
import edu.csh.chase.sprint.json.JsonException
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class JsonArrayTest() {

    Test fun emptyJsonArrayTest() {
        val array = JsonArray()

        assertEquals(0, array.size)

        assertNull(array[0])
        assertEquals(1024, array[0, 1024])
        assertFalse(0 in array)
        assertEquals("[]", array.toString())
    }

    Test fun jsonFromStringTest() {
        val jsonArrayString = "[0,\"A String\",15.8, true, null]"
        try {
            val array = JsonArray(jsonArrayString)

            assertEquals(5, array.size)

            assertEquals(0, array[0])
            assertEquals("A String", array[1])
            assertEquals(15.8, array.getDouble(2))
            assertTrue(array.getBoolean(3, false))
            assertNull(array[4])
        } catch(invalidJson: JsonException) {
            assert(false, "JsonArray failed to create")
        }
    }

}