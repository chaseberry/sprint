package edu.csh.chase.sprint.json

import java.io.IOException
import java.io.StringWriter
import java.io.Writer
import java.util.HashMap

class JsonObject() : JsonBase(), Iterable<Map.Entry<String, Any?>> {

    private val map = HashMap<String, Any?>()

    /**
     * The number of key, value pairs this JsonObject currently holds
     */
    override val size: Int
        get() {
            return map.size()
        }

    /**
     * An iterator over the set of keys this JsonObject currently holds
     */
    val keys: Iterator<String>
        get() {
            return map.keySet().iterator()
        }

    /**
     * A set of the keys this JsonObject currently holds
     */
    val keySet: Set<String>
        get() {
            return map.keySet()
        }

    /**
     * Creates an instance of a JsonObject with a JsonTokener
     * Parses the tokens to create a JsonObject
     * An invalid JsonTokener will cause a JsonException to be thrown
     */
    constructor(tokener: JsonTokener) : this() {
        if (tokener.nextClean() != '{') {
            throw tokener.syntaxError("A JsonObject text must begin with '{'")
        }

        var key: String

        while (true) {
            when (tokener.nextClean()) {
                0.toChar() -> throw tokener.syntaxError("A JsonObject text must end with '}'")//EOF
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

    /**
     * Constructs a JsonObject from a string
     * An invalid JsonString will cause a JsonException to be thrown
     */
    constructor(stringJson: String) : this(JsonTokener(stringJson))

    /**
     * Constructs a clone of the provided JsonObject with only the specified keys
     */
    constructor(obj: JsonObject, vararg names: String) : this() {
        for (name in names) {
            putOnce(name, obj[name])
        }
    }

    /**
     * Constructs a JsonObject from a map of <String, Any?>
     */
    constructor(map: Map<String, Any?>) : this() {
        for ((key, value) in map) {
            putOnce(key, value)
        }
    }

    /**
     * Constructs a JsonObject from a list of key, value Pairs
     * Any key,value with a value that is not a valid json type will be ignored
     */
    constructor(vararg elementList: Pair<String, Any?>) : this() {
        elementList.filter { it.second.isValidJsonType() }.forEach {
            putOnce(it)
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

    /**
     * Adds the key and value to this JsonObject only if key is not already present
     * @param key The key for the value
     * @param value The value assigned to the specified key
     * @return This JsonObject the key, value were placed into
     */
    fun putOnce(key: String, value: Any?): JsonObject {
        if (key in map) {
            return this//Throw an error?
        }
        addKeyToValue(key, value)
        return this
    }

    /**
     * Adds the key and value to this JsonObject only if key is not already present
     * @param keyValuePair A key, value pair to add to this JsonObject
     * @return This JsonObject the pair was placed into
     */
    fun putOnce(keyValuePair: Pair<String, Any?>): JsonObject {
        return putOnce(keyValuePair.first, keyValuePair.second)
    }

    //Setters

    /**
     * A key, value set function, in Kotlin this can be invoked as jsonObject["key"] = "value"
     *
     * @param key String a key for this JsonObject. Will overwrite any data previously stored in this key
     * @param value Any? a value to be stored at this key. This must be a valid Json type or an exception will be thrown
     *
     * @throws JsonException an unchecked exception will be thrown if the passed value is not a valid Json value type
     */
    fun set(key: String, value: Any?) {
        addKeyToValue(key, value)
    }

    //Putters
    /**
     * Puts a mapping of key to value in the given object
     * This function is used to chain calls together for simplicity
     *
     * @param key A String key
     * @param value A valid json value
     *
     * @return JsonObject the JsonObject the key,value pair was put into
     */
    fun put(key: String, value: Any?): JsonObject {
        addKeyToValue(key, value)
        return this
    }

    /**
     * Puts a mapping of a key to value in the given object
     * This function can be used to chain calls together for simplicity
     *
     * @param keyValuePair A Pair<String,Any?> of a key to value
     *
     * @return JsonObect the JsonObject the pair was put into
     */
    fun put(keyValuePair: Pair<String, Any?>): JsonObject {
        return put(keyValuePair.first, keyValuePair.second)
    }

    /**
     * Takes a Any? and only adds it to the given key if the value is not null
     * This function can be used to chain calls together for simplicity
     *
     * @param key A String key
     * @param value A valid json value
     *
     * @return JsonObject the JsonObject the key,value pair was put into
     */
    fun putNotNull(key: String, value: Any?): JsonObject {
        if (value == null) {
            return this
        }
        addKeyToValue(key, value)
        return this
    }

    /**
     * Takes a Pair of <String, Any?> and only adds it to the given key if the value is not null
     * This function can be used to chain calls together for simplicity
     *
     * @param keyValuePair A Pair<String,Any?> of a key to value
     *
     * @return JsonObect the JsonObject the pair was put into
     */
    fun putNotNull(keyValuePair: Pair<String, Any?>): JsonObject {
        return putNotNull(keyValuePair.first, keyValuePair.second)
    }

    //Getters
    /**
     * Gets the value from a given key
     *
     * @param key The key to pull the value from
     * @return The value corresponding to the given key, null if no value was found
     */
    fun get(key: String): Any? {
        return map[key]
    }

    /**
     * Gets the value from a given key
     *
     * @param key The key to pull the value from
     * @param default The default value to return if null is found
     * @return The value corresponding to the given key, default if no value was found
     */
    fun get(key: String, default: Any): Any {
        if (map[key] != null) {
            return map[key]!!
        }
        return default
    }

    /**
     * Gets the value from a given key
     *
     * @param key The key to pull the value from
     * @return The value corresponding to the given key, null if no value was found
     */
    fun getInt(key: String): Int? {
        return get(key) as? Int
    }

    /**
     * Gets the value from a given key
     *
     * @param key The key to pull the value from
     * @param default The default value to return if null is found
     * @return The value corresponding to the given key, default if no value was found
     */
    fun getInt(key: String, default: Int): Int {
        return getInt(key) ?: return default
    }

    /**
     * Gets the value from a given key
     *
     * @param key The key to pull the value from
     * @return The value corresponding to the given key, null if no value was found
     */
    fun getBoolean(key: String): Boolean? {
        return get(key) as? Boolean
    }

    /**
     * Gets the value from a given key
     *
     * @param key The key to pull the value from
     * @param default The default value to return if null is found
     * @return The value corresponding to the given key, default if no value was found
     */
    fun getBoolean(key: String, default: Boolean): Boolean {
        return getBoolean(key) ?: return default
    }

    /**
     * Gets the value from a given key
     *
     * @param key The key to pull the value from
     * @return The value corresponding to the given key, null if no value was found
     */
    fun getString(key: String): String? {
        return get(key) as? String
    }

    /**
     * Gets the value from a given key
     *
     * @param key The key to pull the value from
     * @param default The default value to return if null is found
     * @return The value corresponding to the given key, default if no value was found
     */
    fun getString(key: String, default: String): String {
        return getString(key) ?: return default
    }

    /**
     * Gets the value from a given key
     *
     * @param key The key to pull the value from
     * @return The value corresponding to the given key, null if no value was found
     */
    fun getDouble(key: String): Double? {
        return get(key) as? Double
    }

    /**
     * Gets the value from a given key
     *
     * @param key The key to pull the value from
     * @param default The default value to return if null is found
     * @return The value corresponding to the given key, default if no value was found
     */
    fun getDouble(key: String, default: Double): Double {
        return getDouble(key) ?: return default
    }

    /**
     * Gets the value from a given key
     *
     * @param key The key to pull the value from
     * @return The value corresponding to the given key, null if no value was found
     */
    fun getJsonObject(key: String): JsonObject? {
        return get(key) as? JsonObject
    }

    /**
     * Gets the value from a given key
     *
     * @param key The key to pull the value from
     * @param default The default value to return if null is found
     * @return The value corresponding to the given key, default if no value was found
     */
    fun getJsonObject(key: String, default: JsonObject): JsonObject {
        return getJsonObject(key) ?: return default
    }

    /**
     * Gets the value from a given key
     *
     * @param key The key to pull the value from
     * @return The value corresponding to the given key, null if no value was found
     */
    fun getJsonArray(key: String): JsonArray? {
        return get(key) as? JsonArray
    }

    /**
     * Gets the value from a given key
     *
     * @param key The key to pull the value from
     * @param default The default value to return if null is found
     * @return The value corresponding to the given key, default if no value was found
     */
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

    /**
     * Check to see if a given key exists in the map
     * This function does not care about the value if found
     *
     * @return Boolean true if a key exists in this JsonObject, false otherwise
     */
    fun contains(key: String): Boolean {
        return key in map
    }

    /**
     * Check to see if a given key exists and it's value is null
     *
     * @return Boolean true if the key exists and value of key is null, false otherwise
     */
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
        return other is JsonObject && other.map == map
    }

    override fun jsonSerialize(): String {
        return this.toString(false)
    }

    /**
     * Gets an iterator of key, Value pairs
     * This can be used in a foreach loop to loop over all pairs in this JsonObject
     *
     * @return an iterator of key, Value pairs
     */
    override fun iterator(): Iterator<Map.Entry<String, Any?>> {
        return map.iterator()
    }

    override fun toString(): String {
        return toString(false)
    }

    override fun toString(shouldIndent: Boolean, depth: Int): String {
        val writer = StringWriter()
        synchronized (writer.getBuffer()) {
            return this.write(writer, shouldIndent, depth).toString()
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
                    writer.indent(depth)
                }

                writer.write(quote(key))
                writer.write(":")
                if (shouldIndent) {
                    writer.write(" ")
                }
                writer.write(getJsonValue(value, shouldIndent, depth + 1))
                addComa = true
            }
            if (shouldIndent) {
                writer.write("\n")
                writer.indent(depth - 1)
            }
            writer.write("}")
            return writer
        } catch (exception: IOException) {
            throw JsonException(exception)
        }
    }

}

