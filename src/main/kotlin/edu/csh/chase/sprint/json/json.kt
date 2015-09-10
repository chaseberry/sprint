package edu.csh.chase.sprint.json

object json {

    fun get(elements: List<Any?>): JsonArray {
        return JsonArray(elements)
    }

}

fun json(json: () -> List<Pair<String, Any?>>): JsonObject {
    return JsonObject(json())
}