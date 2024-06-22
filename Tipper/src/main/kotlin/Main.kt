package org.example

import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.round

val matchResults = listOf(
    MatchResult(1.29, 5.75, 9.75, "5:1"),
    MatchResult(3.80, 3.10, 2.15, "1:3"),
    MatchResult(1.87, 3.40, 4.33, "3:0"),
    MatchResult(1.36, 4.75, 9.75, "2:1"),
    MatchResult(6.50, 4.20, 1.50, "1:2"),
    MatchResult(5.75, 3.60, 1.66, "1:1"),
    MatchResult(6.25, 4.33, 1.51, "0:1")
)

val matchUndet = listOf(
    MatchUndet(4.40, 3.50, 1.85),
    MatchUndet(1.47, 3.10, 6.75),
    MatchUndet(5.50, 3.40, 1.60),
    MatchUndet(1.73, 4.75, 5.00),
    MatchUndet(1.49, 4.20, 6.50),
)


fun tipEval(q1: Double, qu: Double, q2: Double, adjust1: Double, adjust2: Double, adjust3: Double): String {
    var g1 = round((q2 / adjust1).pow(adjust2) + adjust3).toInt()
    var g2 = round((q1 / adjust1).pow(adjust2) + adjust3).toInt()

    // Ensure that the goals are not negative
    if (g1 < 0) g1 = 0
    if (g2 < 0) g2 = 0

    return "$g1:$g2"
}


fun calculateError(predicted: String, actual: String): Int {
    val (predG1, predG2) = predicted.split(":").map { it.toInt() }
    val (actG1, actG2) = actual.split(":").map { it.toInt() }
    return abs(predG1 - actG1) + abs(predG2 - actG2)
}

fun findBestParameters(matchResults: List<MatchResult>, ad1Range: List<Double>, ad2Range: List<Double>, ad3Range: List<Double>): Triple<Double, Double, Double> {
    var bestAd1 = 0.0
    var bestAd2 = 1.0
    var bestAd3 = 1.0
    var bestError = Int.MAX_VALUE

    for (ad1 in ad1Range) {
        for (ad2 in ad2Range) {
            for (ad3 in ad3Range) {
                var totalError = 0

                for (match in matchResults) {
                    val predicted = tipEval(match.q1, match.qu, match.q2, ad1, ad2, ad3)
                    totalError += calculateError(predicted, match.actualResult)
                }

                if (totalError < bestError) {
                    bestError = totalError
                    bestAd1 = ad1
                    bestAd2 = ad2
                    bestAd3 = ad3
                }
            }
        }
    }

    return Triple(bestAd1, bestAd2, bestAd3, )
}

fun main() {
    val ad1Range = (1..400).map { it / 50.0 } // Probabilities from 0.0 to 1.0 in steps of 0.01
    val ad2Range = (50..200).map { it / 100.0 } // Factors from 0.5 to 1.5 in steps of 0.01
    val ad3Range = (1..400).map { it / 100.0 } // Factors from 0 to 10 in steps of 1

    val bestParameters = findBestParameters(matchResults, ad1Range, ad2Range, ad3Range)
    println("Best parameters - : ${bestParameters.first}, : ${bestParameters.second}, : ${bestParameters.third}")
    for (match in matchResults) {
        println(tipEval(match.q1, match.qu, match.q2, bestParameters.first, bestParameters.second, bestParameters.third))
    }
    for (match in matchUndet) {
        println(tipEval(match.q1, match.qu, match.q2, bestParameters.first, bestParameters.second, bestParameters.third))
    }
}


