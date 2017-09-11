package edu.csh.chase.sprint.parameters

import okhttp3.MediaType
import okhttp3.RequestBody
import okio.BufferedSink

class RawBody(val rawBody: Any, val contentType: String) : RequestBody() {

    override fun writeTo(sink: BufferedSink) {
        sink.writeUtf8(rawBody.toString())
    }

    override fun contentType(): MediaType {
        return MediaType.parse(contentType) ?: throw IllegalArgumentException("$contentType is not a valid content type")
    }

}