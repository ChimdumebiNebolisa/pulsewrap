package com.pulsewrap.shared.util

import kotlinx.datetime.LocalDate

actual object Formatters {
    private val currencyFormatter = java.text.NumberFormat.getCurrencyInstance(java.util.Locale.US)
    private val numberFormatter = java.text.NumberFormat.getNumberInstance(java.util.Locale.US)
    
    actual fun formatCurrency(amount: Double): String {
        return currencyFormatter.format(amount)
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
        return numberFormatter.format(value)
    }
    
    actual fun formatNumber(value: Double): String {
        return numberFormatter.format(value)
    }
}

