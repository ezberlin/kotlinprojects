package org.example.pluginsystemtest

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import java.io.File

fun main() {
    val pluginManager = PluginManager()

    // Specify the directory where plugin JARs will be loaded from
    val pluginDirectory = File(System.getProperty("user.home"), "ComposePlugins")

    // Ensure plugin directory exists
    pluginDirectory.mkdirs()

    // Initial plugin load
    pluginManager.loadPluginsFromDirectory(pluginDirectory)

    application {
        Window(onCloseRequest = ::exitApplication, title = "Compose Plugin System") {
            MainPluginScreen(pluginManager)
        }
    }
}