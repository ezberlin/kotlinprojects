package org.example

import kotlinx.coroutines.*

val discord = DiscordHandler(Logins.DCTOKEN)
val dsb = APIHandler(Logins.USERNAME, Logins.PASSWORD, arrayOf("Vtr-Nr.", "Neu", "Klassen", "Stunden", "Art", "Fach", "Raum", "(Fach)", "(Raum)", "Text", "Vtr.-Text-2"))
val validClasses = arrayOf(
    "4N",
    "5N",   "6N",  "7N",
    "7A",   "7B",  "8N",
    "8A",   "8B",  "8C",
    "9N",   "9A",  "9B",
    "10N",  "10A", "10B",
    "11w",  "12w", "WiKo")

val planToMessage : (Map<String, String>) -> String = { entry ->
    val stunden = entry["Stunden"] ?: ""
    val art = entry["Art"] ?: ""
    val fach = entry["Fach"] ?: ""
    val raum = entry["Raum"] ?: ""
    val datum = entry["date"] ?: ""
    val klassen = entry["Klassen"] ?: ""

    """
    $klassen: Am $datum in der $stunden. Stunde $art: $fach in $raum
    """.trimIndent()
}

suspend fun main() {
    coroutineScope {
        launch {
            checkTimetableChanges()
        }
        discord.bot()
    }
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
        return "Es gibt keine Einträge für '$classreq'."
    }

    val formattedEntries = classEntries.joinToString("\n", transform = planToMessage)

    return formattedEntries
}

fun getFullUrl(): String? {
    dsb.fetchTimetable()
    return dsb.tableURL
}

var previousTimetable: List<Map<String, String>>? = null

fun hasTimetableChanged(): List<Map<String, String>>? {
    val currentTimetable = dsb.fetchTimetable()
    if (previousTimetable == null) {
        previousTimetable = currentTimetable
        return null
    }
    val changes = currentTimetable.filterNot { previousTimetable!!.contains(it) }
    previousTimetable = currentTimetable
    return changes.ifEmpty { null }
}

suspend fun checkTimetableChanges() {
    while (true) {
        val changes = hasTimetableChanged()
        if (changes != null) {
            val message = changes.joinToString("\n", transform = planToMessage)
            discord.sendMessage("vertretungsplan", "The timetable has changed:\n$message")
            for (entry in changes) {
                val klassen = entry["Klassen"] ?: ""
                discord.sendDmToRoleMembers(klassen, "The timetable has changed:\n$message")
            }
        }
        delay(60000)
    }
}
