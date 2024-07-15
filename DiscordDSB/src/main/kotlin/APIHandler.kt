package org.example

import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

class APIHandler(username: String, password: String) {
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

    private val params = mapOf(
        "UserId" to username,
        "UserPw" to password,
        "AppVersion" to appVersion,
        "Language" to language,
        "OsVersion" to osVersion,
        "AppId" to appId,
        "Device" to device,
        "BundleId" to bundleId,
        "Date" to formattedTime,
        "LastUpdate" to formattedTime
    )

    private fun gzipCompress(input: ByteArray): ByteArray {
        val outputStream = ByteArrayOutputStream()
        GZIPOutputStream(outputStream).use { it.write(input) }
        return outputStream.toByteArray()
    }

    fun fetchData() {
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

            val gson = Gson()
            val jsonString = gson.toJson(data)
            val fileName = "data_${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))}.json"
            val file = File(fileName)
            FileOutputStream(file).use { outputStream ->
                outputStream.write(jsonString.toByteArray(Charsets.UTF_8))
            }
        }
    }
}
