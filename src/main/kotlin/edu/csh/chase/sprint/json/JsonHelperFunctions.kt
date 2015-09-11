package edu.csh.chase.sprint.json

import java.io.StringWriter
import java.io.Writer
import java.util.HashMap

private fun String.times(count: Int): String {
    if (count < 0) {
        return this
    }
    if (count == 0) {
        return ""
    }

    var str = ""

    for (z in 1..count) {
        str += this
    }

    return str

}


/**
 * Produce a string in double quotes with backslash sequences in all the
 * right places. A backslash will be inserted within </, producing <\/,
 * allowing JSON text to be delivered in HTML. In JSON text, a string cannot
 * contain a control character or an unescaped quote or backslash.
 *
 * @param string A String to quote and escape
 * @return A String correctly formatted for insertion in a JSON text.
 */
fun quote(string: String): String {
    val sw = StringWriter();
    synchronized (sw.getBuffer()) {
        return quote(string, sw).toString();
    }
}

/**
 * Produce a string in double quotes with backslash sequences in all the
 * right places. A backslash will be inserted within </, producing <\/,
 * allowing JSON text to be delivered in HTML. In JSON text, a string cannot
 * contain a control character or an unescaped quote or backslash.
 *
 * @param string A String to quote and escape
 * @param w A Writer to write the String to
 * @return A String correctly formatted for insertion in a JSON text.
 */
fun quote(string: String, w: Writer): Writer {
    if (string.length() == 0) {
        w.write("\"\"");
        return w;
    }

    var b: Char
    var c: Char = 0.toChar()

    w.write("\"");
    for (z in string.indices) {
        b = c;//before
        c = string.charAt(z);//current
        when (c) {
            '\\', '"' -> {
                w.write("\\\\");
                w.write(c.toString());
            }
            '/' -> {
                if (b == '<') {
                    // /<?
                    w.write("\\\\");
                }
                w.write(c.toString());
            }
            '\b' -> w.write("\\b");
            '\t' -> w.write("\\t");
            '\n' -> w.write("\\n");
            '\u000C' -> w.write("\\f");
            '\r' -> w.write("\\r");
            else -> {
                if (c < ' ' || (c >= '\u0080' && c < '\u00a0')
                        || (c >= '\u2000' && c < '\u2100')) {
                    w.write("\\u");
                    val hhhh = Integer.toHexString(c.toInt());
                    w.write("0000", 0, 4 - hhhh.length());
                    w.write(hhhh);
                } else {
                    w.write(c.toString());
                }
            }
        }
    }
    w.write("\"");
    return w;
}

/**
 * Adds n tabs(Actually 3 * n spaces. Spaces > tabs) to the calling writer
 *
 * @param indent The number of tabs(3 spaces) to add
 */
fun Writer.indent(indent: Int) = write("   " * indent)

fun getJsonValue(value: Any?): String {
    return when (value) {
        null -> "null"
        is Collection<Any?> -> JsonArray(value.filter { it.isValidJsonType() }).toString()
        is Map<*, *> -> JsonObject(value.jsonMapFilter { it.value.isValidJsonType() }).toString()
        is String -> quote(value)
        is JsonSerializable -> value.jsonSerialize()
        else -> value.toString()
    }
}

fun Any?.isValidJsonType(): Boolean {
    return this is Boolean? || this is Int? || this is Double? || this is String? || this is Collection<Any?>
            || this is Map<*, *> || this is JsonSerializable?
}

private fun Map<*, *>.jsonMapFilter(filterFun: (Map.Entry<Any?, Any?>) -> (Boolean)): Map<String, Any?> {
    val map = HashMap<String, Any?>()
    this.forEach {
        if (filterFun(it) && it.key is String) {
            map.put(it.key as String, it.value)
        }
    }
    return map
}

/**
 * Takes a map of String to Any? and creates a Json representation
 * Any invalid values in the map will cause a JsonException to be thrown
 *
 * @return String a valid JsonObject String from this Map
 */
fun Map<String, Any?>.jsonSerialize(): String {
    return JsonObject(this).toString()
}

/**
 * Takes a Collection of Any? and creates a Json representation
 * Any invalid values in the collection will cause a JsonException to be thrown
 *
 * @return String a valid JsonObject String from this Collection
 */
fun Collection<Any?>.jsonSerialize(): String {
    return JsonArray(this).toString()
}

/**
 * Makes this Int a valid Json representation
 *
 * @return The String version of the int or "null"
 */
fun Int?.jsonSerialize(): String {
    return this.toString()
}

/**
 * Makes this String a valid Json representation
 *
 * @return The Quoted, escaped version or "null"
 */
fun String?.jsonSerialize(): String {
    return if (null == this) "null" else quote(this)
}

/**
 * Makes this Double a valid Json representation
 *
 * @return The String version of this Double or "null"
 */
fun Double?.jsonSerialize(): String {
    return this.toString()
}

/**
 * Makes this Boolean a valid Json representation
 *
 * @return The String version of this boolean or "null"
 */
fun Boolean?.jsonSerialize(): String {
    return this.toString()
}

/**
 * A function that will attempt to create a JsonObject or JsonArray from a given String
 *
 * @param jsonString The String to attempt to be read as json
 * @return A valid JsonObject, JsonArray or null if it failed to parse the Json
 */
fun parseJsonString(jsonString: String): JsonBase? {
    try {
        return JsonObject(jsonString)
    } catch(excep: JsonException) {

    }
    try {
        return JsonArray(jsonString)
    } catch(excep: JsonException) {

    }
    return null
}
