package com.pulsewrap.shared.model

import kotlinx.datetime.Instant

data class RecapData(
    val datasetName: String,
    val insights: List<InsightCard>,
    val markdown: String,
    val generatedAt: Instant
)

