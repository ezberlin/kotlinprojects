fun sayHello(greeting: String, vararg itemsToGreet: String) {
    itemsToGreet.forEach { itemToGreet ->
        println("$greeting $itemToGreet!")
    }
}

fun greetPerson(greeting: String = "Hello", name: String = "Kotlin") = println("$greeting $name!")





// Definition einer Beispielklasse
class KlassenBeispiel(
    // Argumente
    x: Float,
    var text: String
) {
    // Attribute werden wie eine Variable in der Klasse definiert
    val zahl = x.toInt()

    // init-Block
    init {
        println("Klasse wurde initialisiert mit Float $x")
    }

    // Interne Methode
    fun textUmdrehen() {
        text = text.reversed()
    }

    // Methode mit Rückgabewert
    fun ersteXTextBuchstaben(): String {
        return text.slice(0..<zahl)
    }
}

fun main() {
    val objekt = KlassenBeispiel(4f, "Hallo Wiki")
    // -> Klasse wurde initialisiert mit Float 4.0

    println("Zahl ist ${objekt.zahl}, Text ist ${objekt.text}")
    // -> Zahl ist 4, Text ist Hallo Wiki

    objekt.textUmdrehen()
    println(objekt.text)
    // -> ikiW ollaH

    println(objekt.ersteXTextBuchstaben())
    // -> ikiW
}