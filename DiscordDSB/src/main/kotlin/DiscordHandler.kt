package org.example

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.respondPublic
import dev.kord.core.event.gateway.ReadyEvent
import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import dev.kord.rest.builder.interaction.string

class DiscordHandler (private val token: String){
    suspend fun bot() {
        val kord = Kord(token)

        kord.on<ReadyEvent> {
            kord.createGlobalChatInputCommand(
                "vert",
                "Vertretungen für deine Klasse!"
            ) {
                string("klasse", "Deine Klasse") {
                    required = true
                }
            }

            kord.createGlobalChatInputCommand(
                "plan",
                "Ein Link zu einem vollen Vertretungsplan!"
            )
        }

        kord.on<ChatInputCommandInteractionCreateEvent> {
            val command = interaction.command
            var output: String

            if (command.rootName == "vert") {
                val classreq = command.options["klasse"]?.value?.toString()
                output = if (classreq != null && validClasses.contains(classreq)) {
                    getClassEntries(classreq)
                } else {
                    "Leider scheint diese Klasse nicht zu existieren!"
                }

                interaction.respondPublic {
                    content = output
                }
            }

            if (command.rootName == "plan") {
                output = getFullUrl() ?: "Der Vertretungsplan ist zurzeit leider nicht verfügbar!"

                interaction.respondPublic {
                    content = output
                }
            }
        }

        kord.on<MessageCreateEvent> {
            if (message.content != "!bing") return@on

            message.channel.createMessage("bing chilling!")
        }

        kord.login {
            @OptIn(PrivilegedIntent::class)
            intents += Intent.MessageContent
        }
    }
}