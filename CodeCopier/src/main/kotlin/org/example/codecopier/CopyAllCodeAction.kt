package org.example.codecopier

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.TextEditor
import com.intellij.openapi.ui.Messages
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

class CopyAllCodeAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        println("Performed")
        val fileEditorManager = FileEditorManager.getInstance(e.project!!)
        val codeToCopy = StringBuilder()

        val fileEditors = fileEditorManager.allEditors
        for (fileEditor in fileEditors) if (fileEditor != null) {
            val fileName = fileEditor.file.name
            val fileContent = fileEditor.getFileContent()
            codeToCopy.append("""
            ${fileName}:
            ------
            $fileContent
            """.trimIndent())
        }

        if (codeToCopy.isNotEmpty()) {
            val clipboard = Toolkit.getDefaultToolkit().systemClipboard
            clipboard.setContents(StringSelection(codeToCopy.toString()), null)

            // Show a message dialog for confirmation
            Messages.showInfoMessage("Code from all open files copied to clipboard!", "Success")
        } else {
            Messages.showInfoMessage("No open files to copy.", "Info")
        }
    }

    private fun FileEditor.getFileContent(): String? {
        if (this is TextEditor) {
            val document: Document = this.editor.document
            return document.text
        }
        return null
    }
}