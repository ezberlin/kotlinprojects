package io.github.ezberlin.spacetutorial

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform