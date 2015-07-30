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

    Test fun jsonArrayFromStringTest() {
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

    Test fun jsonArrayGetTest() {
        val array = JsonArray(arrayOf<Any?>(0, "String", null, false, 15.687))

        assertEquals(5, array.size)
        assertTrue(3 in array)
        assertFalse(5 in array)
        assertEquals(0..4, array.indices)

        assertEquals(0, array[0] as Int)
        assertEquals(0, array[0, 5] as Int)

        assertEquals("String", array[1] as String)
        assertEquals("String", array[1, "Not String"] as String)

        assertNull(array[2])
        assertNull(array[2, "Some not null value"])

        assertEquals(false, array[3] as Boolean)
        assertEquals(false, array[3, true] as Boolean)

        assertEquals(15.687, array[4] as Double)
        assertEquals(15.687, array[4, 1.0] as Double)
    }


}