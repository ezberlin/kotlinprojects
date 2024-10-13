package org.example

abstract class CardGame {
    protected val cards = mutableSetOf<Card>()
        .apply { for (suit in Suit.entries) for (value in Value.entries)
            this.add(Card(suit, value)) }
        .toSet()

    abstract val optionMap: Map<String, () -> Unit>
    abstract val availableOptionsList: MutableList<String>



}