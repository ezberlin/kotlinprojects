package org.example

val minimalistFont = CardFont {
    var result = """""".trimIndent()

    result += when (it.suit) {
        Suit.HEARTS -> "♡"
        Suit.DIAMONDS -> "♢"
        Suit.CLUBS -> "♧"
        Suit.SPADES -> "♤"
    }

    result += when (it.value) {
        Value.ACE -> "A"
        Value.TWO -> "2"
        Value.THREE -> "3"
        Value.FOUR -> "4"
        Value.FIVE -> "5"
        Value.SIX -> "6"
        Value.SEVEN -> "7"
        Value.EIGHT -> "8"
        Value.NINE -> "9"
        Value.TEN -> "10"
        Value.JACK -> "J"
        Value.QUEEN -> "Q"
        Value.KING -> "K"
    }
    result
}

val trueCardFont = CardFont {
    val s = when (it.suit) {
        Suit.HEARTS -> "♡"
        Suit.DIAMONDS -> "♢"
        Suit.CLUBS -> "♧"
        Suit.SPADES -> "♤"
    }

    val vl = when (it.value) {
        Value.ACE -> "A "
        Value.TWO -> "2 "
        Value.THREE -> "3 "
        Value.FOUR -> "4 "
        Value.FIVE -> "5 "
        Value.SIX -> "6 "
        Value.SEVEN -> "7 "
        Value.EIGHT -> "8 "
        Value.NINE -> "9 "
        Value.TEN -> "10"
        Value.JACK -> "J "
        Value.QUEEN -> "Q "
        Value.KING -> "K "
    }

    """
        ┌─────┐
        │$vl   │
        │  $s  │
        │   $vl│
        └─────┘
    """.trimIndent()
}