package edu.csh.chase.sprint.parameters

data class Header constructor(val name: String, val value: String) {
    constructor(header: Pair<String, String>) : this(header.first, header.second)
}