package edu.csh.chase.sprint.Parameter

abstract class Parameter(private val pair: Pair<String, Any?>, val type: ParameterType) {

    val key = pair.first

    val value = pair.second

    override fun toString(): String {
        return "$type($key:$value)"
    }

}