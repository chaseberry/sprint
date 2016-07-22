package edu.csh.chase.sprint.websockets

/**
 *
 * The underlying OkHttp Websocket will automagically respond to Pings
 *
 */
interface WebSocketCallbacks {

    fun onConnect()

    fun onDisconnect()

    fun onError()

    fun onPong()

}