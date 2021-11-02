package sprint

import edu.csh.chase.sprint.Sprint
import edu.csh.chase.sprint.websockets.WebSocket
import edu.csh.chase.sprint.websockets.WebSocketDisconnect
import edu.csh.chase.sprint.websockets.WebSocketEvent
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test
import java.util.*
import kotlin.concurrent.schedule

class WebsocketTest {

    lateinit var socket: WebSocket

    @Test fun testEchoWebsocket() {
        var msgNum = 0

        val timeoutTimer = Timer()

        socket = Sprint.createWebSocket(
            url = "ws://ws.ifelse.io",
            listener = {
                when (it) {
                    is WebSocketEvent.Connect -> {

                        assertEquals("Connection was not the first message received", 0, msgNum)
                        //msgNum=1 is `Request served by $id`
                    }
                    is WebSocketEvent.Disconnect -> {

                        assertEquals("Disconnection was not the third message received", 3, msgNum)
                        timeoutTimer.cancel()

                    }
                    is WebSocketEvent.Message -> {
                        if (it.message.startsWith("Request served")) {
                            socket.sendText("ping")
                        } else {
                            assertEquals("Message was not the second message received", 2, msgNum)
                            assertEquals("Message sent != message received", it.message, "ping")

                            socket.disconnect(WebSocketDisconnect.normalClosure, null)
                        }

                    }
                    is WebSocketEvent.Error -> {
                        it.exception.printStackTrace()
                        fail("Received an error!")
                    }
                }

                msgNum += 1

            }
        )

        socket.connect()

        timeoutTimer.schedule(2000) {
            socket.disconnect(WebSocketDisconnect.normalClosure, null)
            fail("WebSockets failed to do anything")
        }

        Thread.sleep(2350)//Guess I need to give time for the whole thing to run

    }


}
