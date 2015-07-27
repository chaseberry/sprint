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

}