package edu.csh.chase.sprint

fun <A : Any, B : Any?>A.to(other: B): Pair<A, B> {
    return Pair(this, other)
}

data class Pair<A, B>(var first: A, var second: B)