package com.pulsewrap.shared.util

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

/**
 * Pretty prints JSON text by parsing and re-encoding with pretty printing.
 * Falls back to original text if parsing fails.
 */
fun prettyPrintJson(text: String): String {
    return try {
        val json = Json {
            ignoreUnknownKeys = true
            isLenient = true
            prettyPrint = true
        }
        // Parse as JsonElement and re-encode with pretty printing
        val element = json.parseToJsonElement(text)
        json.encodeToString(JsonElement.serializer(), element)
    } catch (e: Exception) {
        // Fallback to original text if parsing fails
        text
    }
}

