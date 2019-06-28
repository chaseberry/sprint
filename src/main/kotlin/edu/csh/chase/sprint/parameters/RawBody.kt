package edu.csh.chase.sprint.parameters

import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okio.BufferedSink

@Deprecated(
    message = "Use RequestBody.create()",
    replaceWith = ReplaceWith(
        expression = "RequestBody.create(MediaType.parse(contentType), rawBody)",
        imports = ["okhttp3.MediaType", "okhttp3.RequestBody"]
    )
)
class RawBody(val rawBody: Any, val contentType: String) : RequestBody() {

    override fun writeTo(sink: BufferedSink) {
        sink.writeUtf8(rawBody.toString())
    }

    override fun contentType(): MediaType {
        return contentType.toMediaTypeOrNull() ?: throw IllegalArgumentException("$contentType is not a valid content type")
    }

}