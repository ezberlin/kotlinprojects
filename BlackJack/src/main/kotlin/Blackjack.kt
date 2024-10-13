package org.example

class Blackjack : CardGame() {
    private var deck = cards.shuffled().toMutableList()
    var dealerCards = mutableListOf<Card>()
    var playerCards = mutableListOf<Card>()
    var gameState = "initialized"
    override var availableOptionsList = mutableListOf<String>()

    init {
        updateAvailableOptions()
    }

    override val optionMap = mapOf(
        "start" to { start() },
        "hit" to { hit() },
        "pass" to { pass() }
    )


    private fun resetDeck() {
        deck = cards.shuffled().toMutableList()
    }

    private fun drawCards(n: Int): List<Card> {
        val drawnCards = mutableListOf<Card>()
        for (i in 1..n) drawnCards.add(deck.removeFirst())
        return drawnCards
    }

    private fun drawCard() = drawCards(1)[0]

    private fun hardCheckCards(cards: List<Card>): Int {
        var sum = 0
        for (card in cards) sum += when (card.value) {
            Value.ACE -> 1
            Value.TWO -> 2
            Value.THREE -> 3
            Value.FOUR -> 4
            Value.FIVE -> 5
            Value.SIX -> 6
            Value.SEVEN -> 7
            Value.EIGHT -> 8
            Value.NINE -> 9
            Value.TEN -> 10
            Value.JACK -> 10
            Value.QUEEN -> 10
            Value.KING -> 10
        }
        return sum
    }

    private fun checkCards(cards: List<Card>): Int {
        var sum = 0
        for (card in cards) sum += when (card.value) {
            Value.ACE -> 11
            Value.TWO -> 2
            Value.THREE -> 3
            Value.FOUR -> 4
            Value.FIVE -> 5
            Value.SIX -> 6
            Value.SEVEN -> 7
            Value.EIGHT -> 8
            Value.NINE -> 9
            Value.TEN -> 10
            Value.JACK -> 10
            Value.QUEEN -> 10
            Value.KING -> 10
        }
        return if (sum <= 21) sum else hardCheckCards(cards)
    }

    private fun updateAvailableOptions() {
        availableOptionsList.clear()
        availableOptionsList.add("start")
        if (gameState == "running") {
            availableOptionsList.add("hit")
            availableOptionsList.add("pass")
        }
    }

    private fun win() {
        gameState = "win"
        updateAvailableOptions()
    }

    private fun lose() {
        gameState = "lose"
        updateAvailableOptions()
    }

    private fun tie() {
        gameState = "tie"
        updateAvailableOptions()
    }

    private fun overflow() {
        gameState = "overflow"
        updateAvailableOptions()
    }

    private fun running() {
        gameState = "running"
        updateAvailableOptions()
    }

    private fun start() {
        resetDeck()
        dealerCards.clear()
        playerCards.clear()

        dealerCards.add(drawCard())
        playerCards.addAll(drawCards(2))

        running()
    }
    
    private fun hit() {
        playerCards.add(drawCard())
        if (checkCards(playerCards) > 21) overflow()
    }

    private fun pass() {
        while (checkCards(dealerCards) < 17) {
            dealerCards.add(drawCard())
            if (checkCards(dealerCards) > 21) {
                win()
                return
            }
        }

        if (checkCards(playerCards) > checkCards(dealerCards)) win()
        else if (checkCards(playerCards) < checkCards(dealerCards)) lose()
        else if (checkCards(playerCards) == checkCards(dealerCards)) tie()
        else tie()
    }
}