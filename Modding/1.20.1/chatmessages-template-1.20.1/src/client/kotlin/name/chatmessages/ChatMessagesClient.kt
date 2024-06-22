package name.chatmessages.ChatMessages

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.client.util.ChatMessages
import net.minecraft.text.Text

class ExampleModClient : ClientModInitializer {
	override fun onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(ChatMessages.DIRT_BROKEN) { client, _, buf, _ ->
			val totalDirtBlocksBroken = buf.readInt()
			client.execute {
				client.player?.sendMessage(Text.literal("Total dirt blocks broken: $totalDirtBlocksBroken"))
			}
		}
	}
}
