package edu.csh.chase.sprint.json

import java.io.IOException
import java.io.StringWriter
import java.io.Writer
import java.util.*

class JsonObject() {

    private val map = HashMap<String, Any?>()

    val length: Int
        get() {
            return map.size()
        }

    val keys: Iterator<String>
        get() {
            return map.keySet().iterator()
        }

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

    constructor(stringJson: String) : this(JsonTokener(stringJson))

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
        //TODO check validity of value, is it a value Json Value
        map[key] = value
        return this
    }

    //Setters

    //One setter that takes an Any? that ignores invalid types?

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
        return map[key]
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
            var addComa = false
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
                    if (addComa) {
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
                    writeValue(writer, map[key], indentFactor, newindent)
                    addComa = true
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

