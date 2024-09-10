package com.yarvol.skyvibe

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform