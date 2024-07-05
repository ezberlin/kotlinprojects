package name.modid

import net.minecraft.client.network.ClientPlayerEntity

class Issue(
    val name : String,
    val issue: (ClientPlayerEntity) -> Boolean,
    val color: Int,
    val priority: Int,
    var enabled: Boolean)