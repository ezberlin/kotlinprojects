import java.io.File

fun main() {
    val letterFrequency = IntArray(26)
    val words = mutableListOf<String>()

    // Read file and process text
    File("script.txt").bufferedReader().useLines { lines ->
        lines.forEach { line ->
            val text = line.toLowerCase()
            text.forEach { char ->
                if (char in 'a'..'z') {
                    letterFrequency[char - 'a']++
                }
            }
            words.addAll(text.split("\\W+".toRegex()).filter { it.isNotEmpty() })
        }
    }

    // Precompute sets of words that can be formed with each subset of letters
    val wordSets = mutableMapOf<Set<Char>, MutableSet<String>>()
    for (word in words) {
        val charSet = word.toSet()
        wordSets.computeIfAbsent(charSet) { mutableSetOf() }.add(word)
    }

    // Generate all combinations of n letters
    fun combinations(letters: List<Char>, n: Int): List<List<Char>> {
        val result = mutableListOf<List<Char>>()
        val combination = IntArray(n)
        val len = letters.size

        fun nextCombination(): Boolean {
            var i = n - 1
            while (i >= 0 && combination[i] == len - n + i) i--
            if (i < 0) return false
            combination[i]++
            for (j in i + 1 until n) combination[j] = combination[j - 1] + 1
            return true
        }

        combination.indices.forEach { combination[it] = it }
        do {
            result.add(combination.map { letters[it] })
        } while (nextCombination())

        return result
    }

    // Check how many words can be formed with the given letters using precomputed sets
    fun countWords(letters: Set<Char>): Int {
        return wordSets.filterKeys { it.all { char -> char in letters } }.values.sumOf { it.size }
    }

    // Find the best combination for each n
    val sortedLetters = ('a'..'z').filter { letterFrequency[it - 'a'] > 0 }
    for (n in 1..26) {
        val bestCombination = if (n <= 13) {
            combinations(sortedLetters, n).maxByOrNull { countWords(it.toSet()) }
        } else {
            val complementCombinations = combinations(sortedLetters, 26 - n)
            val bestComplement = complementCombinations.maxByOrNull { countWords(it.toSet()) }
            bestComplement?.let { sortedLetters - it.toSet() }
        }
        val bestLettersSet = bestCombination?.toSet() ?: emptySet()
        val totalWords = words.size
        val wordsWithOnlyBestLetters = countWords(bestLettersSet)
        val percentage = if (totalWords > 0) (wordsWithOnlyBestLetters.toDouble() / totalWords) * 100 else 0.0
        println("Best $n letters: ${bestCombination?.joinToString(", ")} - ${"%.2f".format(percentage)}% of words")
    }
}