package name.modid

import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity

class Issue(
    val name : String,
    val issue: (ClientPlayerEntity) -> Boolean,
    val action: (MinecraftClient, Issue) -> Unit,
    val priority: Int,
    var enabled: Boolean)
