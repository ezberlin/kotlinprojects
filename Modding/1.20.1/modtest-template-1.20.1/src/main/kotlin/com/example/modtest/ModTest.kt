package com.example.modtest

import com.example.modtest.items.IceCube
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import org.slf4j.LoggerFactory
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.text.Text
import net.minecraft.util.Identifier


object ModTest : ModInitializer {
    private val logger = LoggerFactory.getLogger("modtest")

	private const val MODID = "modtest"

	private val MODTEST_ITEMS = RegistryKey.of(RegistryKeys.ITEM_GROUP,
		Identifier("modtest", "modtest_items"))

	private val ICE_CUBE: IceCube = Registry.register(
		Registries.ITEM,
		Identifier(MODID, "ice_cube"),
		IceCube(FabricItemSettings().maxCount(16).fireproof()),
	)



	override fun onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		logger.info("Hello Fabric world!")
		Registry.register(
			Registries.ITEM_GROUP,
			MODTEST_ITEMS,
			FabricItemGroup.builder().icon { ItemStack(ICE_CUBE) }
				.displayName(
					Text.translatable("itemGroup.$MODID.modtest_items")
				)
				.build(),
		)

		ItemGroupEvents.modifyEntriesEvent(MODTEST_ITEMS).register(ItemGroupEvents.ModifyEntries { content ->
			content.add(ICE_CUBE)
		})
	}
}