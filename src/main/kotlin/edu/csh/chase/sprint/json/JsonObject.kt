package edu.csh.chase.sprint.json

import java.io.IOException
import java.io.StringWriter
import java.io.Writer
import java.util.*

class JsonObject() : JsonBase() {

    private val map = HashMap<String, Any?>()

    override val size: Int
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
                0.toChar() -> throw tokener.syntaxError("A JSONObject text must end with '}'")//EOF
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

    constructor(map: Map<String, Any?>) : this() {
        for ((key, value) in map) {
            putOnce(key, value)
        }
    }

    private fun addKeyToValue(key: String, value: Any?) {
        if (!value.isValidJsonType()) {
            throw JsonException("$value is not a valid type for Json.")
        }
        if (value is Double && (value.isInfinite() || value.isNaN())) {
            throw JsonException("Doubles must be finite and real")
        }
        map[key] = value
    }

    fun putOnce(key: String, value: Any?): JsonObject {
        if (key in map) {
            return this//Throw an error?
        }
        //TODO check validity of value, is it a value Json Value
        addKeyToValue(key, value)
        return this
    }

    //Setters

    //One setter that takes an Any? that excepts on invalid types

    fun set(key: String, value: Any?) {
        val realValue: Any? = if (value is Collection<Any?>) {
            JsonArray(value)
        } else value
        addKeyToValue(key, realValue)
    }

    //Special function needed to type check Maps
    fun set(key: String, value: Map<String, Any?>) {
        addKeyToValue(key, JsonObject(map))
    }

    //Putters
    fun put(key: String, value: Any?): JsonObject {
        map[key] = value
        return this
    }

    fun put(keyValuePair: Pair<String, Any?>): JsonObject {
        return put(keyValuePair.first, keyValuePair.second)
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
        return toString(false)
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
    fun toString(shouldIndent: Boolean): String {
        val writer = StringWriter()
        synchronized (writer.getBuffer()) {
            return this.write(writer, shouldIndent).toString()
        }
    }


    /**
     * Write the contents of the JSONObject as JSON text to a writer. For
     * compactness, no whitespace is added.
     * <p>
     * Warning: This method assumes that the data structure is acyclical.
     *
     * @return The writer.
     */
    public fun write(writer: Writer): Writer {
        return this.write(writer, false)
    }


    /**
     * Write the contents of the JSONObject as JSON text to a writer. For
     * compactness, no whitespace is added.
     * <p>
     * Warning: This method assumes that the data structure is acyclical.
     *
     * @return The writer.
     */
    fun write(writer: Writer, shouldIndent: Boolean, depth: Int = 1): Writer {
        try {
            var addComa = false
            writer.write("{")
            for ((key, value) in map) {
                if (addComa) {
                    writer.write(",")
                }

                if (shouldIndent) {
                    writer.write("\n")
                    indent(writer, depth)
                }

                writer.write(quote(key))
                writer.write(":")
                if (shouldIndent) {
                    writer.write(" ")
                }
                writer.write(getJsonValue(value))
                addComa = true
            }
            if (shouldIndent) {
                writer.write("\n")
            }
            writer.write("}")
            return writer
        } catch (exception: IOException) {
            throw JsonException(exception)
        }
    }

}

