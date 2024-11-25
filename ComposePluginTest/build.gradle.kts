plugins {
    kotlin("jvm") version "2.0.21"  // Use same Kotlin version as main project
    id("org.jetbrains.kotlin.plugin.compose") version "1.7.1"  // Add compose plugin
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
}

dependencies {
    // Main project dependency
    implementation(files("/Users/ismail/source/kotlinprojects/PluginSystemTest/composeApp/build/libs/composeApp-desktop.jar"))

    // Compose Desktop dependencies
    implementation(compose.desktop.currentOs)
    implementation("org.jetbrains.compose.runtime:runtime:1.7.1")
    implementation("org.jetbrains.compose.foundation:foundation:1.7.1")
    implementation("org.jetbrains.compose.material:material:1.7.1")
    implementation("org.jetbrains.compose.ui:ui:1.7.1")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}