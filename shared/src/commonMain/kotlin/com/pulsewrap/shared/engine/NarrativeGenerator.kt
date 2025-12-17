package com.pulsewrap.shared.engine

import com.pulsewrap.shared.model.InsightType
import com.pulsewrap.shared.model.RecapData
import com.pulsewrap.shared.util.Formatters

object NarrativeGenerator {
    fun generateNarrative(recapData: RecapData): String {
        val insights = recapData.insights
        
        // Find key insights by type
        val netProfit = insights.find { it.type == InsightType.NET_PROFIT }
        val bestRevenueDay = insights.find { it.type == InsightType.BEST_REVENUE_DAY }
        val biggestSpike = insights.find { it.type == InsightType.BIGGEST_REVENUE_SPIKE }
        val topCategory = insights.find { it.type == InsightType.TOP_SPEND_CATEGORY }
        val peakNewUsers = insights.find { it.type == InsightType.PEAK_NEW_USERS_DAY }
        
        val narrativeParts = mutableListOf<String>()
        
        // Sentence 1: Profitability summary
        netProfit?.let { profit ->
            val profitValue = profit.primaryValue
            val isPositive = !profitValue.startsWith("-") && profitValue != "$0.00"
            
            if (isPositive) {
                narrativeParts.add("You were profitable overall ($profitValue)")
            } else {
                val lossAmount = if (profitValue.startsWith("-")) {
                    profitValue // Already has minus sign
                } else {
                    "-$profitValue"
                }
                narrativeParts.add("You ran at a loss ($lossAmount)")
            }
        } ?: run {
            narrativeParts.add("Here's your KPI recap")
        }
        
        // Sentence 2: Pick most interesting highlight
        val highlight = when {
            // Prioritize biggest spike if significant (more than $1000)
            biggestSpike != null && biggestSpike.contextDelta != null && 
            biggestSpike.contextDelta!! > 1000.0 -> {
                val date = biggestSpike.contextDate ?: "a notable day"
                "Your biggest revenue jump came on $date"
            }
            // Then best revenue day
            bestRevenueDay != null -> {
                val date = bestRevenueDay.contextDate ?: "a notable day"
                "Revenue peaked on $date"
            }
            // Then top spend category
            topCategory != null -> {
                val category = topCategory.contextCategory ?: "categories"
                "Spending was concentrated in $category"
            }
            // Then peak new users
            peakNewUsers != null -> {
                val date = peakNewUsers.contextDate ?: "a notable day"
                "New user signups peaked on $date"
            }
            else -> null
        }
        
        highlight?.let {
            narrativeParts.add(it)
        }
        
        return if (narrativeParts.isNotEmpty()) {
            narrativeParts.joinToString(". ") + "."
        } else {
            "Summary of your KPI performance."
        }
    }
}

