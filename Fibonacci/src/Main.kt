fun main() {
    println("Enter an integer: ")
    val userInput = readln()
    val output = fibonacci(userInput.toInt())

    println("The Fibonacci number of $userInput is $output")
}

fun fibonacci(input : Int): Int {
    when (input) {
        1 -> return 0
        2 -> return 1
        else -> {
            var n1 = 0
            var n = 1
            var newn: Int
            for (i in 1..<input-1) {
                newn = n + n1
                n1 = n
                n = newn
            }
            return n
        }
    }
}