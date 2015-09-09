package edu.csh.chase.sprint.json

abstract class JsonBase : JsonSerializable {

    abstract val size: Int
    var delim = ":"

    fun traverse(compoundKey: String): Any? {
        val key = compoundKey.splitBy(delim).iterator()
        return when (this) {
            is JsonArray -> traverseArray(key, this)
            is JsonObject -> traverseObject(key, this)
            else -> null
        }
    }

    fun traverse(compoundKey: String, default: Any): Any {
        return traverse(compoundKey = compoundKey) ?: default
    }

    fun traverseMulti(vararg keys: String): Any? {
        for (key in keys) {
            return traverse(compoundKey = key) ?: continue
        }
        return null
    }

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