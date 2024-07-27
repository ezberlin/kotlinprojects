package org.example

fun main() {
    val board = Board(arrayOf(
        Space(), Space(), Space(),
        Space(), Space(), Space(),
        Space(), Space(), Space()))

    while (true) {
        board.renderBoard()
        collectMove("X", board)
        if (board.checkForWinner() != 0 || board.checkForDraw()) break
        board.renderBoard()
        collectMove("O", board)
        if (board.checkForWinner() != 0 || board.checkForDraw()) break

    }

    board.renderBoard()
    if (board.checkForWinner() == 1) {
        println("X wins!")
    } else if (board.checkForWinner() == 2) {
        println("O wins!")
    } else println("Draw!")
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