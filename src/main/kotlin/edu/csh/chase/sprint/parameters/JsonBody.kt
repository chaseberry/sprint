package edu.csh.chase.sprint.parameters

import edu.csh.chase.sprint.json.JsonBase

class JsonBody(private val jsonValue: JsonBase) : Body {

    override fun serialize(): String {
        return jsonValue.toString()
    }

}