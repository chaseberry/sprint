package json

import edu.csh.chase.sprint.json.JsonException
import edu.csh.chase.sprint.json.JsonObject
import org.junit.Test
import kotlin.test.assertEquals


class JsonObjectTest() {

    Test fun emptyObjectTest() {
        val obj = JsonObject()
        assertEquals(0, obj.size)
        assertEquals("{}", obj.toString())
        assertEquals("{\n}", obj.toString(true))
    }

    Test fun invalidJsonTest1() {
        val invalidJson = "{\"key\":\"value}"
        try {
            val obj = JsonObject(invalidJson)
        } catch(unterminatedString: JsonException) {
            return
        }
        assert(false, "Should have thrown a Unterminated string JsonException")
    }

    Test fun JsonGetTest1() {
        val jsonString = "{\"key\":\"value\"}"
        try {
            val obj = JsonObject(jsonString)
            assertEquals(1, obj.size)
            assertEquals("value", obj["key"] as String)
            assertEquals("value", obj["key", "defaultValue"] as String)
            assertEquals(null, obj["invalidKey"])
            assertEquals(null, obj["invalidKey"])
            assertEquals("value", obj.getString("key") as String)
            assertEquals("value", obj.getString("key", "defaultValue"))
            assertEquals("defaultValue", obj.getString("invalidKey", "defaultValue"))
            assertEquals(null, obj.getString("invalidKey"))
            assertEquals("{\"key\":\"value\"}", obj.toString())
        } catch(exception: JsonException) {
            assert(false, "Creating valid Json threw exception ${exception.getMessage()}")
        }
    }

    Test fun JsonSetTest1() {
        val obj = JsonObject()
        assertEquals(0, obj.size)

        try {
            obj["string"] = "aString"
            obj["int"] = 10
        } catch(invalidValue: JsonException) {
            assert(false, "Invalid value added to JsonObject ${invalidValue.getMessage()}")
        }

        assertEquals(2, obj.size)

        try {
            obj["double"] = 15.0
            obj["double"] = Double.NEGATIVE_INFINITY
            assert(false, "An infinite double was added to a JsonObject")
        } catch(invalidDouble: JsonException) {

        }

        assertEquals(15.0, obj["double"] as Double)

        try {
            obj["invalidType"] = "key" to "value"
            assert(false, "An invalid type was added to a JsonObject")
        } catch(invalidType: JsonException) {
        }

        assertEquals(3, obj.size)

    }

    Test fun additionObjectTest() {
        val obj1 = JsonObject().putOnce("a" to "1").putOnce("b" to "2").putOnce("c" to "3")
        val obj2 = JsonObject().putOnce("d" to "4").putOnce("e" to "5").putOnce("f" to "6")
        val addedObj = obj1 + obj2
        assertEquals(6, addedObj.size)
        assertEquals("1", addedObj["a"])
        assertEquals("3", addedObj["c"])
        assertEquals("4", addedObj["d"])
        assertEquals("6", addedObj["f"])
    }

}