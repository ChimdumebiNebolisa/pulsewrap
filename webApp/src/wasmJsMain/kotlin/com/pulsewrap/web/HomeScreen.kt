package com.pulsewrap.web

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import com.pulsewrap.web.pickFile
import com.pulsewrap.shared.data.DatasetRepository

enum class DataSourceMode {
    Demo,
    Upload
}

@Composable
fun HomeScreen(
    onGenerateRecap: (String) -> Unit,
    onGenerateRecapFromUpload: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedVariant by remember { mutableStateOf("A") }
    var dataSourceMode by remember { mutableStateOf(DataSourceMode.Demo) }
    var kpiJson by remember { mutableStateOf<String?>(null) }
    var spendJson by remember { mutableStateOf<String?>(null) }
    var kpiParseStatus by remember { mutableStateOf<String?>(null) }
    var spendParseStatus by remember { mutableStateOf<String?>(null) }
    var kpiError by remember { mutableStateOf<String?>(null) }
    var spendError by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val repository = remember { DatasetRepository() }
    
    com.pulsewrap.shared.ui.ScreenContainer(
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
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Text(
                    text = "Wrapped-style insights from your KPI JSON — demo or upload.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Data Source Selector
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    FilterChip(
                        selected = dataSourceMode == DataSourceMode.Demo,
                        onClick = { 
                            dataSourceMode = DataSourceMode.Demo
                            kpiJson = null
                            spendJson = null
                            kpiParseStatus = null
                            spendParseStatus = null
                            kpiError = null
                            spendError = null
                        },
                        label = { Text("Demo") },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = dataSourceMode == DataSourceMode.Upload,
                        onClick = { dataSourceMode = DataSourceMode.Upload },
                        label = { Text("Upload (Beta)") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
        
            if (dataSourceMode == DataSourceMode.Demo) {
                // Demo Mode
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
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
            } else {
                // Upload Mode
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Upload your own JSON (beta)",
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    // KPI File Upload
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (kpiError != null) {
                        MaterialTheme.colorScheme.errorContainer
                    } else if (kpiParseStatus != null) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    }
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "KPI Daily JSON",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            scope.launch {
                                try {
                                    val content = pickFile(".json")
                                    kpiJson = content
                                    kpiError = null
                                    // Try to parse to validate
                                    val parseResult = repository.validateKpiJson(content)
                                    parseResult.fold(
                                        onSuccess = { kpiDaily ->
                                            kpiParseStatus = "Loaded ${kpiDaily.size} KPI rows"
                                        },
                                        onFailure = { error ->
                                            kpiError = "Invalid JSON: ${error.message}"
                                            kpiParseStatus = null
                                        }
                                    )
                                } catch (e: Exception) {
                                    kpiError = "Failed to read file: ${e.message}"
                                    kpiParseStatus = null
                                }
                            }
                        }
                    ) {
                        Text("Choose KPI File")
                    }
                    if (kpiParseStatus != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "✅ ${kpiParseStatus!!}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    if (kpiError != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "❌ ${kpiError!!}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
                    // Category Spend File Upload
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (spendError != null) {
                                MaterialTheme.colorScheme.errorContainer
                            } else if (spendParseStatus != null) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            }
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Category Spend JSON",
                                style = MaterialTheme.typography.titleSmall
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    scope.launch {
                                        try {
                                            val content = pickFile(".json")
                                            spendJson = content
                                            spendError = null
                                            // Try to parse to validate
                                            val parseResult = repository.validateSpendJson(content)
                                            parseResult.fold(
                                                onSuccess = { categorySpend ->
                                                    spendParseStatus = "Loaded ${categorySpend.size} category spend rows"
                                                },
                                                onFailure = { error ->
                                                    spendError = "Invalid JSON: ${error.message}"
                                                    spendParseStatus = null
                                                }
                                            )
                                        } catch (e: Exception) {
                                            spendError = "Failed to read file: ${e.message}"
                                            spendParseStatus = null
                                        }
                                    }
                                }
                            ) {
                                Text("Choose Category Spend File")
                            }
                            if (spendParseStatus != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "✅ ${spendParseStatus!!}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            if (spendError != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "❌ ${spendError!!}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }
                }
            }
            
            // Generate Recap Button
            val canGenerate = if (dataSourceMode == DataSourceMode.Demo) {
                true
            } else {
                kpiJson != null && spendJson != null && kpiError == null && spendError == null
            }
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        if (dataSourceMode == DataSourceMode.Demo) {
                            onGenerateRecap(selectedVariant)
                        } else {
                            if (kpiJson != null && spendJson != null) {
                                onGenerateRecapFromUpload(kpiJson!!, spendJson!!)
                            }
                        }
                    },
                    enabled = canGenerate,
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

