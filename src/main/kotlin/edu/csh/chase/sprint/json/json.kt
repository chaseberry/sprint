package edu.csh.chase.sprint.json

object json {

    fun get(vararg elements: Any?): JsonArray {
        return JsonArray(*elements)
    }

    fun invoke(json: () -> Array<Pair<String, Any?>>): JsonObject {
        return JsonObject(*json())
    }

}
