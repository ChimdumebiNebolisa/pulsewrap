package com.pulsewrap.shared.engine

import com.pulsewrap.shared.model.InsightCard
import com.pulsewrap.shared.model.InsightType

object CaptionGenerator {
    fun humanCaption(insight: InsightCard): String {
        return when (insight.type) {
            InsightType.NET_PROFIT -> "Bottom line after expenses"
            
            InsightType.BEST_REVENUE_DAY -> {
                val date = insight.contextDate
                if (date != null) {
                    "Your strongest sales day: $date"
                } else {
                    "Your strongest sales day"
                }
            }
            
            InsightType.BIGGEST_REVENUE_SPIKE -> {
                val date = insight.contextDate
                if (date != null) {
                    "Largest day-over-day jump: $date"
                } else {
                    "Largest day-over-day jump"
                }
            }
            
            InsightType.BURN_RATE -> "Average daily spend"
            
            InsightType.TOP_SPEND_CATEGORY -> {
                val category = insight.contextCategory
                if (category != null) {
                    "Where most money went: $category"
                } else {
                    "Where most money went"
                }
            }
            
            InsightType.HIGHEST_EXPENSE_DAY -> {
                val date = insight.contextDate
                if (date != null) {
                    "Highest spend day: $date"
                } else {
                    "Highest spend day"
                }
            }
            
            InsightType.TOTAL_REVENUE -> "Total across period"
            
            InsightType.TOTAL_EXPENSES -> "Total across period"
            
            InsightType.AVG_ACTIVE_USERS -> "Daily average"
            
            InsightType.PEAK_NEW_USERS_DAY -> {
                val date = insight.contextDate
                if (date != null) {
                    "Record signups: $date"
                } else {
                    "Record signups"
                }
            }
            
            InsightType.RUNWAY_DAYS -> "Months remaining at current burn"
        }
    }
}

