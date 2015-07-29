package edu.csh.chase.sprint.json

import java.io.IOException
import java.io.StringWriter
import java.io.Writer
import java.util
import java.util.*

internal fun String.times(count: Int): String {
    if (count < 0) {
        return this
    }
    if (count == 0) {
        return ""
    }

    var str = ""

    for (z in 0..(count - 1)) {
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
 * @param string
 *            A String
 * @return A String correctly formatted for insertion in a JSON text.
 */
fun quote(string: String): String {
    val sw = StringWriter();
    synchronized (sw.getBuffer()) {
        return quote(string, sw).toString();
    }
}


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

fun indent(writer: Writer, indent: Int) {
    writer.write("   " * indent)
}

fun getJsonValue(value: Any?): String {
    return when (value) {
        null -> "null"
        is Collection<Any?> -> JsonArray(value.filter { it.isValidJsonType() }).toString()
        is Map<*, *> -> JsonObject(value.jsonMapFilter { it.value.isValidJsonType() }).toString()
        is String -> quote(value)
        is JsonSerializable -> value.jsonSerialize().toString()
        else -> value.toString()
    }
}

internal fun Any?.isValidJsonType(): Boolean {
    return this is Boolean? || this is Int? || this is Double? || this is String? || this is Collection<Any?>
            || this is Map<*, *> || this is JsonSerializable?
}

internal fun Map<*, *>.jsonMapFilter(filterFun: (Map.Entry<Any?, Any?>) -> (Boolean)): Map<String, Any?> {
    val map = HashMap<String, Any?>()
    this.forEach {
        if (filterFun(it) && it.key is String) {
            map.put(it.key as String, it.value)
        }
    }
    return map
}

fun Int.jsonSerialize(): String {
    return this.toString()
}

fun String.jsonSerialize(): String {
    return quote(this)
}

fun Map<Any?, Any?>.filter(filterFun: (Map.Entry<Any?, Any?>) -> (Boolean)): Map<Any?, Any?> {
    val map = HashMap<Any?, Any?>()
    this.forEach {
        if (filterFun(it)) {
            map.put(it.key, it.value)
        }
    }
    return map
}