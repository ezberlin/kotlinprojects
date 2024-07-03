package name.modid

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext

class IssueRenderer {
    companion object {
        var highestPriorityIssueIndex: Int? = null

        fun renderOverlay(drawContext: DrawContext, issue: Issue) {
            val client = MinecraftClient.getInstance()
            val window = client.window
            val width = window.scaledWidth
            val height = window.scaledHeight

            drawContext.fill(0, 0, width, height, issue.color)
        }
    }
}