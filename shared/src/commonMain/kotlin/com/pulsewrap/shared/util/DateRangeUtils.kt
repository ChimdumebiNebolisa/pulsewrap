package com.pulsewrap.shared.util

import kotlinx.datetime.LocalDate

object DateRangeUtils {
    fun formatDateRange(dates: List<String>): String? {
        if (dates.isEmpty()) return null
        
        val parsedDates = dates.mapNotNull { dateStr ->
            // Try to parse as ISO date first
            try {
                LocalDate.parse(dateStr.takeIf { it.matches(Regex("\\d{4}-\\d{2}-\\d{2}")) } ?: return@mapNotNull null)
            } catch (e: Exception) {
                // Try to parse formatted dates like "November 1, 2025"
                try {
                    parseFormattedDate(dateStr)
                } catch (e2: Exception) {
                    null
                }
            }
        }
        
        if (parsedDates.isEmpty()) return null
        
        val sortedDates = parsedDates.sorted()
        val firstDate = sortedDates.first()
        val lastDate = sortedDates.last()
        
        val firstMonth = firstDate.month.name.lowercase().replaceFirstChar { it.uppercase() }
        val lastMonth = lastDate.month.name.lowercase().replaceFirstChar { it.uppercase() }
        
        return if (firstDate == lastDate) {
            "$firstMonth ${firstDate.dayOfMonth}, ${firstDate.year}"
        } else if (firstDate.year == lastDate.year && firstDate.month == lastDate.month) {
            "$firstMonth ${firstDate.dayOfMonth}–${lastDate.dayOfMonth}, ${firstDate.year}"
        } else if (firstDate.year == lastDate.year) {
            "$firstMonth ${firstDate.dayOfMonth}–$lastMonth ${lastDate.dayOfMonth}, ${firstDate.year}"
        } else {
            "$firstMonth ${firstDate.dayOfMonth}, ${firstDate.year}–$lastMonth ${lastDate.dayOfMonth}, ${lastDate.year}"
        }
    }
    
    private fun parseFormattedDate(dateStr: String): LocalDate? {
        // Try to parse "Month Day, Year" format
        val months = listOf(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        )
        
        for (i in months.indices) {
            if (dateStr.contains(months[i])) {
                val parts = dateStr.split(" ")
                if (parts.size >= 3) {
                    val day = parts[1].removeSuffix(",").toIntOrNull() ?: return null
                    val year = parts[2].toIntOrNull() ?: return null
                    return LocalDate(year, i + 1, day)
                }
            }
        }
        return null
    }
}

