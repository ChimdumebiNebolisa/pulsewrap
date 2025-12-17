package com.pulsewrap.shared.model

data class InsightCard(
    val title: String,
    val primaryValue: String,
    val supportingDetail: String,
    val type: InsightType,
    val contextDate: String? = null,
    val contextCategory: String? = null,
    val contextDelta: Double? = null
)

