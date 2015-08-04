package edu.csh.chase.sprint.parameters

import com.squareup.okhttp.MediaType
import com.squareup.okhttp.RequestBody
import okio.BufferedSink

class RawBody(val rawBody: Any, val contentType: String) : RequestBody() {

    override fun writeTo(sink: BufferedSink) {
        sink.writeUtf8(rawBody.toString())
    }

    override fun contentType(): MediaType {
        return MediaType.parse(contentType)
    }

}