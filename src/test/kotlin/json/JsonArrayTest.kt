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
        val array = JsonArray(*arrayOf<Any?>(0, "String", null, false, 15.687))

        assertEquals(5, array.size)
        assertTrue(3 in array)
        assertFalse(5 in array)
        assertEquals(0..4, array.indices)

        assertEquals(0, array[0] as Int)
        assertEquals(0, array[0, 5] as Int)

        assertEquals("String", array[1] as String)
        assertEquals("String", array[1, "Not String"] as String)

        assertNull(array[2])

        assertEquals(false, array[3] as Boolean)
        assertEquals(false, array[3, true] as Boolean)

        assertEquals(15.687, array[4] as Double)
        assertEquals(15.687, array[4, 1.0] as Double)
    }

    Test fun jsonArrayGetDefaultTest() {
        val array = JsonArray(arrayOf<Any?>(0, "String", null, false, 15.687))

        assertEquals(15, array[-1, 15])
        assertEquals("default", array[12, "default"])
    }

    Test fun jsonArrayGetBooleanTest() {
        val array = JsonArray(*arrayOf<Any?>(true, false, null, "Not a Boolean"))
        assertEquals(4, array.size)

        assertTrue(array.getBoolean(0)!!)
        assertTrue(array.getBoolean(0, false))

        assertFalse(array.getBoolean(1)!!)
        assertFalse(array.getBoolean(1, true))

        assertNull(array.getBoolean(2))
        assertTrue(array.getBoolean(2, true))

        assertNull(array.getBoolean(3))
        assertTrue(array.getBoolean(3, true))
    }

    Test fun jsonArrayGetIntTest() {
        val array = JsonArray(*arrayOf<Any?>(15, 0, null, 15.9, "A String"))
        assertEquals(5, array.size)

        assertEquals(15, array.getInt(0)!!)
        assertEquals(15, array.getInt(0, 0))

        assertEquals(0, array.getInt(1) as Int)

        assertNull(array.getInt(2))
        assertEquals(12, array.getInt(2, 12))

        assertNull(array.getInt(3))
        assertEquals(12, array.getInt(3, 12))

        assertNull(array.getInt(4))
        assertEquals(-15, array.getInt(4, -15))
    }

}