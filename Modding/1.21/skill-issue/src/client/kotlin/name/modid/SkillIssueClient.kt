package name.modid

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.component.DataComponentTypes
import net.minecraft.item.Items

class SkillIssueClient : ClientModInitializer {
	override fun onInitializeClient() {
		val noTotemEquipped = Issue(
			{ p: ClientPlayerEntity -> p.offHandStack.isEmpty || p.offHandStack.item != Items.TOTEM_OF_UNDYING },
			0x60E8B50D,
			15
		)

		val lowHealth = Issue(
			{ p: ClientPlayerEntity -> p.health <= 8 },

			0x60820C01,
			20
		)

		val lowFood = Issue(
			{ p: ClientPlayerEntity -> p.hungerManager.foodLevel <= 8 },
			0x60492100,
			10
		)

		val noFood = Issue(
			{ p: ClientPlayerEntity ->
				var nofood = true
				for (i in 0..8) if (p.inventory.getStack(i).get(DataComponentTypes.FOOD) != null) nofood = false
				nofood},

			0x60492100,
			12
		)

		val existingIssues = arrayOf(noTotemEquipped, lowHealth, lowFood, noFood)
		val presentIssues = mutableListOf<Issue>()

		ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick { client ->
			client.player?.let { player ->
				presentIssues.clear()
				existingIssues.filter { it.issue(player) }
					.also { filteredIssues -> presentIssues.addAll(filteredIssues) }

				IssueRenderer.highestPriorityIssue = presentIssues.maxByOrNull { it.priority }
			}
		})

		HudRenderCallback.EVENT.register(HudRenderCallback { matrixStack, _ ->
			IssueRenderer.highestPriorityIssue?.let { issue ->
				IssueRenderer.renderOverlay(matrixStack, issue)
			}
		})
	}
}