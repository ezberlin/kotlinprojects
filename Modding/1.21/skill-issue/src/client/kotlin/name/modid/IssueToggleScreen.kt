package name.modid

import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.item.Items
import net.minecraft.text.Text

class IssueToggleScreen(private val existingIssues: Array<Issue>,
                        private val currentIssues: Array<Issue>) :
    Screen(Text.literal("Toggle Issues")) {

    private val buttonHeight = 20
    private val buttonWidth = 250
    private val initialY = 48
    private val marginY = 24
    private val textY = 24

    private val totemItem = Items.TOTEM_OF_UNDYING.defaultStack


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
        context?.drawItem(totemItem, 10, 10)
        for (index in existingIssues.sortedByDescending { issue -> issue.priority }.indices) context?.drawItem(
            existingIssues[index].icon,
            (centerX / 1.9).toInt(),
            initialY + index * marginY + 1)

        for (index in existingIssues.indices) if (existingIssues[index] in currentIssues) context?.drawItem(
            Items.SLIME_BALL.defaultStack,
            (centerX * 1.9).toInt(),
            initialY + index * marginY + 1)
    }

    override fun shouldCloseOnEsc(): Boolean {
        return true
    }
}
