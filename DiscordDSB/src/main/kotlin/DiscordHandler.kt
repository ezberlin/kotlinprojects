package org.example

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.respondPublic
import dev.kord.core.entity.channel.TextChannel
import dev.kord.core.event.gateway.ReadyEvent
import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import dev.kord.rest.builder.interaction.string
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.toList

class DiscordHandler(private val token: String) {
    private lateinit var kord: Kord

    suspend fun bot() {
        kord = Kord(token)

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

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun getChannelIdByName(channelName: String): Snowflake? {
        val channels = kord.guilds.flatMapConcat { it.channels }
        val channel = channels.firstOrNull { it.name == channelName }
        return channel?.id
    }

    suspend fun sendMessage(channelName: String, message: String) {
        val channelId = getChannelIdByName(channelName)
        if (channelId != null) {
            val channel = kord.getChannelOf<TextChannel>(channelId)
            channel?.createMessage(message)
        } else {
            println("Channel not found: $channelName")
        }
    }

    suspend fun sendDmToRoleMembers(roleNames: String, message: String) {
        val roles = roleNames.split(",").map { it.trim().removeSurrounding("(", ")") }
        val guilds = kord.guilds.toList()
        for (guild in guilds) {
            for (roleName in roles) {
                val role = guild.roles.firstOrNull { it.name == roleName }
                if (role != null) {
                    val members = guild.members.filter { it.roleIds.contains(role.id) }.toList()
                    for (member in members) {
                        val dmChannel = member.getDmChannelOrNull()
                        dmChannel?.createMessage(message)
                    }
                }
            }
        }
    }
}