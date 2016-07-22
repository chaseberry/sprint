package edu.csh.chase.sprint.websockets

import edu.csh.chase.sprint.Request
import edu.csh.chase.sprint.RequestProcessor
import edu.csh.chase.sprint.RequestType
import edu.csh.chase.sprint.parameters.UrlParameters
import okhttp3.Headers
import okhttp3.OkHttpClient

class SprintWebSocket {

    fun connect(url: String,
                urlParameters: UrlParameters? = null,
                headers: Headers.Builder = Headers.Builder(),
                client: OkHttpClient = OkHttpClient(),
                extraData: Any? = null): RequestProcessor {

        val request = Request(
                url = url,
                requestType = RequestType.Get,
                urlParams = urlParameters,
                extraData = extraData,
                headers = headers)

        okhttp3.ws.WebSocketCall.create(OkHttpClient(), )

    }

}