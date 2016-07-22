package edu.csh.chase.sprint.websockets

import edu.csh.chase.sprint.Request
import edu.csh.chase.sprint.RequestProcessor
import edu.csh.chase.sprint.RequestType
import edu.csh.chase.sprint.parameters.UrlParameters
import okhttp3.Headers
import okhttp3.OkHttpClient

class SprintWebSocket {

    fun create(url: String,
               urlParameters: UrlParameters? = null,
               headers: Headers.Builder = Headers.Builder(),
               client: OkHttpClient = OkHttpClient(),
               retryCount: Int = 4,
               extraData: Any? = null,
               listener: (WebSocketEvent, Any?) -> Unit) {
    }

    fun create(url: String,
               urlParameters: UrlParameters? = null,
               headers: Headers.Builder = Headers.Builder(),
               client: OkHttpClient = OkHttpClient(),
               retryCount: Int = 4,
               extraData: Any? = null,
               onConnect: () -> Unit,
               onDisconnect: () -> Unit,
               onError: () -> Unit,
               onMessage: () -> Unit,
               onPong: (() -> Unit)?) {

    }

    fun create(url: String,
               urlParameters: UrlParameters? = null,
               headers: Headers.Builder = Headers.Builder(),
               client: OkHttpClient = OkHttpClient(),
               retryCount: Int = 4,
               extraData: Any? = null,
               callbacks: WebSocketCallbacks) {

    }

}