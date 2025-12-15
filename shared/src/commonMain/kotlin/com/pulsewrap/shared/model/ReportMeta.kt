package com.pulsewrap.shared.model

import kotlinx.datetime.LocalDate

data class ReportMeta(
    val datasetName: String,
    val generationDate: LocalDate
)

