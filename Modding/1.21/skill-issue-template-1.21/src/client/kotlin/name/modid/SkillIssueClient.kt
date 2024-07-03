package name.modid

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.item.Items

class SkillIssueClient : ClientModInitializer {
	override fun onInitializeClient() {
		// Register tick event listener
		ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick { client ->
			if (client.player != null) {
				val player = client.player!!
				// Check if the player does not have a totem in their offhand
				renderScreenOverlay = // Render screen overlay
					player.offHandStack.isEmpty || player.offHandStack.item != Items.TOTEM_OF_UNDYING
			}
		})

		// Register render callback
		HudRenderCallback.EVENT.register(HudRenderCallback { matrixStack, tickDelta ->
			if (renderScreenOverlay) {
				renderOverlay(matrixStack)
			}
		})
	}

	companion object {
		var renderScreenOverlay = false

		private fun renderOverlay(drawContext: DrawContext) {
			val client = MinecraftClient.getInstance()
			val window = client.window
			val width = window.scaledWidth
			val height = window.scaledHeight

			// Cover the entire screen with a semi-transparent red rectangle
			drawContext.fill(0, 0, width, height, 0x60FF0000)
		}
	}
}
