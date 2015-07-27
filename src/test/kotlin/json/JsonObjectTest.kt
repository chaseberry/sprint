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

    Test fun invalidJson1Test() {
        val invalidJson = "{\"key\":\"value}"
        try {
            val obj = JsonObject(invalidJson)
        } catch(unterminatedString: JsonException) {
            return
        }
        assert(false, "Should have thrown a Unterminated string JsonException")
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