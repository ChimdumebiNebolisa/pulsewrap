package com.pulsewrap.shared.engine

import com.pulsewrap.shared.model.InsightCard
import com.pulsewrap.shared.model.ReportMeta
import com.pulsewrap.shared.util.Formatters

object MarkdownGenerator {
    fun toMarkdown(insights: List<InsightCard>, meta: ReportMeta): String {
        val sb = StringBuilder()
        
        sb.appendLine("# PulseWrap KPI Recap")
        sb.appendLine()
        sb.appendLine("**Dataset:** ${meta.datasetName}")
        sb.appendLine("**Generated:** ${Formatters.formatDate(meta.generationDate)}")
        sb.appendLine()
        
        insights.forEach { insight ->
            sb.appendLine("## ${insight.title}")
            sb.appendLine(insight.primaryValue)
            sb.appendLine(insight.supportingDetail)
            sb.appendLine()
        }
        
        return sb.toString()
    }
}

