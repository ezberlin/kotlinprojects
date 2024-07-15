package com.example.chatlogger

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.client.MinecraftClient
import net.minecraft.network.packet.CustomPayload
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket
import net.minecraft.text.Text
import java.io.BufferedWriter
import java.io.FileWriter
import java.io.IOException
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ChatLoggerMod : ClientModInitializer {
    private var lastReceivedMessage: Text? = null

    companion object {
        private val LOG_FILE_PATH = Paths.get("chatlog.txt")

        @JvmStatic
        fun logChatMessage(message: Text) {
            try {
                BufferedWriter(FileWriter(LOG_FILE_PATH.toFile(), true)).use { writer ->
                    val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                    writer.write("[$timestamp] ${message.string}\n")
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    override fun onInitializeClient() {
        // Register an event listener for client ticks
        ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick { client ->
            val chatHud = client.inGameHud.chatHud
            // Check the latest chat message and log it
            val message = lastReceivedMessage ?: return@EndTick
            logChatMessage(message)
        })

        // Register a global receiver for the chat message packet
        ClientPlayNetworking.registerGlobalReceiver<CustomPayload>(GameMessageS2CPacket) { packet, context ->
            // Use reflection to access the getMessage method
            val getMessageMethod = GameMessageS2CPacket::class.java.getDeclaredMethod("getMessage")
            getMessageMethod.isAccessible = true
            // Store the message from the packet
            lastReceivedMessage = getMessageMethod.invoke(packet) as Text
        }
    }
}
