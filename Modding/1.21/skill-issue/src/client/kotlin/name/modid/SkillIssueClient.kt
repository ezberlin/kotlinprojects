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
			Items.LAVA_BUCKET.defaultStack,
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
			{ c: MinecraftClient, i: Issue ->
				val coords = c.player?.pos.toString()
				i.enabled = false
				c.world?.disconnect()
				c.disconnect()
				c.setScreen(DisconnectedScreen(
					MultiplayerScreen(TitleScreen()),
					Text.of("You fell undefended in lava and were logged of by Skill Issue!"),
					Text.of("Your coordinates are: $coords")
				))},
			120,
			true,
			),

		Issue("Falling dangerously high",
			Items.LEATHER_BOOTS.defaultStack,
			{p: ClientPlayerEntity ->
				p.velocity.y < -1.6},
			{ c: MinecraftClient, i: Issue ->
				val coords = c.player?.pos.toString()
				i.enabled = false
				c.world?.disconnect()
				c.disconnect()
				c.setScreen(DisconnectedScreen(
					MultiplayerScreen(TitleScreen()),
					Text.of("You fell from a dangerously high place and were logged of by Skill Issue!"),
					Text.of("Your coordinates are: $coords")
				))},
			110,
			true,
		),

		Issue("Low health",
			Items.POPPY.defaultStack,
			{p: ClientPlayerEntity -> p.health <= 8 },
			{_, _ -> IssueRenderer.color = 0x60820C01},
			20,
			true),

		Issue("No totem equipped",
			Items.TOTEM_OF_UNDYING.defaultStack,
			{p: ClientPlayerEntity -> p.offHandStack.isEmpty || p.offHandStack.item != Items.TOTEM_OF_UNDYING },
			{_, _ -> IssueRenderer.color = 0x60E8B50D},
			15,
			false),

		Issue("No shield equipped",
			Items.SHIELD.defaultStack,
			{p: ClientPlayerEntity -> p.offHandStack.isEmpty || p.offHandStack.item != Items.SHIELD },
			{_, _ -> IssueRenderer.color = 0x6068412b},
			13,
			false),

		Issue("No food in hotbar",
			Items.GOLDEN_CARROT.defaultStack,
			{p: ClientPlayerEntity ->
				var noFood = true
				for (i in 0..8) if (p.inventory.getStack(i).get(DataComponentTypes.FOOD) != null) noFood = false
				noFood},
			{_, _ -> IssueRenderer.color = 0x60492100},
			12,
			false),

		Issue("Low hunger",
			Items.BEEF.defaultStack,
			{ p: ClientPlayerEntity -> p.hungerManager.foodLevel <= 8 },
			{_, _ -> IssueRenderer.color = 0x60492100},
			10,
			true))

	private fun setupHudRenderCallback() {

		HudRenderCallback.EVENT.register(HudRenderCallback { matrixStack, _ ->
			IssueRenderer.renderOverlay(matrixStack)
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
			IssueRenderer.color = null
			client.player?.let { player ->
				presentIssues.clear()
				existingIssues.filterTo(presentIssues) { it.issue(player) && it.enabled }
				highestPriorityIssue = presentIssues.maxByOrNull { it.priority }
				highestPriorityIssue?.action?.let { it(MinecraftClient.getInstance(), highestPriorityIssue!!) }
			}
			if (toggleIssuesKeyBinding.wasPressed()) {
				client.setScreen(IssueToggleScreen(existingIssues, presentIssues.toTypedArray()))
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