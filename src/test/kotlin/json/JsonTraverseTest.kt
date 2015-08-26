package json

import edu.csh.chase.sprint.json.JsonArray
import edu.csh.chase.sprint.json.JsonObject
import org.junit.Test
import kotlin.test.assertEquals

class JsonTraverseTest {

    Test fun traverseBaseObjectTest() {
        val jsonObject = JsonObject()
        val obj = jsonObject
        obj["key"] = "value"
        obj["test"] = 15

        assertEquals("value", obj.traverse("key"))
        assertEquals("value", obj.traverse("key:test"))
        assertEquals(15, obj.traverse("test"))
        assertEquals(null, obj.traverse("invalid"))
        assertEquals("default", obj.traverse(compoundKey = "invalid", default = "default"))
    }

    Test fun traverseBaseArrayTest() {
        val arr = JsonArray("value", 15)

        assertEquals("value", arr.traverse("0"))
        assertEquals("value", arr.traverse("0:1"))
        assertEquals(15, arr.traverse("1"))
        assertEquals(null, arr.traverse("3"))
        assertEquals(null, arr.traverse("invalid"))
        assertEquals("default", arr.traverse(compoundKey = "invalid", default = "default"))
        assertEquals("default", arr.traverse(compoundKey = "-1", default = "default"))
    }

}