package com.paoapps.kombutime

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform