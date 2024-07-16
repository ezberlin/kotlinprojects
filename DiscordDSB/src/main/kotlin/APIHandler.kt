package org.example

import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.jsoup.Jsoup
import java.io.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

class APIHandler(username: String, password: String, private val tablemapper: Array<String> = arrayOf("type","class","lesson","subject","room","new_subject","new_teacher","teacher")) {
    private val currentTime = LocalDateTime.now()
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private val formattedTime = currentTime.format(formatter)
    private val appVersion = "2.5.9"
    private val language = "de"
    private val osVersion = "28 8.0"
    private val appId = UUID.randomUUID().toString()
    private val device = "SM-G930F"
    private val bundleId = "de.heinekingmedia.dsbmobile"
    private val dataURL = "https://app.dsbcontrol.de/JsonHandler.ashx/GetData"
    private var tableURL : String? = null

    private var classIndex: Int? = null
    init {
        var i = 0
        while (i < this.tablemapper.size) {
            if (this.tablemapper[i] == "class") {
                classIndex = i
                break
            }
            i++
        }
    }

    private val params = mapOf(
        "UserId"     to username,
        "UserPw"     to password,
        "AppVersion" to appVersion,
        "Language"   to language,
        "OsVersion"  to osVersion,
        "AppId"      to appId,
        "Device"     to device,
        "BundleId"   to bundleId,
        "Date"       to formattedTime,
        "LastUpdate" to formattedTime
    )

    private fun gzipCompress(input: ByteArray): ByteArray {
        val outputStream = ByteArrayOutputStream()
        GZIPOutputStream(outputStream).use { it.write(input) }
        return outputStream.toByteArray()
    }

    private fun extractUrlFromJson(json: JsonObject): String? {
        try {
            val resultMenuItems = json.getAsJsonArray("ResultMenuItems")
            val firstItem = resultMenuItems.get(0).asJsonObject
            val childs = firstItem.getAsJsonArray("Childs")
            val root = childs.get(1).asJsonObject.getAsJsonObject("Root")
            val childChilds = root.getAsJsonArray("Childs").get(0).asJsonObject.getAsJsonArray("Childs")
            val detail = childChilds.get(0).asJsonObject.get("Detail")
            return detail.asString
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    private fun fetchData() {
        val client = OkHttpClient()

        val paramsBytes = Gson().toJson(params).toByteArray(Charsets.UTF_8)
        val paramsCompressed = Base64.getEncoder().encodeToString(gzipCompress(paramsBytes))!!

        val jsonData = JsonObject().apply {
            addProperty("Data", paramsCompressed)
            addProperty("DataType", 1)
        }

        val requestBody = JsonObject().apply {
            add("req", jsonData)
        }

        val request = Request.Builder()
            .url(dataURL)
            .post(requestBody.toString().toRequestBody("application/json".toMediaTypeOrNull()))
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            val timetableData = response.body?.string()
            val dataCompressed = Gson().fromJson(timetableData, JsonObject::class.java)["d"].asString
            val dataByteArray = Base64.getDecoder().decode(dataCompressed.toByteArray(Charsets.UTF_8))
            val dataDecompressed = ByteArrayOutputStream().use { output ->
                ByteArrayInputStream(dataByteArray).use { input ->
                    GZIPInputStream(input).use { gzipInput ->
                        gzipInput.copyTo(output)
                    }
                }
                output.toByteArray()
            }
            val data = Gson().fromJson(String(dataDecompressed), JsonObject::class.java)
            tableURL = extractUrlFromJson(data)
            println(tableURL)
        }
    }

    fun fetchTimetable(): List<MutableMap<String, String>> {
        fetchData()
        val results = mutableListOf<MutableMap<String, String>>()

        val client = OkHttpClient()
        val request: Request? = tableURL?.let {
            Request.Builder()
                .url(it)
                .build()
        }
        val call: Call? = request?.let { client.newCall(it) }
        val sauce = call?.execute()?.body?.string()
        val soupi = sauce?.let { Jsoup.parse(it) }
        var ind = -1
        if (soupi != null) {
            for (soup in soupi.select("table.mon_list")) {
                ind++
                val updates = soupi.select("table.mon_head")
                    .map { it.select("p span").last()?.nextSibling().toString().split("Stand: ")[1] }[ind]
                val titles = soupi.select("div.mon_title").map { it.text() }[ind]
                val date = titles.split(" ")[0]
                val day = titles.split(" ")[1].split(", ")[0].replace(",", "")
                val entries = soup.select("tr").drop(1)
                for (entry in entries) {
                    val infos = entry.select("td")
                    if (infos.size < 2) continue
                    val classArray = if (classIndex != null) {
                        infos[classIndex!!].text().split(", ")
                    } else {
                        listOf("---")
                    }
                    for (schoolclass in classArray) {
                        val newEntry = mutableMapOf<String, String>()
                        newEntry["date"] = date
                        newEntry["day"] = day
                        newEntry["updated"] = updates
                        var i = 0
                        while (i < infos.size) {
                            val attribute = if (i < tablemapper.size) tablemapper[i] else "col$i"
                            newEntry[attribute] = if (infos[i].text() != "\u00a0") {
                                if (attribute == "class") schoolclass else infos[i].text()
                            } else {
                                "---"
                            }
                            i++
                        }
                        results.add(newEntry)
                    }
                }
            }
        }
        return results
    }


}
