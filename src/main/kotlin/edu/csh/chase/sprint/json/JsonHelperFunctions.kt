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


fun quote(string: String?, w: Writer): Writer {
    if (string == null || string.length() == 0) {
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


/**
 * Make a JSON text of an Object value. If the object has an
 * value.toJSONString() method, then that method will be used to produce the
 * JSON text. The method is required to produce a strictly conforming text.
 * If the object does not contain a toJSONString method (which is the most
 * common case), then a text will be produced by other means. If the value
 * is an array or Collection, then a JSONArray will be made from it and its
 * toJSONString method will be called. If the value is a MAP, then a
 * JSONObject will be made from it and its toJSONString method will be
 * called. Otherwise, the value's toString method will be called, and the
 * result will be quoted.
 *
 * <p>
 * Warning: This method assumes that the data structure is acyclical.
 *
 * @param value
 *            The value to be serialized.
 * @return a printable, displayable, transmittable representation of the
 *         object, beginning with <code>{</code>&nbsp<small>(left
 *         brace)</small> and ending with <code>}</code>&nbsp<small>(right
 *         brace)</small>.
 */
fun valueToString(value: Any?): String? {
    return when (value) {
        null -> "null"
        is Number -> numberToString(value)
        is Boolean, is JsonObject, is JsonArray -> value.toString()
        else -> quote(value.toString())
    }
}


/**
 * Produce a string from a Number.
 *
 * @param number
 *            A Number
 * @return A String.
 * @throws JSONException
 *             If n is a non-finite number.
 */
fun numberToString(number: Number): String? {
    if (number is Double) {
        if (number.isInfinite() || number.isNaN()) {
            return null
        }
    }

    // Shave off trailing zeros and decimal point, if possible.
    var string = number.toString();
    if (string.indexOf('.') > 0 && string.indexOf('e') < 0 && string.indexOf('E') < 0) {
        while (string.endsWith("0")) {
            //remove trailing 0s after the decimal
            string = string.substring(0, string.length() - 1);
        }
        if (string.endsWith(".")) {
            //change 546. to just 546
            string = string.substring(0, string.length() - 1);
        }
    }
    return string;
}

//TODO scrap this for a better version
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