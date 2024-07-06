package name.modid

import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.text.Text

class IssueToggleScreen(private val existingIssues: Array<Issue>) : Screen(Text.literal("Toggle Issues")) {
    private val buttonHeight = 20
    private val buttonWidth = 250
    private val initialY = 36
    private val marginY = 24
    private val textY = 10

    private fun buildButtons() {
        val buttonX = (width - buttonWidth) / 2
        var buttonY = initialY

        existingIssues.sortedByDescending {issue -> issue.priority}.forEachIndexed { _, issue ->
            val button = ButtonWidget.builder(
                Text.of("${issue.name}: ${if (issue.enabled) "Enabled" else "Disabled"} ")
            ) { btn ->
                issue.enabled = !issue.enabled
                ConfigManager.saveConfig(existingIssues)
                btn.message = Text.of("${issue.name}: ${if (issue.enabled) "Enabled" else "Disabled"} ")
            }.dimensions(buttonX, buttonY, buttonWidth, buttonHeight).build()

            addDrawableChild(button)
            buttonY += marginY
        }
    }

    override fun init() {
        super.init()
        buildButtons()
    }

    override fun render(context: DrawContext?, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(context, mouseX, mouseY, delta)
        super.render(context, mouseX, mouseY, delta)

        val textWidth = textRenderer.getWidth(title)
        val centerX = (width - textWidth) / 2
        context?.drawText(this.textRenderer, title, centerX, textY, 0xFFFFFF, true)
    }

    override fun shouldCloseOnEsc(): Boolean {
        return true
    }
}
