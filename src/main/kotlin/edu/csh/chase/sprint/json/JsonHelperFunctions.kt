package edu.csh.chase.sprint.json

import java.io.IOException
import java.io.StringWriter
import java.io.Writer
import java.util
import java.util.*

internal fun String.times(indent: Int): String {
    if (indent < 0) {
        return this
    }
    if (indent == 0) {
        return ""
    }

    var str = this

    for (z in 0..indent) {
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
        try {
            return quote(string, sw).toString();
        } catch (ignored: IOException) {
            // will never happen - we are writing to a string writer
            return "";
        }
    }
}

//TODO change this to return a quoted String, not write to a writer
fun quote(string: String?, w: Writer): Writer {
    if (string == null || string.length() == 0) {
        w.write("\"\"");
        return w;
    }

    var b: Char
    var c: Char = 0.toChar()
    var hhhh: String
    var i: Int
    val len = string.length()

    w.write("\"");
    for (z in 0..(string.length() - 1)) {
        b = c;
        c = string.charAt(z);
        when (c) {
            '\\', '"' -> {
                w.write("\\\\");
                w.write(c.toString());
            }
            '/' -> {
                if (b == '<') {
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
                    hhhh = Integer.toHexString(c.toInt());
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
    writer.write("    " * indent)
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
fun writeValue(writer: Writer, value: Any?): Writer {
    when (value) {
        null -> writer.write("null")
        is Collection<Any?> -> writer.write(JsonArray(value.filter { it.isValidJsonType() }).toString())
    //the is Map<*, *> should autocast value to a Map<Any?, Any?> for the filter, but apperently not
        is Map<*, *> -> writer.write(JsonObject((value as Map<Any?, Any?>).jsonMapFilter { it.value.isValidJsonType() }).toString())
        is String -> writer.write(quote(value))
        else -> writer.write(value.toString())
    }
    return writer
}

internal fun Any?.isValidJsonType(): Boolean {
    return this is Boolean? || this is Int? || this is Double? || this is JsonObject? || this is String?
            || this is JsonArray? || this is Long? || this is Collection<Any?> || this is Map<*, *>
}

internal fun Map<Any?, Any?>.jsonMapFilter(filterFun: (Map.Entry<Any?, Any?>) -> (Boolean)): Map<String, Any?> {
    val map = HashMap<String, Any?>()
    this.forEach {
        if (filterFun(it) && it.key is String) {
            map.put(it.key as String, it.value)
        }
    }
    return map
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