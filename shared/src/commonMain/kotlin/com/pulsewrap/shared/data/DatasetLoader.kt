package com.pulsewrap.shared.data

expect object DatasetLoader {
    fun loadText(path: String): String
}

