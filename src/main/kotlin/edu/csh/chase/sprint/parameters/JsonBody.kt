package edu.csh.chase.sprint.parameters

import edu.csh.chase.kjson.JsonArray
import edu.csh.chase.kjson.JsonBase
import edu.csh.chase.kjson.JsonObject
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.BufferedSink

class JsonBody(json: JsonBase) : RequestBody() {

    constructor(map: Map<String, Any?>) : this(JsonObject(map))

    constructor(list: List<Any?>) : this(JsonArray(list))

    private val jsonValue = json.toString().toByteArray()

    override fun contentLength(): Long {
        return jsonValue.size.toLong()
    }

    override fun contentType(): MediaType? {
        return MediaType.parse("application/json; charset=utf-8")
    }

    override fun writeTo(sink: BufferedSink) {
        sink.write(jsonValue)
    }

}