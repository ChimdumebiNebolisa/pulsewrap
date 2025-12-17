package com.pulsewrap.shared.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarkdownPreviewScreen(
    markdown: String,
    datasetName: String? = null,
    onBack: () -> Unit,
    onShare: () -> Unit,
    onExport: () -> Unit,
    onCopy: (() -> Unit)? = null,
    isAndroid: Boolean,
    modifier: Modifier = Modifier
) {
    ScreenContainer(
        title = "Report",
        onBack = onBack,
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Summary header
            if (datasetName != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = datasetName,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "This report matches your recap cards above.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            // Preview card
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f, fill = false),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                SelectionContainer {
                    Text(
                        text = markdown,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState()),
                        style = MaterialTheme.typography.bodyMedium,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
            
            // Actions row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (isAndroid) {
                    Button(
                        onClick = onShare,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Share")
                    }
                } else {
                    OutlinedButton(
                        onClick = { onCopy?.invoke() },
                        enabled = onCopy != null,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Copy")
                    }
                    Button(
                        onClick = onExport,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Download .md")
                    }
                }
            }
        }
    }
}

