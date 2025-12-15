package com.pulsewrap.shared.util

import kotlinx.datetime.LocalDate

actual object Formatters {
    actual fun formatCurrency(amount: Double): String {
        // Simple currency formatting for Wasm
        val formatted = "%.2f".format(amount)
        return "$${formatted.replace(Regex("(\\d)(?=(\\d{3})+(?!\\d))"), "$1,")}"
    }
    
    actual fun formatDate(date: LocalDate): String {
        return try {
            val monthNames = listOf(
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
            )
            val monthName = monthNames[date.monthNumber - 1]
            "$monthName ${date.dayOfMonth}, ${date.year}"
        } catch (e: Exception) {
            date.toString()
        }
    }
    
    actual fun formatNumber(value: Int): String {
        return value.toString().reversed().chunked(3).joinToString(",").reversed()
    }
    
    actual fun formatNumber(value: Double): String {
        val intValue = value.toInt()
        return formatNumber(intValue)
    }
}

