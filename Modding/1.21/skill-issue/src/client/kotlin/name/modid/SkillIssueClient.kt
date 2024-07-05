package name.modid

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.component.DataComponentTypes
import net.minecraft.item.Items
import org.lwjgl.glfw.GLFW

class SkillIssueClient : ClientModInitializer {

	private lateinit var toggleIssuesKeyBinding: KeyBinding

	override fun onInitializeClient() {
		val noTotemEquipped = Issue(
			"No Totem Equipped",
			{ p: ClientPlayerEntity -> p.offHandStack.isEmpty || p.offHandStack.item != Items.TOTEM_OF_UNDYING },
			0x60E8B50D,
			15,
			false
		)

		val lowHealth = Issue(
			"Low Health",
			{ p: ClientPlayerEntity -> p.health <= 8 },
			0x60820C01,
			20,
			true
		)

		val lowHunger = Issue(
			"Low Hunger",
			{ p: ClientPlayerEntity -> p.hungerManager.foodLevel <= 8 },
			0x60492100,
			10,
			true
		)

		val noFood = Issue(
			"No food in hotbar",
			{ p: ClientPlayerEntity ->
				var nofood = true
				for (i in 0..8) if (p.inventory.getStack(i).get(DataComponentTypes.FOOD) != null) nofood = false
				nofood},
			0x60492100,
			12,
			true
		)

		val existingIssues = arrayOf(noTotemEquipped, lowHealth, lowHunger, noFood)
		val presentIssues = mutableListOf<Issue>()

		ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick { client ->
			client.player?.let { player ->
				presentIssues.clear()
				existingIssues.filter { it.issue(player) && it.enabled }
					.also { filteredIssues -> presentIssues.addAll(filteredIssues) }
				IssueRenderer.highestPriorityIssue = presentIssues.maxByOrNull { it.priority }
			}

			if (toggleIssuesKeyBinding.wasPressed()) {
				client.setScreen(IssueToggleScreen(existingIssues))
			}

		})

		toggleIssuesKeyBinding = KeyBinding(
			"key.modid.toggleIssues",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_H,
			"category.modid.issues"
		)
		KeyBindingHelper.registerKeyBinding(toggleIssuesKeyBinding)


		HudRenderCallback.EVENT.register(HudRenderCallback { matrixStack, _ ->
			IssueRenderer.highestPriorityIssue?.let { issue ->
				IssueRenderer.renderOverlay(matrixStack, issue)
			}
		})
	}
}