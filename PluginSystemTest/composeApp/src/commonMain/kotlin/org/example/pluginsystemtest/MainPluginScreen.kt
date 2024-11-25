package org.example.pluginsystemtest

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MainPluginScreen(pluginManager: PluginManager) {
    var selectedPlugin by remember { mutableStateOf<ScreenPlugin?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Plugin Selection Buttons
        LazyRow {
            pluginManager.getPlugins().forEach { plugin ->
                item {
                    Button(
                        onClick = { selectedPlugin = plugin },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(plugin.name)
                    }
                }
            }
        }

        // Render Selected Plugin
        selectedPlugin?.let { plugin ->
            PluginContent(plugin)
        }
    }
}

@Composable
fun PluginContent(plugin: ScreenPlugin) {
    plugin.Content()
}