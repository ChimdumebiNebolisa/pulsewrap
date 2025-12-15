package com.pulsewrap.shared.model

import kotlinx.serialization.Serializable

@Serializable
data class KpiDaily(
    val date: String,
    val revenue: Double,
    val expenses: Double,
    val activeUsers: Int,
    val newUsers: Int,
    val cashBalance: Double? = null
)

