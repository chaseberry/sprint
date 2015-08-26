package json

import edu.csh.chase.sprint.json.JsonObject
import org.junit.Test
import kotlin.test.assertEquals

class JsonTraverseTest {

    Test fun traverseObjectTest() {
        val obj = JsonObject()
        obj["key"] = "value"
        obj["test"] = "always"

        assertEquals("value", obj.traverse("key"))
        assertEquals("value", obj.traverse("key:test"))
        assertEquals("always", obj.traverse("test"))
        assertEquals(null, obj.traverse("invalid"))
        assertEquals("default", obj.traverse(compoundKey = "invalid", default = "default"))
    }

}