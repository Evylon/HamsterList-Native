package de.evylon.shoppinglist

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform