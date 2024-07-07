package name.modid

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.DisconnectedScreen
import net.minecraft.client.gui.screen.TitleScreen
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.component.DataComponentTypes
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.text.Text
import org.lwjgl.glfw.GLFW

class SkillIssueClient : ClientModInitializer {

	private lateinit var toggleIssuesKeyBinding: KeyBinding
	private val existingIssues = initializeIssues()

	private fun initializeIssues() = arrayOf(
		Issue("Undefended in lava",
			{p: ClientPlayerEntity ->
				var noFireRes = true
				if (p.inventory.contains(ItemStack(Items.TOTEM_OF_UNDYING)) ||
					p.inventory.contains(ItemStack(Items.ENCHANTED_GOLDEN_APPLE))) {
					noFireRes = false
				}
				if (p.hasStatusEffect(StatusEffects.FIRE_RESISTANCE)) {
					noFireRes = false
				}
				p.isInLava && noFireRes},
			100,
			true,
			action = {c: MinecraftClient, i: Issue ->
				val coords = c.player?.pos.toString()
				i.enabled = false
				c.world?.disconnect()
				c.disconnect()
				c.setScreen(DisconnectedScreen(
					MultiplayerScreen(TitleScreen()),
					Text.of("You fell undefended in lava and were logged of by Skill Issue!"),
					Text.of("Your coordinates are: $coords")
				))}),

		Issue("Low health",
			{p: ClientPlayerEntity -> p.health <= 8 },
			20,
			true,
			0x60820C01),

		Issue("No totem equipped",
			{p: ClientPlayerEntity -> p.offHandStack.isEmpty || p.offHandStack.item != Items.TOTEM_OF_UNDYING },
			15,
			false,
			0x60E8B50D),

		Issue("No shield equipped",
			{p: ClientPlayerEntity -> p.offHandStack.isEmpty || p.offHandStack.item != Items.SHIELD },
			13,
			false,
			0x60E8B50D),

		Issue("No food in hotbar",
			{p: ClientPlayerEntity ->
				var nofood = true
				for (i in 0..8) if (p.inventory.getStack(i).get(DataComponentTypes.FOOD) != null) nofood = false
				nofood},
			12,
			false,
			0x60492100),

		Issue("Low hunger",
			{ p: ClientPlayerEntity -> p.hungerManager.foodLevel <= 8 },
			10,
			true,
			0x60492100))

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
		var highestPriorityIssue : Issue?
		ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick { client ->
			client.player?.let { player ->
				presentIssues.clear()
				existingIssues.filterTo(presentIssues) { it.issue(player) && it.enabled }
				highestPriorityIssue = presentIssues.maxByOrNull { it.priority }
				if (highestPriorityIssue?.action == null) {
					IssueRenderer.highestPriorityIssue = highestPriorityIssue
				} else {
					highestPriorityIssue?.action?.let { it(MinecraftClient.getInstance(), highestPriorityIssue!!) }
				}
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