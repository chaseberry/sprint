package edu.csh.chase.sprint.json

abstract class JsonBase : JsonSerializable {

    /**
     * A value that equals how large this JsonBase is
     */
    abstract val size: Int

    var delim = ":"

    abstract fun toString(shouldIndent: Boolean, depth: Int = 1): String

    /**
     * Attempts to walk down a chain of Objects and Arrays based on a compoundKey
     * Compound keys will be split by the delim(base of ":")
     * If it encounters a dead route it will return what it last found, including the array or object it's checking
     * An int will be parses as an array index, all else will be object keys
     *
     * @param compoundKey A compound key in the form of key:key2:key3 to dig down into
     * @return The value that was found by the provided key, or null if nothing was found
     */
    fun traverse(compoundKey: String): Any? {
        val key = compoundKey.splitBy(delim).iterator()
        return when (this) {
            is JsonArray -> traverseArray(key, this)
            is JsonObject -> traverseObject(key, this)
            else -> null
        }
    }

    /**
     * Attempts to walk down a chain of Objects and Arrays based on a compoundKey
     * Compound keys will be split by the delim(base of ":")
     * If it encounters a dead route it will return what it last found, including the array or object it's checking
     * An int will be parses as an array index, all else will be object keys
     *
     * @param compoundKey A compound key in the form of key:key2:key3 to dig down into
     * @param default A default value to return if null was encountered
     * @return The value that was found by the provided key, or null if nothing was found
     */
    fun traverse(compoundKey: String, default: Any): Any {
        return traverse(compoundKey = compoundKey) ?: default
    }

    /**
     * Traverses this JsonBase with multiple keys
     *
     * @param keys A list of keys to check
     * @return The first found value or null if no key matched
     */
    fun traverseMulti(vararg keys: String): Any? {
        for (key in keys) {
            return traverse(compoundKey = key) ?: continue
        }
        return null
    }

    /**
     * Traverses this JsonBase with a default value if a null was found
     *
     * @param default A default if null was found
     * @param keys A list of keys to check
     * @return The first found value or default if none worked
     */
    fun traverseMultiWithDefault(default: Any, vararg keys: String): Any {
        return traverseMulti(keys = *keys) ?: default
    }

    private fun traverseArray(key: Iterator<String>, array: JsonArray): Any? {
        if (!key.hasNext()) {
            return array
        }

        val index = try {
            key.next().toInt()
        } catch(e: NumberFormatException) {
            return null
        }
        val value = array[index]
        return when (value) {
            is JsonArray -> traverseArray(key, value)
            is JsonObject -> traverseObject(key, value)
            else -> value
        }

    }

    private fun traverseObject(key: Iterator<String>, `object`: JsonObject): Any? {
        if (!key.hasNext()) {
            return `object`
        }
        val value = `object`[key.next()]
        return when (value) {
            is JsonObject -> traverseObject(key, value)
            is JsonArray -> traverseArray(key, value)
            else -> value
        }
    }

}