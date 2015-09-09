package json

import edu.csh.chase.sprint.json.JsonObject
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class JsonMultiTraverseTest {

    val obj1 = JsonObject().put("key", "value").put("k2", "v2").put("k3", "v3")
    val obj2 = JsonObject().put("a", "b").put("c", "d").put("e", "f").put("obj", obj1)

    Test fun traverseMultiBasic() {
        assertEquals(obj1.traverseMulti("k", "ke", "key"), "value")
        assertNull(obj1.traverseMulti("k", "ke"))
        assertEquals(obj2.traverseMulti("z", "y", "v:xz", "obj:key"), "value")
        assertEquals(obj2.traverseMulti("obj:key", "v:xw"), "value")
        assertEquals(obj2.traverseMulti("obj:k3", "v:wq"), "v3")
        assertEquals(obj2.traverseMulti("v:wq", "y:t", "obj:k2"), "v2")
    }

}