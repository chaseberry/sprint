package edu.csh.chase.sprint.websockets

import edu.csh.chase.sprint.Response
import okio.Buffer
import okio.ByteString
import java.io.IOException

/**
 *
 * The underlying OkHttp WebSocket will automagically respond to Pings
 *
 */
interface WebSocketCallbacks {

    fun onConnect(response: Response)

    fun onDisconnect(disconnectCode: Int, reason: String?)

    fun onError(exception: IOException, response: Response?)

    fun messageReceived(message: String)

    fun messageReceived(message: ByteString)
}