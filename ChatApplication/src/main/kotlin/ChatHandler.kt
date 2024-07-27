package org.example

import io.ktor.websocket.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.channels.consumeEach

val connections = mutableListOf<DefaultWebSocketSession>()

suspend fun handleWebSocket(session: DefaultWebSocketSession) {
    connections.add(session)
    try {
        session.incoming.consumeEach { frame ->
            if (frame is Frame.Text) {
                val text = frame.readText()
                broadcast(text)
            }
        }
    } catch (_: ClosedReceiveChannelException) {
    } finally {
        connections.remove(session)
    }
}


suspend fun broadcast(message: String) {
    for (connection in connections) {
        connection.send(Frame.Text(message))
    }
}
