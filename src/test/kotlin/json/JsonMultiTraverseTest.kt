package json

import edu.csh.chase.sprint.json.JsonObject
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class JsonMultiTraverseTest {

    val obj1 = JsonObject().put("key", "value").put("k2", "v2").put("k3", "v3")
    val obj2 = JsonObject().put("a", "b").put("c", "d").put("e", "f").put("obj", obj1)

    Test fun traverseMultiBasic() {
        assertEquals("value", obj1.traverseMulti("k", "ke", "key"))
        assertNull(obj1.traverseMulti("k", "ke"))
        assertEquals("value", obj2.traverseMulti("z", "y", "v:xz", "obj:key"))
        assertEquals("value", obj2.traverseMulti("obj:key", "v:xw"))
        assertEquals("v3", obj2.traverseMulti("obj:k3", "v:wq"))
        assertEquals("v2", obj2.traverseMulti("v:wq", "y:t", "obj:k2"))
    }

    Test fun traverseMultiDefault() {
        assertEquals("def", obj1.traverseMultiWithDefault("def", "k", "k7"))
    }

}