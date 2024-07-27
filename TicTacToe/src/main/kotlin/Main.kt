package org.example

fun main() {
    val board = Board(arrayOf(
        Space(), Space(), Space(),
        Space(), Space(), Space(),
        Space(), Space(), Space()))

    while (true) {
        board.renderBoard()
        collectMove("X", board)
        clearConsole()
        if (board.checkForWinner() != 0) break
        board.renderBoard()
        collectMove("O", board)
        clearConsole()
        if (board.checkForWinner() != 0) break

    }
    board.renderBoard()
    if (board.checkForWinner() == 1) println("X wins!") else println("O wins!")
}

fun collectMove(player: String, board: Board) {
    println("$player's turn!")
    while (true) {
        val stringMove = readln()
        try {
            if (stringMove.toInt() in 0..8) {
                val move = stringMove.toInt()
                if (board.spaces[move].value == 0) {
                    board.spaces[move].value = if (player == "X") 1 else 2
                    break
                }
            }
            println("This cell is occupied! Choose another one!")
        } catch (e: Exception) {
            println("You should enter numbers!")
        }
    }
}

fun clearConsole() {
    print("\u001B[2J\u001B[;H")
}