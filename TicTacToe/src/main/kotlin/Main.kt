package org.example

import com.googlecode.lanterna.terminal.DefaultTerminalFactory
import com.googlecode.lanterna.terminal.Terminal

val terminal = DefaultTerminalFactory().createTerminal()!!

fun main() {
    val board = Board(arrayOf(
        Space(), Space(), Space(),
        Space(), Space(), Space(),
        Space(), Space(), Space()))

    while (true) {
        board.renderBoard()
        collectMove("X", board)
        clearTerminal(terminal)
        if (board.checkForWinner() != 0 || board.checkForDraw()) break
        board.renderBoard()
        collectMove("O", board)
        clearTerminal(terminal)
        if (board.checkForWinner() != 0 || board.checkForDraw()) break

    }

    board.renderBoard()
    if (board.checkForWinner() == 1) {
        putString(terminal, "X wins!")
    } else if (board.checkForWinner() == 2) {
        putString(terminal, "O wins!")
    } else putString(terminal, "Draw!")
}

fun collectMove(player: String, board: Board) {
    putString(terminal, "$player's turn!")
    while (true) {
        val stringMove = terminal.readInput().character.toString()
        terminal.flush()
        try {
            if (stringMove.toInt() in 0..8) {
                val move = stringMove.toInt()
                if (board.spaces[move].value == 0) {
                    board.spaces[move].value = if (player == "X") 1 else 2
                    break
                }
            }
            putString(terminal, "This cell is occupied! Choose another one!")
        } catch (e: Exception) {
            putString(terminal, "You should enter numbers!")
        }
    }
}

fun putString(terminal: Terminal, string: String) {
    for (char in string) {
        terminal.putCharacter(char)
        terminal.flush()
    }
    terminal.setCursorPosition(0, terminal.cursorPosition.row + 1)
    terminal.flush()
}

fun clearTerminal(terminal: Terminal) {
    terminal.setCursorPosition(0, 0)
    terminal.clearScreen()
    terminal.flush()
}