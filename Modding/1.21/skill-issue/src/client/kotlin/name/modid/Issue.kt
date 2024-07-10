package name.modid

import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.item.ItemStack

class Issue(
    val name : String,
    val icon : ItemStack?,
    val issue: (ClientPlayerEntity) -> Boolean,
    val action: (MinecraftClient, Issue) -> Unit,
    val priority: Int,
    var enabled: Boolean)
