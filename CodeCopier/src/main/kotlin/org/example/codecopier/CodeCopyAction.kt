package org.example.codecopier

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.ui.Messages
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

class CopyCodeAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        var codeToCopy = ""
        val fileEditorManager = FileEditorManager.getInstance(e.project!!)
        val editors: Array<FileEditor> = fileEditorManager.allEditors

        for (editor in editors) {
            val selectedTextEditor = fileEditorManager.selectedTextEditor
            if (selectedTextEditor != null) {
                val fileName = selectedTextEditor.virtualFile.name
                val fileContent = selectedTextEditor.document.text
                codeToCopy = """
                ${fileName}:
                ------
                $fileContent
                """.trimIndent()
            }
        }

        if (codeToCopy.isNotEmpty()) {
            val clipboard = Toolkit.getDefaultToolkit().systemClipboard
            clipboard.setContents(StringSelection(codeToCopy), null)

            // Show a message dialog for confirmation
            Messages.showInfoMessage("Code copied to clipboard!", "Success")
        } else {
            Messages.showInfoMessage("No open files to copy.", "Info")
        }
    }
}
