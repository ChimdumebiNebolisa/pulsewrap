package com.pulsewrap.shared.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

enum class InsightCardTier {
    Tier1, // Primary - most prominent
    Tier2, // Secondary
    Tier3  // Detail
}

@Composable
fun InsightCard(
    title: String,
    primaryValue: String,
    supportingDetail: String,
    tier: InsightCardTier = InsightCardTier.Tier3,
    modifier: Modifier = Modifier
) {
    val cardColors = when (tier) {
        InsightCardTier.Tier1 -> CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
        InsightCardTier.Tier2 -> CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
        InsightCardTier.Tier3 -> CardDefaults.cardColors()
    }
    
    val titleColor = when (tier) {
        InsightCardTier.Tier1 -> MaterialTheme.colorScheme.onPrimaryContainer
        InsightCardTier.Tier2 -> MaterialTheme.colorScheme.onSurfaceVariant
        InsightCardTier.Tier3 -> MaterialTheme.colorScheme.onSurface
    }
    
    val elevation = when (tier) {
        InsightCardTier.Tier1 -> 4.dp
        InsightCardTier.Tier2 -> 2.dp
        InsightCardTier.Tier3 -> 1.dp
    }
    
    val cornerRadius = when (tier) {
        InsightCardTier.Tier1 -> 24.dp
        InsightCardTier.Tier2 -> 20.dp
        InsightCardTier.Tier3 -> 16.dp
    }
    
    val padding = when (tier) {
        InsightCardTier.Tier1 -> 24.dp
        InsightCardTier.Tier2 -> 20.dp
        InsightCardTier.Tier3 -> 20.dp
    }
    
    val metricStyle = when (tier) {
        InsightCardTier.Tier1 -> MaterialTheme.typography.displayMedium
        InsightCardTier.Tier2 -> MaterialTheme.typography.headlineLarge
        InsightCardTier.Tier3 -> MaterialTheme.typography.headlineSmall
    }
    
    val metricColor = when (tier) {
        InsightCardTier.Tier1 -> MaterialTheme.colorScheme.primary
        InsightCardTier.Tier2 -> MaterialTheme.colorScheme.onSurface
        InsightCardTier.Tier3 -> MaterialTheme.colorScheme.onSurface
    }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = cardColors,
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        shape = RoundedCornerShape(cornerRadius)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = titleColor
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = primaryValue,
                style = metricStyle,
                fontWeight = if (tier == InsightCardTier.Tier1) FontWeight.Bold else FontWeight.Medium,
                color = metricColor
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = supportingDetail,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
