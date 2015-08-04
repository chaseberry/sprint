package json

import edu.csh.chase.sprint.json.JsonDelegates
import edu.csh.chase.sprint.json.JsonObject
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class JsonDelegatesTest {

    Test fun delegatesObjectVal() {
        val obj = JsonObject(mapOf("key" to "value", "key2" to "value2"))
        val delegateTest = JsonDelegateObjectValTest(obj)
        assertEquals("value", delegateTest.key)
        assertEquals("value2", delegateTest.key2)
        assertNull(delegateTest.invalidKey)
    }

    class JsonDelegateObjectValTest(obj: JsonObject) {

        val key: String? by JsonDelegates.objectVal(obj)
        val key2: String? by JsonDelegates.objectVal(obj)
        val invalidKey: String? by JsonDelegates.objectVal(obj)

    }

    Test fun delegatesObjectVar() {
        val obj = JsonObject(mapOf("key" to "value", "key2" to "value2"))
        val delegateObj = JsonDelegateObjectVarTest(obj)
        assertEquals("value", delegateObj.key)
        assertEquals("value2", delegateObj.key2)
        assertNull(delegateObj.invalidKey)

        delegateObj.key = "newValue"
        delegateObj.invalidKey = 15

        assertEquals("newValue", delegateObj.key)
        assertEquals(15, delegateObj.invalidKey)

        //Test to make sure it got set back into the JsonObject
        assertEquals("newValue", obj["key"])
        assertEquals(15, obj["invalidKey"])
    }

    class JsonDelegateObjectVarTest(obj: JsonObject) {
        var key: String? by JsonDelegates.objectVar(obj)
        var key2: String? by JsonDelegates.objectVar(obj)
        var invalidKey: Int? by JsonDelegates.objectVar(obj)
    }

}
