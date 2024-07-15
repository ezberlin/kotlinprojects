package org.example

fun main() {
    val logins = Logins
    val dsbMobile = APIHandler(logins.USERNAME, logins.PASSWORD)
    dsbMobile.fetchData()
}