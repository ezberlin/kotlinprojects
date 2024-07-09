package name.modid

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext

class IssueRenderer {
    companion object {

        var color : Int? = null

        fun renderOverlay(drawContext: DrawContext) {
            val client = MinecraftClient.getInstance()
            val window = client.window
            val width = window.scaledWidth
            val height = window.scaledHeight
            if (color != null) drawContext.fill(0, 0, width, height, color!!)
        }
    }
}