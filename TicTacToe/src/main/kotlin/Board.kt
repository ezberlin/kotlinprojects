package org.example

class Board (val spaces : Array<Space>) {

    fun renderBoard() {
        val lines = arrayOf(
            "  ${spaces[0].content}  |  ${spaces[1].content}  |  ${spaces[2].content}",
            "-----+-----+-----",
            "  ${spaces[3].content}  |  ${spaces[4].content}  |  ${spaces[5].content}",
            "-----+-----+-----",
            "  ${spaces[6].content}  |  ${spaces[7].content}  |  ${spaces[8].content}")
        for (line in lines) {
            println(line)
        }
    }

    fun checkForWinner(): Int {
        val firstSpaces = mutableListOf<Int>()
        for (spaceIndex in spaces.indices) if (spaces[spaceIndex].value == 1) firstSpaces.add(spaceIndex)

        val secondSpaces = mutableListOf<Int>()
        for (spaceIndex in spaces.indices) if (spaces[spaceIndex].value == 2) secondSpaces.add(spaceIndex)

        fun checkForWinningLine(a : Int, b : Int, c : Int): Boolean {
            if (a % 3 == b % 3 && b % 3 == c % 3) return true
            if (a - (a % 3) == b - (b % 3) && b - (b % 3) == c - (c % 3)) return true
            if (a == 0 && b == 4 && c == 8) return true
            if (a == 2 && b == 4 && c == 6) return true
            return false
        }

        if (firstSpaces.size >= 3) {
            for (a in 0 until firstSpaces.size - 2) {
                for (b in a + 1 until firstSpaces.size - 1) {
                    for (c in b + 1 until firstSpaces.size) {

                        if (checkForWinningLine(firstSpaces[a], firstSpaces[b], firstSpaces[c])) return 1
                    }
                }
            }
        }

        if (secondSpaces.size >= 3) {
            for (a in 0 until secondSpaces.size - 2) {
                for (b in a + 1 until secondSpaces.size - 1) {
                    for (c in b + 1 until secondSpaces.size) {

                        if (checkForWinningLine(secondSpaces[a], secondSpaces[b], secondSpaces[c])) return 2
                    }
                }
            }
        }
        return 0
    }

    fun checkForDraw(): Boolean {
        for (space in spaces) {
            if (space.value == 0) return false
        }
        return true
    }
}