plugins {
    kotlin("jvm") version "2.0.21"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.wikidata.wdtk:wdtk-dumpfiles:0.16.0")
    implementation("org.wikidata.wdtk:wdtk-wikibaseapi:0.16.0")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}