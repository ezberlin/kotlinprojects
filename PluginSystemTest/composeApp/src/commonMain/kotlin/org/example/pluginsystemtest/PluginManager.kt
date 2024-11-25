package org.example.pluginsystemtest

import androidx.compose.runtime.mutableStateListOf
import java.io.File
import java.net.URLClassLoader
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

class PluginManager {
    private val loadedPlugins = mutableStateListOf<ScreenPlugin>()
    private val pluginClassLoaders = mutableListOf<URLClassLoader>()

    fun loadPluginsFromDirectory(pluginDirectory: File) {
        // Clear previous plugins
        loadedPlugins.clear()
        pluginClassLoaders.forEach { it.close() }
        pluginClassLoaders.clear()

        // Ensure directory exists
        if (!pluginDirectory.exists() || !pluginDirectory.isDirectory) {
            println("Plugin directory does not exist: ${pluginDirectory.absolutePath}")
            return
        }

        // Find all JAR files in the directory
        val jarFiles = pluginDirectory.listFiles { file ->
            file.isFile && file.extension.lowercase() == "jar"
        } ?: emptyArray()

        jarFiles.forEach { jarFile ->
            try {
                // Create a URL class loader for each JAR
                val classLoader = URLClassLoader(
                    arrayOf(jarFile.toURI().toURL()),
                    PluginManager::class.java.classLoader
                )
                pluginClassLoaders.add(classLoader)

                // Use Kotlin reflection to find and instantiate plugins
                val pluginClasses = findPluginClasses(classLoader)

                pluginClasses.forEach { pluginClass ->
                    try {
                        val plugin = pluginClass.createInstance() as? ScreenPlugin
                        plugin?.let { loadedPlugins.add(it) }
                    } catch (e: Exception) {
                        println("Error instantiating plugin from ${jarFile.name}: ${e.message}")
                    }
                }
            } catch (e: Exception) {
                println("Error loading plugin JAR ${jarFile.name}: ${e.message}")
            }
        }

        println("Loaded ${loadedPlugins.size} plugins from ${jarFiles.size} JAR files")
    }

    private fun findPluginClasses(classLoader: ClassLoader): List<KClass<*>> {
        return classLoader.getResources("META-INF/services/com.example.pluginsystem.ScreenPlugin")
            .toList()
            .flatMap { url ->
                url.openStream().bufferedReader().use { reader ->
                    reader.readLines()
                        .filter { it.isNotBlank() }
                        .mapNotNull { className ->
                            try {
                                val clazz = classLoader.loadClass(className.trim())
                                clazz.kotlin
                            } catch (e: Exception) {
                                println("Could not load plugin class $className: ${e.message}")
                                null
                            }
                        }
                        .filter { ScreenPlugin::class.java.isAssignableFrom(it.java) }
                }
            }
    }

    fun getPlugins(): List<ScreenPlugin> = loadedPlugins.toList()
}