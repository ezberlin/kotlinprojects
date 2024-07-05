package name.modid

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext

// Update the IssueRenderer to store the highest priority issue directly
class IssueRenderer {
    companion object {
        var highestPriorityIssue: Issue? = null

        fun renderOverlay(drawContext: DrawContext, issue: Issue) {
            val client = MinecraftClient.getInstance()
            val window = client.window
            val width = window.scaledWidth
            val height = window.scaledHeight

            drawContext.fill(0, 0, width, height, issue.color)
        }
    }
}