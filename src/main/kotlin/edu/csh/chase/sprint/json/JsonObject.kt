package edu.csh.chase.sprint.json

import java.util.*

class JsonObject() {

    val map = HashMap<String, Any?>()

    constructor(tokener: JsonTokener) : this() {
        var c: Char
        var key: String

        if (tokener.nextClean() != '{') {
            throw tokener.syntaxError("A JSONObject text must begin with '{'")
        }
        while (true) {
            c = tokener.nextClean();
            when (c) {
                0.toChar() -> throw tokener.syntaxError("A JSONObject text must end with '}'")
                '}' -> return
                else -> {
                    tokener.back()
                    key = tokener.nextValue().toString()
                }

            }

            // The key is followed by ':'.
            c = tokener.nextClean();
            if (c != ':') {
                throw tokener.syntaxError("Expected a ':' after a key");
            }
            this.putOnce(key, tokener.nextValue());

            // Pairs are separated by ','.

            when (tokener.nextClean()) {
                ';', ',' -> {
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

    constructor(obj: JsonObject, vararg names: String) : this() {
        for (name in names) {
            putOnce(name, obj[name])
        }
    }

    fun putOnce(key: String, value: Any?): JsonObject {
        if (key in map) {
            return this
        }
        map[key] = value
        return this
    }

    fun set(key: String, value: Any?) {
        map[key] = value
    }

    fun get(key: String): Any? {
        return map[key]
    }

    fun get(key: String, default: Any): Any {
        if (key in map && map[key] != null) {
            return map[key]!!
        }
        return default
    }

    fun contains(key: String): Boolean {
        return key in map
    }

}