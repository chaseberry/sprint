package edu.csh.chase.sprint.websockets

import edu.csh.chase.sprint.Response
import okio.Buffer
import java.io.IOException

abstract class WebSocketEvent(val type: Type) {

    enum class Type {
        Connect,
        Disconnect,
        Error,
        Pong,
        Message
    }

}

class ConnectEvent(val response: Response) : WebSocketEvent(Type.Connect)

class DisconnectEvent(val code: Int, val reason: String?) : WebSocketEvent(Type.Disconnect)

class ErrorEvent(val exception: IOException, response: Response?) : WebSocketEvent(Type.Error)

class PongEvent(val payload: Buffer?) : WebSocketEvent(Type.Pong)

class MessageEvent(val response: Response) : WebSocketEvent(Type.Message)