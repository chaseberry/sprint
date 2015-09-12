package edu.csh.chase.sprint.parameters

import com.squareup.okhttp.MediaType
import com.squareup.okhttp.RequestBody
import edu.csh.chase.kjson.JsonArray
import edu.csh.chase.kjson.JsonBase
import edu.csh.chase.kjson.JsonObject
import okio.BufferedSink

class JsonBody(private val jsonValue: JsonBase) : RequestBody() {

    constructor(map: Map<String, Any?>) : this(JsonObject(map))

    constructor(list: List<Any?>) : this(JsonArray(list))

    override fun contentType(): MediaType? {
        return MediaType.parse("application/json; charset=utf-8")
    }

    override fun writeTo(sink: BufferedSink) {
        sink.writeUtf8(jsonValue.toString())
    }


}