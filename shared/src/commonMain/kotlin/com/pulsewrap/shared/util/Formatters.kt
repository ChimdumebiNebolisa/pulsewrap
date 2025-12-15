package com.pulsewrap.shared.util

import kotlinx.datetime.LocalDate

expect object Formatters {
    fun formatCurrency(amount: Double): String
    fun formatDate(date: LocalDate): String
    fun formatNumber(value: Int): String
    fun formatNumber(value: Double): String
}
