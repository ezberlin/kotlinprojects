package org.example.pluginsystemtest

import androidx.compose.runtime.Composable

interface ScreenPlugin {
    val id: String
    val name: String
    @Composable
    fun Content()
}