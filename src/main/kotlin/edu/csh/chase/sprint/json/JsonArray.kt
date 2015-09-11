package edu.csh.chase.sprint.json

import java.io.IOException
import java.io.StringWriter
import java.io.Writer
import java.util.ArrayList

class JsonArray() : JsonBase(), Iterable<Any?> {

    private val array = ArrayList<Any?>()

    override val size: Int
        get() {
            return array.size()
        }

    val indices: IntRange
        get() {
            return array.indices
        }

    constructor(tokener: JsonTokener) : this() {
        if (tokener.nextClean() != '[') {
            throw tokener.syntaxError("A JSONArray text must start with '['")
        }
        if (tokener.nextClean() != ']') {
            tokener.back()
            while (true) {
                if (tokener.nextClean() == ',') {
                    tokener.back()
                    array.add(null)
                } else {
                    tokener.back()
                    array.add(tokener.nextValue())
                }
                when (tokener.nextClean()) {
                    ',' -> {
                        if (tokener.nextClean() == ']') {
                            return
                        }
                        tokener.back()
                    }
                    ']' -> return
                    else -> throw tokener.syntaxError("Expected a ',' or ']'")
                }
            }
        }
    }

    constructor(jsonString: String) : this(JsonTokener(jsonString))

    constructor(list: Collection<Any?>) : this() {
        array.addAll(list.filter { it.isValidJsonType() })
    }

    constructor(vararg array: Any?) : this() {
        this.array.addAll(array.filter { it.isValidJsonType() })
    }

    private fun getValue(index: Int): Any? {
        if (index !in array.indices) {
            return null
        }
        return array[index]
    }

    private fun setValue(index: Int, value: Any?) {
        if (index !in array.indices) {
            return//Throws exception?
        }
        if (!value.isValidJsonType()) {
            throw JsonException("$value is not a valid JsonException")
        }
        array [index] = value
    }

    //Setters

    fun set(index: Int, value: Any?) {
        setValue(index, value)
    }

    //Putters

    fun put(value: Any?): JsonArray {
        if (!value.isValidJsonType()) {
            throw JsonException("$value is not a valid JsonException")
        }
        array.add(value)
        return this
    }

    fun putNotNull(value: Any?): JsonArray {
        if (value == null) {
            return this
        }
        return put(value)
    }

    //Getters

    fun get(index: Int): Any? {
        return getValue(index)
    }

    fun get(index: Int, default: Any): Any {
        return getValue(index) ?: return default
    }

    fun getBoolean(index: Int): Boolean? {
        return getValue(index) as? Boolean
    }

    fun getBoolean(index: Int, default: Boolean): Boolean {
        return getBoolean(index) ?: return default
    }

    fun getInt(index: Int): Int? {
        return getValue(index) as? Int
    }

    fun getInt(index: Int, default: Int): Int {
        return getInt(index) ?: return default
    }

    fun getDouble(index: Int): Double? {
        return getValue(index) as? Double
    }

    fun getDouble(index: Int, default: Double): Double {
        return getDouble(index) ?: return default
    }

    fun getString(index: Int): String? {
        return getValue(index) as? String
    }

    fun getString(index: Int, default: String): String {
        return getString(index) ?: return default
    }

    fun getJsonObject(index: Int): JsonObject? {
        return getValue(index) as? JsonObject
    }

    fun getJsonObject(index: Int, default: JsonObject): JsonObject {
        return getJsonObject(index) ?: return default
    }

    fun getJsonArray(index: Int): JsonArray? {
        return getValue(index) as? JsonArray
    }

    fun getJsonArray(index: Int, default: JsonArray): JsonArray {
        return getJsonArray(index) ?: return default
    }

    //Other Functions

    fun contains(index: Int): Boolean {
        return index in array.indices
    }

    fun forEach(operation: (Any?) -> Unit) {
        array.forEach(operation)
    }

    override fun jsonSerialize(): String {
        return this.toString()
    }

    override fun iterator(): Iterator<Any?> {
        return array.iterator()
    }

    override fun equals(other: Any?): Boolean {
        return other is JsonArray && array == other.array
    }

    /**
     * Make a JSON text of this JSONArray. For compactness, no unnecessary
     * whitespace is added. If it is not possible to produce a syntactically
     * correct JSON text then null will be returned instead. This could occur if
     * the array contains an invalid number.
     * <p>
     * Warning: This method assumes that the data structure is acyclical.
     *
     * @return a printable, displayable, transmittable representation of the
     *         array.
     */
    override fun toString(): String {
        return toString(false)
    }

    /**
     * Make a prettyprinted JSON text of this JSONArray. Warning: This method
     * assumes that the data structure is acyclical.
     *
     * @param indentFactor
     *            The number of spaces to add to each level of indentation.
     * @return a printable, displayable, transmittable representation of the
     *         object, beginning with <code>[</code>&nbsp<small>(left
     *         bracket)</small> and ending with <code>]</code>
     *         &nbsp<small>(right bracket)</small>.
     * @throws JSONException
     */
    fun toString(shouldIndent: Boolean): String {
        val sw = StringWriter()
        synchronized (sw.getBuffer()) {
            return this.write(sw, shouldIndent).toString()
        }
    }

    /**
     * Write the contents of the JSONArray as JSON text to a writer. For
     * compactness, no whitespace is added.
     * <p>
     * Warning: This method assumes that the data structure is acyclical.
     *
     * @return The writer.
     * @throws JSONException
     */
    fun write(writer: Writer): Writer {
        return this.write(writer, false)
    }


    /**
     * Write the contents of the JSONArray as JSON text to a writer. For
     * compactness, no whitespace is added.
     * <p>
     * Warning: This method assumes that the data structure is acyclical.
     *
     * @param indentFactor
     *            The number of spaces to add to each level of indentation.
     * @param indent
     *            The indention of the top level.
     * @return The writer.
     * @throws JSONException
     */
    fun write(writer: Writer, shouldIndent: Boolean, depth: Int = 1): Writer {
        try {
            var addComa = false

            writer.write("[")

            for (value in array) {

                if (addComa) {
                    writer.write(",")
                }
                if (shouldIndent) {
                    writer.write("\n")
                    writer.indent(depth)
                }
                writer.write(getJsonValue(value))
                addComa = true
            }
            writer.write("]")
            return writer
        } catch (e: IOException) {
            throw JsonException(e)
        }
    }

}