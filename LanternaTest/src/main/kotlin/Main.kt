package org.example

import com.googlecode.lanterna.TextColor
import com.googlecode.lanterna.terminal.DefaultTerminalFactory
import com.googlecode.lanterna.terminal.Terminal

fun main() {
    val terminal = DefaultTerminalFactory().createTerminal()
    terminal.setCursorPosition(0, 0)
    while (true) {
        terminal.putCharacter(terminal.readInput().character)
        terminal.flush()
    }

}

fun putString(terminal: Terminal, string: String) {
    terminal.apply {
        string.forEach { putCharacter(it) }
    }
    terminal.flush()
}