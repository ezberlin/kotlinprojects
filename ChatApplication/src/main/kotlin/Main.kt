package org.example

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.routing.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.websocket.*
import java.time.Duration
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.serialization.kotlinx.json.*
import io.ktor.websocket.*



fun main() {
    embeddedServer(Netty, port = 8080) {
        install(WebSockets) {
            pingPeriod = Duration.ofMinutes(1)
            timeout = Duration.ofSeconds(15)
            maxFrameSize = Long.MAX_VALUE
            masking = false
        }
        install(ContentNegotiation) {
            json() // Configure JSON serialization
        }
        routing {
            webSocket("/chat") {
                handleWebSocket(this)
            }
        }
    }.start(wait = true)
}



