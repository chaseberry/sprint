package edu.csh.chase.sprint.json

import kotlin.properties.Delegates
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty

public object JsonDelegates {

    public fun<T> objectVal(jsonObject: JsonObject): JsonObjectVal<T> = JsonObjectVal(jsonObject)

    open class JsonObjectVal<T : Any?>(protected val jsonObject: JsonObject) : ReadOnlyProperty<Any, T> {

        override fun get(thisRef: Any, desc: PropertyMetadata): T {
            return jsonObject[desc.name] as T
        }

    }

    public fun <T>objectVar(jsonObject: JsonObject): JsonObjectVar<T> = JsonObjectVar(jsonObject)

    class JsonObjectVar<T : Any?>(jsonObject: JsonObject) : JsonObjectVal<T>(jsonObject), ReadWriteProperty<Any, T> {

        override fun set(thisRef: Any, desc: PropertyMetadata, value: T) {
            jsonObject[desc.name] = value
        }

    }

    public fun <T>notNullObjectVal(jsonObject: JsonObject, defaultValue: (String) -> Any = { key -> key }): JsonObjectValNotNull<T>
            = JsonObjectValNotNull(jsonObject, defaultValue)

    private open class JsonObjectValNotNull<T : Any>(protected val jsonObject: JsonObject, val defaultValue: (String) -> Any) :
            ReadOnlyProperty<Any, T> {
        override fun get(thisRef: Any, desc: PropertyMetadata): T {
            return jsonObject[desc.name, defaultValue(desc.name)] as T
        }

    }

    private class JsonObjectVarNotNull<T : Any>(jsonObject: JsonObject, defaultValue: (String) -> Any) :
            JsonObjectValNotNull<T>(jsonObject, defaultValue), ReadWriteProperty<Any, T> {
        override fun set(thisRef: Any, desc: PropertyMetadata, value: T) {
            jsonObject[desc.name] = value
        }

    }

}