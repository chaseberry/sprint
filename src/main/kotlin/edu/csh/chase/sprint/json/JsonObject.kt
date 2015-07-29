package edu.csh.chase.sprint.json

import java.io.IOException
import java.io.StringWriter
import java.io.Writer
import java.util.*

class JsonObject() : JsonBase(), Iterable<Map.Entry<String, Any?>> {

    private val map = HashMap<String, Any?>()

    override val size: Int
        get() {
            return map.size()
        }

    val keys: Iterator<String>
        get() {
            return map.keySet().iterator()
        }

    val keySet: Set<String>
        get() {
            return map.keySet()
        }

    constructor(tokener: JsonTokener) : this() {
        if (tokener.nextClean() != '{') {
            throw tokener.syntaxError("A JSONObject text must begin with '{'")
        }

        var key: String

        while (true) {
            when (tokener.nextClean()) {
                0.toChar() -> throw tokener.syntaxError("A JSONObject text must end with '}'")//EOF
                '}' -> return
                else -> {
                    tokener.back()
                    key = tokener.nextValue().toString()
                }

            }

            // The key is followed by ':'.
            if (tokener.nextClean() != ':') {
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
        addKeyToValue(key, value)
        return this
    }

    fun putOnce(keyValuePair: Pair<String, Any?>): JsonObject {
        return putOnce(keyValuePair.first, keyValuePair.second)
    }

    //Setters

    fun set(key: String, value: Any?) {
        addKeyToValue(key, value)
    }

    //Putters
    fun put(key: String, value: Any?): JsonObject {
        addKeyToValue(key, value)
        return this
    }

    fun put(keyValuePair: Pair<String, Any?>): JsonObject {
        return put(keyValuePair.first, keyValuePair.second)
    }

    fun putNotNull(key: String, value: Any?): JsonObject {
        if (value == null) {
            return this
        }
        addKeyToValue(key, value)
        return this
    }

    fun putNotNull(keyValuePair: Pair<String, Any?>): JsonObject {
        return putNotNull(keyValuePair.first, keyValuePair.second)
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

    fun getDouble(key: String): Double? {
        return get(key) as? Double
    }

    fun getDouble(key: String, default: Double): Double {
        return getDouble(key) ?: return default
    }

    fun getJsonObject(key: String): JsonObject? {
        return get(key) as? JsonObject
    }

    fun getJsonObject(key: String, default: JsonObject): JsonObject {
        return getJsonObject(key) ?: return default
    }

    fun getJsonArray(key: String): JsonArray? {
        return get(key) as? JsonArray
    }

    fun getJsonArray(key: String, default: JsonArray): JsonArray {
        return getJsonArray(key) ?: return default
    }

    //Other functions

    /**
     * Removes all values from this JsonObject
     * No data is saved and the size is reset to 0
     *
     * @return Map<String, Any?> a copy of the key,value pairs that were housed in this JsonObject
     */
    fun clear(): HashMap<String, Any?> {
        val mapClone = HashMap<String, Any?>(map)
        map.clear()
        return mapClone
    }


    fun contains(key: String): Boolean {
        return key in map
    }

    fun isNull(key: String): Boolean {
        return key in this && get(key) == null
    }

    fun plus(other: JsonObject): JsonObject {
        val newJson = JsonObject(map)
        for ((key, value) in other) {
            newJson.putOnce(key, value)
        }
        return newJson
    }

    override fun equals(other: Any?): Boolean {
        //TODO check each key value pair?
        return other is JsonObject && other.map == map
    }

    override fun toString(): String {
        return toString(false)
    }

    override fun jsonSerialize(): JsonBase {
        return this
    }

    override fun iterator(): Iterator<Map.Entry<String, Any?>> {
        return map.iterator()
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

