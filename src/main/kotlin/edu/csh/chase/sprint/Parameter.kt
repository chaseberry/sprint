package edu.csh.chase.sprint

data class Parameter(private val pair: Pair<String, Any?>, val type: ParameterType) {

    val key: String
        get() {
            return pair.first
        }

    var value: Any?
        get() {
            return pair.second
        }
        set(value: Any?) {
            pair.second = value
        }

}