package edu.csh.chase.sprint.json

abstract class JsonBase : JsonSerializable {

    abstract val size: Int

    fun traverse(compoundKey: String, delim: String = ":"): Any? {
        val key = compoundKey.splitBy(delim).iterator()

    }

    fun traverse(compoundKey: String, default: Any, delim: String = ":"): Any {
        return traverse(compoundKey = compoundKey, delim = delim) ?: default
    }

    private fun traverseArray(key: Iterator<String>, array: JsonArray): Any? {
        if (!key.hasNext()) {
            return null
        }


    }

    private fun traverseObject(key: Iterator<String>, `object`: JsonObject): Any? {
        if (!key.hasNext()) {
            return null
        }
        val value = `object`[key.next()]
        return value
    }

}