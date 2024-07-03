package name.modid

import name.modid.IssueRenderer.Companion.highestPriorityIssueIndex
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.item.Items

class SkillIssueClient : ClientModInitializer {
	override fun onInitializeClient() {
		val noTotemEquipped = Issue(
			{ p: ClientPlayerEntity -> p.offHandStack.isEmpty || p.offHandStack.item != Items.TOTEM_OF_UNDYING },
			0x60E8B50D,
			15
		)


		val existingIssues = arrayOf(noTotemEquipped)
		val presentIssues = mutableListOf<Issue>()

		ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick { client ->
			client.player?.let { player ->
				presentIssues.clear()
				existingIssues.filter { it.issue(player) }
					.also { filteredIssues -> presentIssues.addAll(filteredIssues) }

				highestPriorityIssueIndex = presentIssues.maxByOrNull { it.priority }?.let { issue ->
					existingIssues.indexOf(issue)
				}
			}
		})

		HudRenderCallback.EVENT.register(HudRenderCallback { matrixStack, _ ->
			if (highestPriorityIssueIndex != null && presentIssues.isNotEmpty()) {
				IssueRenderer.renderOverlay(matrixStack, presentIssues[highestPriorityIssueIndex!!])
			}
		})
	}
}
