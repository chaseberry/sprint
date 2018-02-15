package edu.csh.chase.sprint.websockets

import edu.csh.chase.sprint.Response
import okio.Buffer
import java.io.IOException

sealed class WebSocketEvent {

    class Connect(val response: Response) : WebSocketEvent()

    class Disconnect(val code: Int, val reason: String?) : WebSocketEvent()

    class Error(val exception: IOException, response: Response?) : WebSocketEvent()

    class Pong(val payload: Buffer?) : WebSocketEvent()

    class Message(val message: String) : WebSocketEvent()

}

