package com.pulsewrap.shared.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    onGenerateRecap: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedVariant by remember { mutableStateOf("A") }
    
    ScreenContainer(
        title = "PulseWrap",
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            // Header Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Generate a KPI Recap",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Wrapped-style insights from your KPI JSON — demo or upload.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Data Source Selector
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Try a demo dataset",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    FilterChip(
                        selected = selectedVariant == "A",
                        onClick = { selectedVariant = "A" },
                        label = { Text("Demo A") },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = selectedVariant == "B",
                        onClick = { selectedVariant = "B" },
                        label = { Text("Demo B") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            // Generate Button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onGenerateRecap(selectedVariant) },
                    modifier = Modifier
                        .height(48.dp)
                        .widthIn(min = 200.dp)
                ) {
                    Text("Generate Recap", style = MaterialTheme.typography.titleMedium)
                }
                Text(
                    text = "Takes < 1 second on demo datasets",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

