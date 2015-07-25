package edu.csh.chase.sprint.parameters

class RawBody(val rawBody: Any) : Body {
    override fun serialize(): String {
        return rawBody.toString()
    }

}