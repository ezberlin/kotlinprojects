package name.modid

import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.text.Text

class IssueToggleScreen(existingIssues: Array<Issue>) : Screen(Text.literal("Toggle Issues")) {
    init {
        var y = 20
        existingIssues.forEachIndexed { _, issue ->
            val button = ButtonWidget.builder(
                Text.of("${if (issue.enabled) "Disable" else "Enable"} ${issue.name}")
            ) { btn ->
                issue.enabled = !issue.enabled
                btn.message =
                    Text.of("${if (issue.enabled) "Disable" else "Enable"} ${issue.name}")
            }.dimensions(20, y, 150, 20).build()

            addDrawableChild(button)
            y += 48
        }
    }

    override fun render(context: DrawContext?, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(context, mouseX, mouseY, delta) // Render the default background
        super.render(context, mouseX, mouseY, delta) // Render buttons and other elements

        val textWidth = textRenderer.getWidth(title)
        // Calculate the center position
        val centerX = (width - textWidth) / 2
        // Draw the text
        context?.drawText(this.textRenderer, title, centerX, 10, 0xFFFFFF, true)
    }

    override fun shouldCloseOnEsc(): Boolean {
        return true // Allow closing the screen with the ESC key
    }
}
