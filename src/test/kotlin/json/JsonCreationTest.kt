package json

import edu.csh.chase.sprint.json.JsonArray
import edu.csh.chase.sprint.json.JsonObject
import edu.csh.chase.sprint.json.json
import org.junit.Test
import kotlin.test.assertEquals

class JsonCreationTest {

    Test fun testObjectCreationEmpty() {
        val obj = JsonObject()
        assertEquals(obj, json { arrayOf() })
    }

    Test fun testObjectCreation() {
        val obj = JsonObject("key" to "value")
        assertEquals(obj, json {
            arrayOf(
                    "key" to "value"
            )
        })
    }

    Test fun testArrayCreationEmpty() {
        val arr = JsonArray()
        assertEquals(arr, json.get())
    }

    Test fun testArrayCreation() {
        val arr = JsonArray().put("value")
        assertEquals(arr, json[
                "value"
                ])
    }

}