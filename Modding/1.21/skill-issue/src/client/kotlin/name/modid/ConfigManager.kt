package name.modid

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

object ConfigManager {
    private const val CONFIGPATH = "siconfig.json"
    private val gson = Gson()

    fun saveConfig(issues: Array<Issue>) {
        val issueStates = issues.associate { it.name to it.enabled }
        val json = gson.toJson(issueStates)
        Files.write(Paths.get(CONFIGPATH), json.toByteArray())
    }

    fun loadConfig(issues: Array<Issue>) {
        val file = File(CONFIGPATH)
        if (!file.exists()) return

        val type = object : TypeToken<Map<String, Boolean>>() {}.type
        val issueStates: Map<String, Boolean> = gson.fromJson(file.readText(), type)

        issues.forEach { issue ->
            issue.enabled = issueStates[issue.name] ?: issue.enabled
        }
    }
}