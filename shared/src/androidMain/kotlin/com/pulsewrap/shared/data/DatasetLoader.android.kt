package com.pulsewrap.shared.data

import android.content.Context
import androidx.compose.ui.platform.LocalContext
import java.io.IOException

actual object DatasetLoader {
    private var context: Context? = null
    
    fun initialize(context: Context) {
        DatasetLoader.context = context.applicationContext
    }
    
    actual fun loadText(path: String): String {
        val ctx = context ?: throw IllegalStateException("DatasetLoader not initialized. Call initialize() first.")
        return try {
            ctx.assets.open(path).bufferedReader().use { it.readText() }
        } catch (e: IOException) {
            throw RuntimeException("Failed to load asset: $path", e)
        }
    }
}

