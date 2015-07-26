package edu.csh.chase.sprint.json

import java.io.IOException
import java.io.StringWriter
import java.io.Writer
import java.util.*

class JsonObject() {

    private val map = HashMap<String, Any?>()

    constructor(tokener: JsonTokener) : this() {
        var c: Char
        var key: String

        if (tokener.nextClean() != '{') {
            throw tokener.syntaxError("A JSONObject text must begin with '{'")
        }
        while (true) {
            c = tokener.nextClean()
            when (c) {
                0.toChar() -> throw tokener.syntaxError("A JSONObject text must end with '}'")
                '}' -> return
                else -> {
                    tokener.back()
                    key = tokener.nextValue().toString()
                }

            }

            // The key is followed by ':'.
            c = tokener.nextClean()
            if (c != ':') {
                throw tokener.syntaxError("Expected a ':' after a key")
            }
            this.putOnce(key, tokener.nextValue())

            // Pairs are separated by ','.

            when (tokener.nextClean()) {
                ',' -> {
                    if (tokener.nextClean() == '}') {
                        return
                    }
                    tokener.back()
                }
                '}' -> return
                else -> throw tokener.syntaxError("Expected a ',' or '}'")

            }
        }
    }

    constructor(obj: JsonObject, vararg names: String) : this() {
        for (name in names) {
            putOnce(name, obj[name])
        }
    }

    constructor(map: HashMap<String, Any?>) : this() {
        for ((key, value) in map) {
            putOnce(key, value)
        }
    }

    private fun addKeyToValue(key: String, value: Any?) {
        map[key] = value
    }

    fun putOnce(key: String, value: Any?): JsonObject {
        if (key in map) {
            return this
        }
        map[key] = value
        return this
    }

    //Setters

    fun set(key: String, value: Int?) {
        addKeyToValue(key, value)
    }

    fun set(key: String, value: Boolean?) {
        addKeyToValue(key, value)
    }

    fun set(key: String, value: String?) {
        addKeyToValue(key, value)
    }

    //Getters

    fun get(key: String): Any? {
        return map [key]
    }

    fun get(key: String, default: Any): Any {
        if (key in map && map[key] != null) {
            return map[key]!!
        }
        return default
    }

    fun getInt(key: String): Int? {
        return get(key) as? Int
    }

    fun getInt(key: String, default: Int): Int {
        return getInt(key) ?: return default
    }

    fun getBoolean(key: String): Boolean? {
        return get(key) as? Boolean
    }

    fun getBoolean(key: String, default: Boolean): Boolean {
        return getBoolean(key) ?: return default
    }

    fun getString(key: String): String? {
        return get(key) as? String
    }

    fun getString(key: String, default: String): String {
        return getString(key) ?: return default
    }

    fun contains(key: String): Boolean {
        return key in map
    }

    override fun toString(): String {
        return toString(0)
    }

    /**
     * Make a prettyprinted JSON text of this JSONObject.
     * <p>
     * Warning: This method assumes that the data structure is acyclical.
     *
     * @param indentFactor
     *            The number of spaces to add to each level of indentation.
     * @return a printable, displayable, portable, transmittable representation
     *         of the object, beginning with <code>{</code>&nbsp<small>(left
     *         brace)</small> and ending with <code>}</code>&nbsp<small>(right
     *         brace)</small>.
     */
    fun toString(indentFactor: Int): String {
        val w = StringWriter()
        synchronized (w.getBuffer()) {
            return this.write(w, indentFactor, 0).toString()
        }
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

    /**
     * Write the contents of the JSONObject as JSON text to a writer. For
     * compactness, no whitespace is added.
     * <p>
     * Warning: This method assumes that the data structure is acyclical.
     *
     * @return The writer.
     * @throws JSONException
     */
    public fun write(writer: Writer): Writer {
        return this.write(writer, 0, 0)
    }

    fun writeValue(writer: Writer, value: Any?,
                   indentFactor: Int, indent: Int): Writer {
        if (value == null || value.equals(null)) {
            writer.write("null")
        } else if (value is JsonObject) {
            value.write(writer, indentFactor, indent)
        } else if (value is JsonArray) {
            value.write(writer, indentFactor, indent)
        } else if (value is Number) {
            writer.write(value.toString())
        } else if (value is Boolean) {
            writer.write(value.toString())
        } else {
            //A string
            quote(value.toString(), writer)
        }
        return writer
    }

    fun indent(writer: Writer, indent: Int) {
        writer.write(" " * indent)
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
                '\f' -> w.write("\\f");
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

    /**
     * Write the contents of the JSONObject as JSON text to a writer. For
     * compactness, no whitespace is added.
     * <p>
     * Warning: This method assumes that the data structure is acyclical.
     *
     * @return The writer.
     * @throws JSONException
     */
    fun write(writer: Writer, indentFactor: Int, indent: Int): Writer {
        try {
            var commanate = false
            val length = this.length()
            val keys = this.keys()
            writer.write("{")

            if (length == 1) {
                val key = keys.next()
                writer.write(quote(key.toString()))
                writer.write(":")
                if (indentFactor > 0) {
                    writer.write(" ")//* indentFactor?
                }
                writeValue(writer, map[key], indentFactor, indent)
            } else if (length != 0) {
                val newindent = indent + indentFactor
                while (keys.hasNext()) {
                    val key = keys.next()
                    if (commanate) {
                        writer.write(",")
                    }
                    if (indentFactor > 0) {
                        writer.write("\n")
                    }
                    indent(writer, newindent)
                    writer.write(quote(key.toString()))
                    writer.write(":")
                    if (indentFactor > 0) {
                        writer.write(" ")
                    }
                    writeValue(writer, this.map.get(key), indentFactor, newindent)
                    commanate = true
                }
                if (indentFactor > 0) {
                    writer.write("\n")
                }
                indent(writer, indent)
            }
            writer.write("}")
            return writer
        } catch (exception: IOException) {
            throw JsonException(exception)
        }
    }

}

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
