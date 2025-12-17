package com.pulsewrap.shared.engine

import com.pulsewrap.shared.model.InsightCard
import com.pulsewrap.shared.model.InsightType
import com.pulsewrap.shared.model.ReportMeta
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertTrue

class MarkdownGeneratorTest {
    @Test
    fun testMarkdownFormat() {
        val insights = listOf(
            InsightCard("Total Revenue", "$3,700.00", "Across 3 days", InsightType.TOTAL_REVENUE),
            InsightCard("Net Profit", "$1,630.00", "Profitable period", InsightType.NET_PROFIT)
        )
        val meta = ReportMeta("Demo A", LocalDate(2025, 11, 3))
        
        val markdown = MarkdownGenerator.toMarkdown(insights, meta)
        
        assertTrue(markdown.contains("# PulseWrap KPI Recap"))
        assertTrue(markdown.contains("**Dataset:** Demo A"))
        assertTrue(markdown.contains("## Total Revenue"))
        assertTrue(markdown.contains("$3,700.00"))
        assertTrue(markdown.contains("## Net Profit"))
    }
}

