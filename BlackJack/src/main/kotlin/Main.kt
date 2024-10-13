package org.example

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main

fun clearConsole() {
    System.out.flush()
}
class BlackjackCommand: CliktCommand() {
    override fun run() {
        val blackjack = Blackjack()
        var input: String
        while (true) {
            input = readln()

            print("\u001b[H\u001b[2J")

            clearConsole()

            if (input in blackjack.availableOptionsList) {
                blackjack.optionMap[input]?.invoke()
            } else {
                echo("Please enter a valid command")
            }

            // Function to print cards
            fun printCards(cards: List<Card>, font: CardFont) {
                val cardLines = cards.map { font.visualize(it).lines() }
                val maxLines = cardLines.firstOrNull()?.size ?: 0
                for (i in 0 until maxLines) {
                    var printedLine = ""
                    for (card in cardLines) {
                        printedLine += "${card[i]} "
                    }
                    echo(printedLine)
                }
            }

            // Print dealer cards
            echo("Dealer's cards:")
            printCards(blackjack.dealerCards, trueCardFont)

            echo()

            echo("Player's cards:")
            printCards(blackjack.playerCards, trueCardFont)

            echo(blackjack.gameState)

            echo(blackjack.availableOptionsList)
        }
    }
}

fun main(args: Array<String>) = BlackjackCommand().main(args)