package json

import edu.csh.chase.sprint.json.jsonSerialize
import org.junit.Test
import kotlin.test.assertEquals

class JsonHelperFunctionTest {

    Test fun booleanJsonSerialize() {
        assertEquals("true", true.jsonSerialize())
        assertEquals("false", false.jsonSerialize())
        assertEquals("null", (null as Boolean?).jsonSerialize())
    }

}