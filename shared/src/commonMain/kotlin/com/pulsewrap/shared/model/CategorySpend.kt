package com.pulsewrap.shared.model

import kotlinx.serialization.Serializable

@Serializable
data class CategorySpend(
    val date: String,
    val category: String,
    val amount: Double
)

