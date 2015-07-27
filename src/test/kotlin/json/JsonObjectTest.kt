package json

import edu.csh.chase.sprint.json.JsonObject
import org.junit.Test
import kotlin.test.assertEquals


class JsonObjectTest() {

    Test fun emptyObjectTest() {
        val obj = JsonObject()
        assertEquals(0, obj.size)
    }

}