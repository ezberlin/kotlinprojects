class Person(val firstName: String = "Peter", val lastName: String = "Parker") {

    var nickname: String? = null
        set(value) {
            field = value
            println("The new nickname is $value!")
        }
        get() {
            //print("The returned value is $field!")
            return field
        }

    fun printInfo() {
        val nicknameToPrint =  nickname ?: "no nickname"
        println("$firstName ($nicknameToPrint) $lastName")
    }
}