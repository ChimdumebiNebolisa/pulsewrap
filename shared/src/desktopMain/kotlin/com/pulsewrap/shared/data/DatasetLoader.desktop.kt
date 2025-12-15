package com.pulsewrap.shared.data

import java.io.IOException

actual object DatasetLoader {
    actual fun loadText(path: String): String {
        return try {
            // Resources are in shared/src/commonMain/resources/
            val resourcePath = if (path.startsWith("/")) path else "/$path"
            val resourceStream = DatasetLoader::class.java.classLoader
                .getResourceAsStream(resourcePath)
                ?: throw IOException("Resource not found: $resourcePath")
            resourceStream.bufferedReader().use { it.readText() }
        } catch (e: IOException) {
            throw RuntimeException("Failed to load resource: $path", e)
        }
    }
}

