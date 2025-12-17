package com.pulsewrap.shared.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pulsewrap.shared.data.DatasetLoader
import com.pulsewrap.shared.data.DatasetRepository
import kotlinx.coroutines.launch

enum class InputMode {
    Demo,
    Upload
}

@Composable
fun InputScreen(
    inputMode: InputMode,
    onGenerateDemo: (String) -> Unit,
    onGenerateUpload: (String, String) -> Unit,
    onBack: () -> Unit,
    onUploadKpiFile: (suspend () -> String)? = null,
    onUploadSpendFile: (suspend () -> String)? = null,
    onSwitchToDemo: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var selectedVariant by remember { mutableStateOf("A") }
    var kpiJson by remember { mutableStateOf<String?>(null) }
    var spendJson by remember { mutableStateOf<String?>(null) }
    var kpiParseStatus by remember { mutableStateOf<String?>(null) }
    var spendParseStatus by remember { mutableStateOf<String?>(null) }
    var kpiError by remember { mutableStateOf<String?>(null) }
    var spendError by remember { mutableStateOf<String?>(null) }
    var kpiRecordCount by remember { mutableStateOf<Int?>(null) }
    var spendRecordCount by remember { mutableStateOf<Int?>(null) }
    var showDemoPreview by remember { mutableStateOf(false) }
    var showKpiPreview by remember { mutableStateOf(false) }
    var showSpendPreview by remember { mutableStateOf(false) }
    var shouldAutoOpenPreview by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val repository = remember { DatasetRepository() }
    
    // Auto-open demo preview when switching from Upload to Demo mode
    LaunchedEffect(inputMode, shouldAutoOpenPreview) {
        if (inputMode == InputMode.Demo && shouldAutoOpenPreview) {
            showDemoPreview = true
            shouldAutoOpenPreview = false
        }
    }
    
    ScreenContainer(
        title = if (inputMode == InputMode.Demo) "Try Demo" else "Upload Data",
        onBack = onBack,
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            when (inputMode) {
                InputMode.Demo -> {
                    // Demo Mode
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Pick a sample dataset",
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
                        
                        // Preview demo inputs button
                        TextButton(
                            onClick = { showDemoPreview = true }
                        ) {
                            Text("Preview demo inputs")
                        }
                    }
                }
                InputMode.Upload -> {
                    // Upload Mode
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Upload your KPI JSON",
                            style = MaterialTheme.typography.titleMedium
                        )
                        
                        if (onUploadKpiFile != null && onUploadSpendFile != null) {
                            // KPI File Upload
                            UploadCard(
                                title = "KPI Daily",
                                subtitle = "One record per day (date, revenue, expenses, activeUsers, newUsers).",
                                parseStatus = kpiParseStatus,
                                error = kpiError,
                                hasPreview = kpiJson != null && kpiError == null,
                                onUpload = {
                                    scope.launch {
                                        try {
                                            val content = onUploadKpiFile()
                                            kpiJson = content
                                            kpiError = null
                                            val parseResult = repository.validateKpiJson(content)
                                            parseResult.fold(
                                                onSuccess = { kpiDaily ->
                                                    kpiRecordCount = kpiDaily.size
                                                    kpiParseStatus = "Loaded ${kpiDaily.size} rows"
                                                },
                                                onFailure = { error ->
                                                    kpiError = "Invalid JSON: ${error.message}"
                                                    kpiParseStatus = null
                                                    kpiRecordCount = null
                                                }
                                            )
                                        } catch (e: Exception) {
                                            kpiError = "Failed to read file: ${e.message}"
                                            kpiParseStatus = null
                                            kpiRecordCount = null
                                        }
                                    }
                                },
                                onPreview = {
                                    if (kpiJson != null) {
                                        showKpiPreview = true
                                    }
                                }
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Category Spend File Upload
                            UploadCard(
                                title = "Category Spend",
                                subtitle = "Multiple records per day (date, category, amount).",
                                parseStatus = spendParseStatus,
                                error = spendError,
                                hasPreview = spendJson != null && spendError == null,
                                onUpload = {
                                    scope.launch {
                                        try {
                                            val content = onUploadSpendFile()
                                            spendJson = content
                                            spendError = null
                                            val parseResult = repository.validateSpendJson(content)
                                            parseResult.fold(
                                                onSuccess = { categorySpend ->
                                                    spendRecordCount = categorySpend.size
                                                    spendParseStatus = "Loaded ${categorySpend.size} rows"
                                                },
                                                onFailure = { error ->
                                                    spendError = "Invalid JSON: ${error.message}"
                                                    spendParseStatus = null
                                                    spendRecordCount = null
                                                }
                                            )
                                        } catch (e: Exception) {
                                            spendError = "Failed to read file: ${e.message}"
                                            spendParseStatus = null
                                            spendRecordCount = null
                                        }
                                    }
                                },
                                onPreview = {
                                    if (spendJson != null) {
                                        showSpendPreview = true
                                    }
                                }
                            )
                            
                            // Helper section
                            Spacer(modifier = Modifier.height(8.dp))
                            if (onSwitchToDemo != null) {
                                TextButton(
                                    onClick = {
                                        shouldAutoOpenPreview = true
                                        onSwitchToDemo()
                                    }
                                ) {
                                    Text("Need a template? Use demo format")
                                }
                            }
                        } else {
                            // Platform doesn't support upload
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
                                        text = "File upload not supported on this platform",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            // Generate Recap Button
            val canGenerate = when (inputMode) {
                InputMode.Demo -> true
                InputMode.Upload -> kpiJson != null && spendJson != null && kpiError == null && spendError == null
            }
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.widthIn(min = 200.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            // Reset all state
                            selectedVariant = "A"
                            kpiJson = null
                            spendJson = null
                            kpiParseStatus = null
                            spendParseStatus = null
                            kpiError = null
                            spendError = null
                        },
                        modifier = Modifier
                            .height(48.dp)
                            .weight(1f)
                    ) {
                        Text("Reset", style = MaterialTheme.typography.titleMedium)
                    }
                    Button(
                        onClick = {
                            when (inputMode) {
                                InputMode.Demo -> onGenerateDemo(selectedVariant)
                                InputMode.Upload -> {
                                    if (kpiJson != null && spendJson != null) {
                                        onGenerateUpload(kpiJson!!, spendJson!!)
                                    }
                                }
                            }
                        },
                        enabled = canGenerate,
                        modifier = Modifier
                            .height(48.dp)
                            .weight(1f)
                    ) {
                        Text("Generate Recap", style = MaterialTheme.typography.titleMedium)
                    }
                }
                Text(
                    text = when {
                        inputMode == InputMode.Demo -> "Takes < 1 second on demo datasets"
                        !canGenerate -> "Upload KPI Daily and Category Spend to continue."
                        else -> "Generates insights from your data"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
    
    // Demo preview dialog
    if (showDemoPreview) {
        val kpiPath = if (selectedVariant == "A") "kpi_daily_A.json" else "kpi_daily_B.json"
        val spendPath = if (selectedVariant == "A") "category_spend_A.json" else "category_spend_B.json"
        val kpiJsonText = DatasetLoader.loadText(kpiPath)
        val spendJsonText = DatasetLoader.loadText(spendPath)
        
        // Get record counts
        val kpiCount = repository.validateKpiJson(kpiJsonText).getOrNull()?.size
        val spendCount = repository.validateSpendJson(spendJsonText).getOrNull()?.size
        
        JsonPreviewDialog(
            title = "Preview Demo Inputs (Demo $selectedVariant)",
            tabs = listOf(
                JsonTab(
                    label = "KPI Daily JSON",
                    rawText = kpiJsonText,
                    recordCount = kpiCount
                ),
                JsonTab(
                    label = "Category Spend JSON",
                    rawText = spendJsonText,
                    recordCount = spendCount
                )
            ),
            onDismiss = { showDemoPreview = false }
        )
    }
    
    // KPI preview dialog (upload mode)
    if (showKpiPreview && kpiJson != null) {
        JsonPreviewDialog(
            title = "Preview KPI Daily JSON",
            tabs = listOf(
                JsonTab(
                    label = "KPI Daily JSON",
                    rawText = kpiJson!!,
                    recordCount = kpiRecordCount
                )
            ),
            onDismiss = { showKpiPreview = false }
        )
    }
    
    // Spend preview dialog (upload mode)
    if (showSpendPreview && spendJson != null) {
        JsonPreviewDialog(
            title = "Preview Category Spend JSON",
            tabs = listOf(
                JsonTab(
                    label = "Category Spend JSON",
                    rawText = spendJson!!,
                    recordCount = spendRecordCount
                )
            ),
            onDismiss = { showSpendPreview = false }
        )
    }
}

@Composable
private fun UploadCard(
    title: String,
    subtitle: String,
    parseStatus: String?,
    error: String?,
    hasPreview: Boolean,
    onUpload: () -> Unit,
    onPreview: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when {
                error != null -> MaterialTheme.colorScheme.errorContainer
                parseStatus != null -> MaterialTheme.colorScheme.primaryContainer
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onUpload) {
                Text("Choose file")
            }
            if (parseStatus != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "✅",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = parseStatus,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            if (error != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "❌",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
            if (hasPreview) {
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(
                    onClick = onPreview,
                    modifier = Modifier.padding(start = 0.dp)
                ) {
                    Text("View preview")
                }
            }
        }
    }
}

