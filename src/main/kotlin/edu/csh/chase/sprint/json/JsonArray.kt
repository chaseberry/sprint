package edu.csh.chase.sprint.json

import java.util.*

class JsonArray() {

    private val array = ArrayList<Any?>()

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

    private fun getValue(index: Int): Any? {
        if (index !in array.indices) {
            return null
        }
        return array[index]
    }

    private fun setValue(index: Int, value: Any?) {
        if (index !in array.indices) {
            return
        }
        array [index] = value

    }

    //Setters

    fun set(index: Int, value: Any?) {
        setValue(index, value)
    }

    //Putters

    fun put(value: Any?): JsonArray {
        //TODO check the type of value
        array.add(value)
        return this
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


}