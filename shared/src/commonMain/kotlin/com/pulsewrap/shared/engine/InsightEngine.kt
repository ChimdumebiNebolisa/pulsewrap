package com.pulsewrap.shared.engine

import com.pulsewrap.shared.model.CategorySpend
import com.pulsewrap.shared.model.InsightCard
import com.pulsewrap.shared.model.KpiDaily
import com.pulsewrap.shared.util.Formatters
import kotlinx.datetime.LocalDate

object InsightEngine {
    fun computeInsights(
        daily: List<KpiDaily>,
        spend: List<CategorySpend>
    ): List<InsightCard> {
        if (daily.isEmpty()) {
            return emptyList()
        }
        
        val insights = mutableListOf<InsightCard>()
        
        // Parse dates and filter valid records
        val validDaily = daily.mapNotNull { record ->
            try {
                LocalDate.parse(record.date) to record
            } catch (e: Exception) {
                null
            }
        }
        
        if (validDaily.isEmpty()) {
            return listOf(InsightCard(
                title = "Error",
                primaryValue = "No valid data",
                supportingDetail = "Could not parse any valid dates from the dataset"
            ))
        }
        
        val dailyRecords = validDaily.map { it.second }
        
        // 1. Total Revenue
        val totalRevenue = dailyRecords.sumOf { it.revenue }
        insights.add(InsightCard(
            title = "Total Revenue",
            primaryValue = Formatters.formatCurrency(totalRevenue),
            supportingDetail = "Across ${dailyRecords.size} days"
        ))
        
        // 2. Total Expenses
        val totalExpenses = dailyRecords.sumOf { it.expenses }
        insights.add(InsightCard(
            title = "Total Expenses",
            primaryValue = Formatters.formatCurrency(totalExpenses),
            supportingDetail = "Across ${dailyRecords.size} days"
        ))
        
        // 3. Net Profit
        val netProfit = totalRevenue - totalExpenses
        insights.add(InsightCard(
            title = "Net Profit",
            primaryValue = Formatters.formatCurrency(netProfit),
            supportingDetail = if (netProfit >= 0) "Profitable period" else "Loss period"
        ))
        
        // 4. Best Revenue Day
        val bestRevenueDay = dailyRecords.maxByOrNull { it.revenue }
        bestRevenueDay?.let {
            insights.add(InsightCard(
                title = "Best Revenue Day",
                primaryValue = Formatters.formatCurrency(it.revenue),
                supportingDetail = try {
                    "On ${Formatters.formatDate(LocalDate.parse(it.date))}"
                } catch (e: Exception) {
                    "On ${it.date}"
                }
            ))
        }
        
        // 5. Highest Expenses Day
        val highestExpenseDay = dailyRecords.maxByOrNull { it.expenses }
        highestExpenseDay?.let {
            insights.add(InsightCard(
                title = "Highest Expenses Day",
                primaryValue = Formatters.formatCurrency(it.expenses),
                supportingDetail = try {
                    "On ${Formatters.formatDate(LocalDate.parse(it.date))}"
                } catch (e: Exception) {
                    "On ${it.date}"
                }
            ))
        }
        
        // 6. Average Daily Active Users
        val avgActiveUsers = dailyRecords.map { it.activeUsers }.average()
        insights.add(InsightCard(
            title = "Average Daily Active Users",
            primaryValue = Formatters.formatNumber(avgActiveUsers.toInt()),
            supportingDetail = "Across all days"
        ))
        
        // 7. Peak New Users Day
        val peakNewUsersDay = dailyRecords.maxByOrNull { it.newUsers }
        peakNewUsersDay?.let {
            insights.add(InsightCard(
                title = "Peak New Users Day",
                primaryValue = "${it.newUsers} users",
                supportingDetail = try {
                    "On ${Formatters.formatDate(LocalDate.parse(it.date))}"
                } catch (e: Exception) {
                    "On ${it.date}"
                }
            ))
        }
        
        // 8. Biggest Revenue Spike (day-over-day)
        if (dailyRecords.size >= 2) {
            val sortedByDate = dailyRecords.sortedBy { it.date }
            var maxSpike = Double.NEGATIVE_INFINITY
            var spikeDay: KpiDaily? = null
            
            for (i in 1 until sortedByDate.size) {
                val spike = sortedByDate[i].revenue - sortedByDate[i - 1].revenue
                if (spike > maxSpike) {
                    maxSpike = spike
                    spikeDay = sortedByDate[i]
                }
            }
            
            spikeDay?.let {
                insights.add(InsightCard(
                    title = "Biggest Revenue Spike",
                    primaryValue = Formatters.formatCurrency(maxSpike),
                    supportingDetail = try {
                        "On ${Formatters.formatDate(LocalDate.parse(it.date))}"
                    } catch (e: Exception) {
                        "On ${it.date}"
                    }
                ))
            }
        }
        
        // 9. Burn Rate (average daily expenses)
        val burnRate = totalExpenses / dailyRecords.size
        insights.add(InsightCard(
            title = "Burn Rate",
            primaryValue = Formatters.formatCurrency(burnRate),
            supportingDetail = "Average daily expenses"
        ))
        
        // 10. Runway (if cashBalance exists)
        val lastRecord = dailyRecords.lastOrNull()
        lastRecord?.cashBalance?.let { cashBalance ->
            if (burnRate > 0) {
                val runwayDays = (cashBalance / burnRate).toInt()
                insights.add(InsightCard(
                    title = "Runway",
                    primaryValue = "$runwayDays days",
                    supportingDetail = "Based on current cash balance and burn rate"
                ))
            }
        }
        
        // 11. Top Spending Category
        val categoryTotals = spend.groupBy { it.category }
            .mapValues { (_, records) -> records.sumOf { it.amount } }
        
        val topCategory = categoryTotals.maxByOrNull { it.value }
        topCategory?.let {
            insights.add(InsightCard(
                title = "Top Spending Category",
                primaryValue = it.key,
                supportingDetail = "Total: ${Formatters.formatCurrency(it.value)}"
            ))
        }
        
        return insights
    }
}

