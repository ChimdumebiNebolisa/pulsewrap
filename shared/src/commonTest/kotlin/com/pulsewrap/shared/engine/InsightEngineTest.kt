package com.pulsewrap.shared.engine

import com.pulsewrap.shared.model.CategorySpend
import com.pulsewrap.shared.model.InsightType
import com.pulsewrap.shared.model.KpiDaily
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
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
        assertNotNull(totalRevenueInsight)
        assertEquals(InsightType.TOTAL_REVENUE, totalRevenueInsight!!.type)
        assertTrue(totalRevenueInsight.primaryValue.contains("2,100"))
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
        assertNotNull(netProfitInsight)
        assertEquals(InsightType.NET_PROFIT, netProfitInsight!!.type)
        // 2100 - 1350 = 750
        assertTrue(netProfitInsight.primaryValue.contains("750"))
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
        assertNotNull(spikeInsight)
        assertEquals(InsightType.BIGGEST_REVENUE_SPIKE, spikeInsight!!.type)
        assertNotNull(spikeInsight.contextDate, "Spike insight should have contextDate")
        assertNotNull(spikeInsight.contextDelta, "Spike insight should have contextDelta")
        assertEquals(700.0, spikeInsight.contextDelta, "Spike delta should be 700")
        // Spike from 900 to 1600 = 700
        assertTrue(spikeInsight.primaryValue.contains("700"))
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
        assertNotNull(topCategoryInsight)
        assertEquals(InsightType.TOP_SPEND_CATEGORY, topCategoryInsight!!.type)
        assertEquals("Ads", topCategoryInsight.primaryValue)
        assertEquals("Ads", topCategoryInsight.contextCategory, "Top category insight should have contextCategory")
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

