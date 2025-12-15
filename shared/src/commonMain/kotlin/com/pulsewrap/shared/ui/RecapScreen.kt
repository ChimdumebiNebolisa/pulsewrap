package com.pulsewrap.shared.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pulsewrap.shared.model.InsightCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecapScreen(
    insights: List<InsightCard>,
    markdown: String,
    onBack: () -> Unit,
    onViewMarkdown: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("KPI Recap") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("←")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onViewMarkdown
            ) {
                Text("View Markdown")
            }
        }
    ) { paddingValues ->
        if (insights.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text(
                    text = "No insights available",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(insights) { insight ->
                    InsightCard(
                        title = insight.title,
                        primaryValue = insight.primaryValue,
                        supportingDetail = insight.supportingDetail
                    )
                }
            }
        }
    }
}

