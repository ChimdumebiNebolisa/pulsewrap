package com.pulsewrap.shared.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pulsewrap.shared.engine.CaptionGenerator
import com.pulsewrap.shared.engine.NarrativeGenerator
import com.pulsewrap.shared.model.RecapData
import com.pulsewrap.shared.util.DateRangeUtils
import kotlinx.datetime.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecapScreen(
    recapData: RecapData?,
    onBack: () -> Unit,
    onViewMarkdown: () -> Unit,
    onTryAnother: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    ScreenContainer(
        title = "KPI Recap",
        onBack = onBack,
        actions = {
            if (recapData != null) {
                TextButton(onClick = onViewMarkdown) {
                    Text("Export Report")
                }
            }
        },
        modifier = modifier
    ) { paddingValues ->
        if (recapData == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No recap available",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Go back and generate a recap first.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onBack) {
                            Text("Go Back")
                        }
                    }
                }
            }
        } else if (recapData.insights.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No insights generated",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "The dataset did not produce any insights. Please check your data and try again.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        } else {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                // Breadcrumb
                Text(
                    text = "Landing → Input → Recap",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                // Extract date range from insights - look for dates in "On ..." details
                // Dates are formatted as "On November 1, 2025" or "On 2025-11-01"
                val dateStrings = recapData.insights.mapNotNull { insight ->
                    val detail = insight.supportingDetail
                    when {
                        detail.startsWith("On ") -> detail.removePrefix("On ").takeIf { it.isNotBlank() }
                        else -> null
                    }
                }.distinct()
                
                // Try to format date range, fall back to dataset name if we can't parse dates
                val dateRange = if (dateStrings.isNotEmpty()) {
                    DateRangeUtils.formatDateRange(dateStrings) ?: recapData.datasetName
                } else {
                    recapData.datasetName
                }
                val displayDateRange = dateRange
                
                // Hero Summary Card
                HeroSummaryCard(
                    recapData = recapData,
                    dateRange = displayDateRange,
                    onTryAnother = onTryAnother
                )
                
                // Grouped Insight Sections
                val sections = categorizeInsights(recapData.insights)
                
                sections.forEach { section ->
                    InsightSectionView(
                        section = section,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun HeroSummaryCard(
    recapData: RecapData,
    dateRange: String,
    onTryAnother: () -> Unit
) {
    val insights = recapData.insights
    val totalRevenue = insights.find { it.title == "Total Revenue" }?.primaryValue ?: "$0"
    val totalExpenses = insights.find { it.title == "Total Expenses" }?.primaryValue ?: "$0"
    val netProfit = insights.find { it.title == "Net Profit" }?.primaryValue ?: "$0"
    val netProfitValue = insights.find { it.title == "Net Profit" }
    val isProfitable = netProfitValue?.let { 
        !it.primaryValue.startsWith("-") && it.primaryValue != "$0.00"
    } ?: false
    
    val narrativeText = NarrativeGenerator.generateNarrative(recapData)
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = MaterialTheme.shapes.extraLarge,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Your KPI Recap",
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = "${recapData.datasetName} • $dateRange",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Stat Pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatPill(
                    label = "Total Revenue",
                    value = totalRevenue,
                    modifier = Modifier.weight(1f)
                )
                StatPill(
                    label = "Total Expenses",
                    value = totalExpenses,
                    modifier = Modifier.weight(1f)
                )
                StatPill(
                    label = "Net Profit",
                    value = netProfit,
                    isHighlighted = true,
                    isPositive = isProfitable,
                    modifier = Modifier.weight(1f)
                )
            }
            
            // Try Another Dataset button
            OutlinedButton(
                onClick = onTryAnother,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Try Another Dataset")
            }
            
            // Narrative text
            Text(
                text = narrativeText,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun StatPill(
    label: String,
    value: String,
    isHighlighted: Boolean = false,
    isPositive: Boolean = true,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = if (isHighlighted) {
            if (isPositive) {
                MaterialTheme.colorScheme.tertiaryContainer
            } else {
                MaterialTheme.colorScheme.errorContainer
            }
        } else {
            MaterialTheme.colorScheme.surface
        }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = if (isHighlighted) {
                    MaterialTheme.typography.headlineMedium
                } else {
                    MaterialTheme.typography.titleLarge
                },
                fontWeight = FontWeight.Bold,
                color = if (isHighlighted) {
                    if (isPositive) {
                        MaterialTheme.colorScheme.onTertiaryContainer
                    } else {
                        MaterialTheme.colorScheme.onErrorContainer
                    }
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
        }
    }
}

@Composable
private fun InsightSectionView(
    section: InsightSection,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = section.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold
        )
        
        // For wide screens, show 2 columns. For narrow, single column.
        // Using Row with two Columns for wasm stability
        val wideScreen = true // In a real app, you'd use LocalConfiguration or similar
        if (wideScreen && section.insights.size > 1) {
            val midpoint = (section.insights.size + 1) / 2
            val leftInsights = section.insights.take(midpoint)
            val rightInsights = section.insights.drop(midpoint)
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    leftInsights.forEach { insight ->
                        InsightCard(
                            title = insight.title,
                            primaryValue = insight.primaryValue,
                            supportingDetail = CaptionGenerator.humanCaption(insight),
                            tier = section.tier,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rightInsights.forEach { insight ->
                        InsightCard(
                            title = insight.title,
                            primaryValue = insight.primaryValue,
                            supportingDetail = CaptionGenerator.humanCaption(insight),
                            tier = section.tier,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                section.insights.forEach { insight ->
                    InsightCard(
                        title = insight.title,
                        primaryValue = insight.primaryValue,
                        supportingDetail = CaptionGenerator.humanCaption(insight),
                        tier = section.tier,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

private fun humanizeSupportingDetail(detail: String): String {
    return when {
        detail.startsWith("Across ") && detail.contains(" days") -> {
            val days = detail.removePrefix("Across ").removeSuffix(" days")
            when (days.toIntOrNull()) {
                1 -> "Single day recap"
                2, 3 -> "Short window recap"
                in 4..7 -> "Week-long period"
                else -> "Extended period"
            }
        }
        detail == "Profitable period" -> "Profitable period"
        detail == "Loss period" -> "Loss period"
        detail.startsWith("On ") -> detail // Keep date details as is
        detail.startsWith("Average daily") -> "Average daily expenses"
        detail.startsWith("Based on") -> detail
        detail.startsWith("Total: ") -> detail
        detail == "Across all days" -> "Across all days"
        else -> detail
    }
}
