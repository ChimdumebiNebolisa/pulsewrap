package com.pulsewrap.shared.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.pulsewrap.shared.util.prettyPrintJson

data class JsonTab(
    val label: String,
    val rawText: String,
    val recordCount: Int?
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JsonPreviewDialog(
    title: String,
    tabs: List<JsonTab>,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (tabs.isEmpty()) return
    
    var selectedTabIndex by remember { mutableStateOf(0) }
    val selectedTab = tabs[selectedTabIndex]
    val prettyJson = remember(selectedTab.rawText) {
        prettyPrintJson(selectedTab.rawText)
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = title)
        },
        text = {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .widthIn(max = 800.dp)
            ) {
                // Tab Row
                if (tabs.size > 1) {
                    TabRow(selectedTabIndex = selectedTabIndex) {
                        tabs.forEachIndexed { index, tab ->
                            Tab(
                                selected = selectedTabIndex == index,
                                onClick = { selectedTabIndex = index },
                                text = { Text(tab.label) }
                            )
                        }
                    }
                } else {
                    // Single tab - just show label
                    Text(
                        text = tabs[0].label,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Record count summary
                if (selectedTab.recordCount != null) {
                    Text(
                        text = "${selectedTab.recordCount} record${if (selectedTab.recordCount != 1) "s" else ""}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                
                // JSON content
                OutlinedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp)
                ) {
                    SelectionContainer {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                                .padding(16.dp)
                        ) {
                            Text(
                                text = prettyJson,
                                style = MaterialTheme.typography.bodyMedium,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

