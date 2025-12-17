package com.pulsewrap.shared.ui

import com.pulsewrap.shared.model.InsightCard
import com.pulsewrap.shared.util.Formatters
import kotlinx.datetime.LocalDate

data class InsightSection(
    val title: String,
    val insights: List<InsightCard>,
    val tier: InsightCardTier
)

fun categorizeInsights(insights: List<InsightCard>): List<InsightSection> {
    val sections = mutableListOf<InsightSection>()
    
    // Categorize each insight
    val moneyInsights = mutableListOf<InsightCard>()
    val userInsights = mutableListOf<InsightCard>()
    val costInsights = mutableListOf<InsightCard>()
    val otherInsights = mutableListOf<InsightCard>()
    
    insights.forEach { insight ->
        when {
            insight.title.contains("Revenue", ignoreCase = true) ||
            insight.title.contains("Expenses", ignoreCase = true) && insight.title.contains("Total", ignoreCase = true) ||
            insight.title.contains("Profit", ignoreCase = true) ||
            insight.title.contains("Burn Rate", ignoreCase = true) ||
            insight.title.contains("Runway", ignoreCase = true) ||
            insight.title.contains("Spike", ignoreCase = true) -> {
                moneyInsights.add(insight)
            }
            insight.title.contains("Users", ignoreCase = true) ||
            (insight.title.contains("Peak", ignoreCase = true) && insight.title.contains("Users", ignoreCase = true)) -> {
                userInsights.add(insight)
            }
            insight.title.contains("Category", ignoreCase = true) ||
            (insight.title.contains("Expense", ignoreCase = true) && !insight.title.contains("Total", ignoreCase = true)) -> {
                costInsights.add(insight)
            }
            else -> {
                otherInsights.add(insight)
            }
        }
    }
    
    // Money section - separate Tier 1 from Tier 2
    val tier1Money = moneyInsights.filter { 
        it.title == "Net Profit" || it.title == "Best Revenue Day" 
    }
    val tier2Money = moneyInsights.filter { 
        it.title != "Net Profit" && it.title != "Best Revenue Day" 
    }
    
    if (tier1Money.isNotEmpty()) {
        sections.add(InsightSection("Money", tier1Money, InsightCardTier.Tier1))
    }
    if (tier2Money.isNotEmpty()) {
        sections.add(InsightSection("Money", tier2Money, InsightCardTier.Tier2))
    }
    
    // Users section - Tier 2
    if (userInsights.isNotEmpty()) {
        sections.add(InsightSection("Users", userInsights, InsightCardTier.Tier2))
    }
    
    // Costs & Spend section - Tier 2
    if (costInsights.isNotEmpty()) {
        sections.add(InsightSection("Costs & Spend", costInsights, InsightCardTier.Tier2))
    }
    
    // Highlights section - Tier 3 (everything else)
    if (otherInsights.isNotEmpty()) {
        sections.add(InsightSection("Highlights", otherInsights, InsightCardTier.Tier3))
    }
    
    return sections
}

fun generateNarrativeText(insights: List<InsightCard>, dateRange: String?): String {
    val netProfitInsight = insights.find { it.title == "Net Profit" }
    val bestRevenueDay = insights.find { it.title == "Best Revenue Day" }
    
    val narrativeParts = mutableListOf<String>()
    
    netProfitInsight?.let {
        val profitValue = it.primaryValue
        val isPositive = !profitValue.startsWith("-") && profitValue != "$0.00"
        if (isPositive) {
            narrativeParts.add("You were profitable overall")
        } else {
            narrativeParts.add("You operated at a loss during this period")
        }
    }
    
    bestRevenueDay?.let {
        val dateDetail = it.supportingDetail
        if (dateDetail.startsWith("On ")) {
            val date = dateDetail.removePrefix("On ")
            narrativeParts.add("with the strongest revenue on $date")
        }
    }
    
    return if (narrativeParts.isNotEmpty()) {
        narrativeParts.joinToString(", ") + "."
    } else {
        "Summary of your KPI performance."
    }
}

fun formatDateRangeFromInsights(insights: List<InsightCard>): String? {
    // Extract dates from supporting details
    val dates = insights.mapNotNull { insight ->
        val detail = insight.supportingDetail
        if (detail.startsWith("On ")) {
            detail.removePrefix("On ").takeIf { it.isNotBlank() }
        } else null
    }.distinct()
    
    return if (dates.isNotEmpty()) {
        // Try to parse and format the date range
        // This is a simplified version - in a real implementation, you'd parse the dates
        dates.firstOrNull()
    } else {
        null
    }
}

