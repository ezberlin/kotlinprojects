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

class DiscordHandler (private val token: String, private val guildId: Snowflake){
    suspend fun bot() {
        val kord = Kord(token)

        kord.on<ReadyEvent> {
            kord.createGlobalChatInputCommand(
                "plan",
                "Returns plan for the class"
            ) {
                string("class", "Your class") {
                    required = true
                }
            }
        }

        kord.on<ChatInputCommandInteractionCreateEvent> {
            val command = interaction.command
            val output: String

            if (command.rootName == "plan") {
                val classreq = command.options["class"]?.value?.toString()
                output = if (classreq != null && validClasses.contains(classreq)) {
                    getClassEntries(classreq)
                } else {
                    "Leider scheint diese Klasse nicht zu existieren!"
                }

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