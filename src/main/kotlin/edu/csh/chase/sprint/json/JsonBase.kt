package edu.csh.chase.sprint.json

abstract class JsonBase : JsonSerializable {

    abstract val size: Int

    fun traverse(compoundKey: String, delim: String = ":"): Any? {
        val key = compoundKey.splitBy(delim).iterator()
        return when (this) {
            is JsonArray -> traverseArray(key, this)
            is JsonObject -> traverseObject(key, this)
            else -> null
        }
    }

    fun traverse(compoundKey: String, default: Any, delim: String = ":"): Any {
        return traverse(compoundKey = compoundKey, delim = delim) ?: default
    }

    fun traverseMulti(delim: String = ":", vararg keys: String): Any? {
        keys.forEach { return traverse(compoundKey = it, delim = delim) ?: return@forEach }
        return null
    }

    fun traverseMulti(default: Any, delim: String = ":", vararg keys: String): Any {
        return traverseMulti(keys = *keys, delim = delim) ?: default
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