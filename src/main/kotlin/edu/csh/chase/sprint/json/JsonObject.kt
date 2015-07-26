package edu.csh.chase.sprint.json

import java.util.*

class JsonObject() {

    private val map = HashMap<String, Any?>()

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

    constructor(map: HashMap<String, Any?>) : this() {
        for ((key, value) in map) {
            putOnce(key, value)
        }
    }

    private fun addKeyToValue(key: String, value: Any?) {
        map[key] = value
    }

    fun putOnce(key: String, value: Any?): JsonObject {
        if (key in map) {
            return this
        }
        map[key] = value
        return this
    }

    //Setters

    fun set(key: String, value: Int?) {
        addKeyToValue(key, value)
    }

    fun set(key: String, value: Boolean?) {
        addKeyToValue(key, value)
    }

    fun set(key: String, value: String?) {
        addKeyToValue(key, value)
    }

    //Getters

    fun get(key: String): Any? {
        return map [key]
    }

    fun get(key: String, default: Any): Any {
        if (key in map && map[key] != null) {
            return map[key]!!
        }
        return default
    }

    fun getInt(key: String): Int? {
        return get(key) as? Int
    }

    fun getInt(key: String, default: Int): Int {
        return getInt(key) ?: return default
    }

    fun getBoolean(key: String): Boolean? {
        return get(key) as? Boolean
    }

    fun getBoolean(key: String, default: Boolean): Boolean {
        return getBoolean(key) ?: return default
    }

    fun getString(key: String): String? {
        return get(key) as? String
    }

    fun getString(key: String, default: String): String {
        return getString(key) ?: return default
    }

    fun contains(key: String): Boolean {
        return key in map
    }

}