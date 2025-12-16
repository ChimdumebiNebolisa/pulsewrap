package com.pulsewrap.shared.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
    modifier: Modifier = Modifier
) {
    var selectedVariant by remember { mutableStateOf("A") }
    var kpiJson by remember { mutableStateOf<String?>(null) }
    var spendJson by remember { mutableStateOf<String?>(null) }
    var kpiParseStatus by remember { mutableStateOf<String?>(null) }
    var spendParseStatus by remember { mutableStateOf<String?>(null) }
    var kpiError by remember { mutableStateOf<String?>(null) }
    var spendError by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val repository = remember { DatasetRepository() }
    
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
                                title = "KPI Daily JSON",
                                parseStatus = kpiParseStatus,
                                error = kpiError,
                                onUpload = {
                                    scope.launch {
                                        try {
                                            val content = onUploadKpiFile()
                                            kpiJson = content
                                            kpiError = null
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
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Category Spend File Upload
                            UploadCard(
                                title = "Category Spend JSON",
                                parseStatus = spendParseStatus,
                                error = spendError,
                                onUpload = {
                                    scope.launch {
                                        try {
                                            val content = onUploadSpendFile()
                                            spendJson = content
                                            spendError = null
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
                            )
                            
                            // Helper text
                            Text(
                                text = "Use sample format",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(top = 8.dp)
                            )
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
                        .widthIn(min = 200.dp)
                ) {
                    Text("Generate Recap", style = MaterialTheme.typography.titleMedium)
                }
                Text(
                    text = if (inputMode == InputMode.Demo) "Takes < 1 second on demo datasets" else "Generates insights from your data",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun UploadCard(
    title: String,
    parseStatus: String?,
    error: String?,
    onUpload: () -> Unit,
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
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onUpload) {
                Text("Choose File")
            }
            if (parseStatus != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "✅ $parseStatus",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            if (error != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "❌ $error",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}

