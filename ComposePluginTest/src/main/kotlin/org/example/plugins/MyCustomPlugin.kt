package org.example.plugins

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import org.example.pluginsystemtest.ScreenPlugin

class MyCustomPlugin : ScreenPlugin {
    override val id = "custom_plugin"
    override val name = "Custom Plugin"

    @Composable
    override fun Content() {
        Column {
            Text("This is a dynamically loaded plugin")
            Button(onClick = { println("Plugin button clicked!") }) {
                Text("Plugin Action")
            }
        }
    }
}