package edu.csh.chase.sprint.json

object json {

    /**
     * Constructs a JsonArray from a list of elements
     * If any provided element is not a valid json type it will be skipped
     *
     * @param elements A list of Any? elements to put into a JsonArray
     * @return A JsonArray containing only valid elements from the provided list
     */
    fun get(vararg elements: Any?): JsonArray {
        return JsonArray(*elements)
    }

    /**
     * Constructs a JsonObject from a Lambda that returns an array of Pair<String, Any?>
     * Provided pairs with invalid json types will be ignored
     *
     * @param json A Function that returns an array of key, value pairs
     * @return A JsonObject with only valid pairs from the provided lambda
     */
    fun invoke(json: () -> Array<Pair<String, Any?>>): JsonObject {
        return JsonObject(*json())
    }

}