package name.chatmessagespackage.ChatMessages

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.block.Blocks
import net.minecraft.util.Identifier

class ExampleMod : ModInitializer {
	companion object {
		const val MOD_ID = "chatmessages"
		val DIRT_BROKEN = Identifier(MOD_ID, "dirt_broken")
	}

	private var totalDirtBlocksBroken = 0

	override fun onInitialize() {
		PlayerBlockBreakEvents.AFTER.register { world, player, pos, state, entity ->
			if (state.block == Blocks.GRASS_BLOCK || state.block == Blocks.DIRT) {
				// Increment the amount of dirt blocks that have been broken
				totalDirtBlocksBroken++

				// Send a packet to the client
				val server = world.server
				val data = PacketByteBufs.create()
				data.writeInt(totalDirtBlocksBroken)

				val playerEntity = server?.playerManager?.getPlayer(player.uuid)
				server?.execute {
					ServerPlayNetworking.send(playerEntity, DIRT_BROKEN, data)
				}
			}
		}
	}
}