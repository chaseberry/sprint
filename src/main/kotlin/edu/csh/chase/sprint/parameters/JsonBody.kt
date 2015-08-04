package edu.csh.chase.sprint.parameters

import com.squareup.okhttp.MediaType
import com.squareup.okhttp.RequestBody
import edu.csh.chase.sprint.json.JsonBase
import okio.BufferedSink

class JsonBody(private val jsonValue: JsonBase) : RequestBody() {

    override fun contentType(): MediaType? {
        return MediaType.parse("application/json; charset=utf-8")
    }

    override fun writeTo(sink: BufferedSink?) {
        sink?.writeUtf8(jsonValue.toString())
    }


}