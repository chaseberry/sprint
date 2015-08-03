package edu.csh.chase.sprint.json

import kotlin.properties.Delegates
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty

public object JsonDelegates {

    public fun<T> objectVal(jsonObject: JsonObject): JsonObjectVal<T> = JsonObjectVal(jsonObject)

    private open class JsonObjectVal<T>(protected val jsonObject: JsonObject) : ReadOnlyProperty<T, Any?> {

        override fun get(thisRef: T, desc: PropertyMetadata): Any? {
            return jsonObject[desc.name]
        }

    }

    public fun <T>objectVar(jsonObject: JsonObject): JsonObjectVar<T> = JsonObjectVar(jsonObject)

    private class JsonObjectVar<T>(jsonObject: JsonObject) : JsonObjectVal<T>(jsonObject), ReadWriteProperty<T, Any?> {

        override fun set(thisRef: T, desc: PropertyMetadata, value: Any?) {
            jsonObject[desc.name] = value
        }

    }

}