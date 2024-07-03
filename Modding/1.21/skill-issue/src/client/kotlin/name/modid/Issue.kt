package name.modid

import net.minecraft.client.network.ClientPlayerEntity

class Issue(
    val issue: (ClientPlayerEntity) -> Boolean,
    val color: Int,
    val priority: Int)