package com.pulsewrap.shared.data

import com.pulsewrap.shared.model.CategorySpend
import com.pulsewrap.shared.model.KpiDaily
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString

class DatasetRepository {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }
    
    fun loadDataset(variant: String): Result<Pair<List<KpiDaily>, List<CategorySpend>>> {
        return try {
            val kpiPath = if (variant == "A") {
                "kpi_daily_A.json"
            } else {
                "kpi_daily_B.json"
            }
            
            val spendPath = if (variant == "A") {
                "category_spend_A.json"
            } else {
                "category_spend_B.json"
            }
            
            val kpiJson = DatasetLoader.loadText(kpiPath)
            val spendJson = DatasetLoader.loadText(spendPath)
            
            val kpiDaily = json.decodeFromString<List<KpiDaily>>(kpiJson)
            val categorySpend = json.decodeFromString<List<CategorySpend>>(spendJson)
            
            if (kpiDaily.isEmpty()) {
                Result.failure(IllegalStateException("KPI dataset is empty"))
            } else {
                Result.success(Pair(kpiDaily, categorySpend))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

