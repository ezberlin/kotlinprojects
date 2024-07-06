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
	private val existingIssues = initializeIssues()

	private fun initializeIssues() = arrayOf(
		Issue("No totem equipped",
			{ p: ClientPlayerEntity -> p.offHandStack.isEmpty || p.offHandStack.item != Items.TOTEM_OF_UNDYING },
			0x60E8B50D,
			15,
			false),

		Issue("Low health",
			{ p: ClientPlayerEntity -> p.health <= 8 },
			0x60820C01,
			20,
			true),

		Issue("No food in hotbar",
			{ p: ClientPlayerEntity ->
				var nofood = true
				for (i in 0..8) if (p.inventory.getStack(i).get(DataComponentTypes.FOOD) != null) nofood = false
				nofood},
			0x60492100,
			12,
			false),

		Issue("Low hunger",
			{ p: ClientPlayerEntity -> p.hungerManager.foodLevel <= 8 },
			0x60492100,
			10,
			true))

	private fun setupHudRenderCallback() {
		HudRenderCallback.EVENT.register(HudRenderCallback { matrixStack, _ ->
			IssueRenderer.highestPriorityIssue?.let { issue ->
				IssueRenderer.renderOverlay(matrixStack, issue)
			}
		})
	}

	private fun setupKeyBinding() {
		toggleIssuesKeyBinding = KeyBinding(
			"key.modid.toggleIssues",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_H,
			"category.modid.issues"
		).also { KeyBindingHelper.registerKeyBinding(it) }
	}

	private fun setupClientTickEvent() {
		val presentIssues = mutableListOf<Issue>()
		ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick { client ->
			client.player?.let { player ->
				presentIssues.clear()
				existingIssues.filterTo(presentIssues) { it.issue(player) && it.enabled }
				IssueRenderer.highestPriorityIssue = presentIssues.maxByOrNull { it.priority }
			}
			if (toggleIssuesKeyBinding.wasPressed()) {
				client.setScreen(IssueToggleScreen(existingIssues))
			}
		})
	}

	override fun onInitializeClient() {
		ConfigManager.loadConfig(existingIssues)
		setupClientTickEvent()
		setupKeyBinding()
		setupHudRenderCallback()
	}
}