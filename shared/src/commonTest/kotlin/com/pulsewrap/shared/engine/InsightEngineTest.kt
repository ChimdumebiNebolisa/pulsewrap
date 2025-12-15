package com.pulsewrap.shared.engine

import com.pulsewrap.shared.model.CategorySpend
import com.pulsewrap.shared.model.KpiDaily
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class InsightEngineTest {
    @Test
    fun testTotalRevenue() {
        val daily = listOf(
            KpiDaily("2025-11-01", 1200.0, 700.0, 95, 10, 8000.0),
            KpiDaily("2025-11-02", 900.0, 650.0, 102, 14, 8250.0)
        )
        val spend = emptyList<CategorySpend>()
        
        val insights = InsightEngine.computeInsights(daily, spend)
        
        val totalRevenueInsight = insights.find { it.title == "Total Revenue" }
        assertTrue(totalRevenueInsight != null)
        assertTrue(totalRevenueInsight!!.primaryValue.contains("2,100"))
    }
    
    @Test
    fun testNetProfit() {
        val daily = listOf(
            KpiDaily("2025-11-01", 1200.0, 700.0, 95, 10, 8000.0),
            KpiDaily("2025-11-02", 900.0, 650.0, 102, 14, 8250.0)
        )
        val spend = emptyList<CategorySpend>()
        
        val insights = InsightEngine.computeInsights(daily, spend)
        
        val netProfitInsight = insights.find { it.title == "Net Profit" }
        assertTrue(netProfitInsight != null)
        // 2100 - 1350 = 750
        assertTrue(netProfitInsight!!.primaryValue.contains("750"))
    }
    
    @Test
    fun testBiggestRevenueSpike() {
        val daily = listOf(
            KpiDaily("2025-11-01", 1200.0, 700.0, 95, 10, 8000.0),
            KpiDaily("2025-11-02", 900.0, 650.0, 102, 14, 8250.0),
            KpiDaily("2025-11-03", 1600.0, 720.0, 120, 18, 9130.0)
        )
        val spend = emptyList<CategorySpend>()
        
        val insights = InsightEngine.computeInsights(daily, spend)
        
        val spikeInsight = insights.find { it.title == "Biggest Revenue Spike" }
        assertTrue(spikeInsight != null)
        // Spike from 900 to 1600 = 700
        assertTrue(spikeInsight!!.primaryValue.contains("700"))
    }
    
    @Test
    fun testTopSpendingCategory() {
        val daily = listOf(
            KpiDaily("2025-11-01", 1200.0, 700.0, 95, 10, 8000.0)
        )
        val spend = listOf(
            CategorySpend("2025-11-01", "Ads", 120.0),
            CategorySpend("2025-11-01", "Cloud", 90.0),
            CategorySpend("2025-11-02", "Ads", 140.0)
        )
        
        val insights = InsightEngine.computeInsights(daily, spend)
        
        val topCategoryInsight = insights.find { it.title == "Top Spending Category" }
        assertTrue(topCategoryInsight != null)
        assertEquals("Ads", topCategoryInsight!!.primaryValue)
        // Total Ads: 120 + 140 = 260
        assertTrue(topCategoryInsight.supportingDetail.contains("260"))
    }
    
    @Test
    fun testRunwayOmittedWhenCashBalanceAbsent() {
        val daily = listOf(
            KpiDaily("2025-11-01", 1200.0, 700.0, 95, 10, null)
        )
        val spend = emptyList<CategorySpend>()
        
        val insights = InsightEngine.computeInsights(daily, spend)
        
        val runwayInsight = insights.find { it.title == "Runway" }
        assertTrue(runwayInsight == null, "Runway should be omitted when cashBalance is null")
    }
    
    @Test
    fun testEmptyDataset() {
        val daily = emptyList<KpiDaily>()
        val spend = emptyList<CategorySpend>()
        
        val insights = InsightEngine.computeInsights(daily, spend)
        
        assertTrue(insights.isEmpty())
    }
}

