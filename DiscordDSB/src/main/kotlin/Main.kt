package org.example


val discord = DiscordHandler(Logins.DCTOKEN, Logins.GUILDID)
val dsb = APIHandler(Logins.USERNAME, Logins.PASSWORD, arrayOf("Vtr-Nr.", "Neu", "Klassen", "Stunden", "Art", "Fach", "Raum", "(Fach)", "(Raum)", "Text", "Vtr.-Text-2"))
val validClasses = arrayOf(
    "5N",   "6N",  "7N",
    "7A",   "7B",  "7C",
    "8N",   "8A",  "8B",
    "9N",   "9A",  "9B",
    "10XN", "10A", "10B",
    "11w",  "12w", "WiKo")

suspend fun main() {
    discord.bot()
}

fun getClassEntries(classreq: String): String {
    val classEntries = mutableListOf<MutableMap<String, String>>()
    val entries = dsb.fetchTimetable()
    for (entry in entries) {
        if (entry["Klassen"]?.contains(classreq) == true) {
            classEntries.add(entry)
        }
    }

    if (classEntries.isEmpty()) {
        return "No entries found for class '$classreq'."
    }

    val formattedEntries = classEntries.joinToString("\n") { entry ->
        val stunden = entry["Stunden"] ?: ""
        val art = entry["Art"] ?: ""
        val fach = entry["Fach"] ?: ""
        val raum = entry["Raum"] ?: ""
        val datum = entry["date"] ?: ""

        """
        Am $datum in der $stunden. Stunde $art: $fach in $raum
        """.trimIndent()
    }

    return formattedEntries
}