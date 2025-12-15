package com.pulsewrap.desktop

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.pulsewrap.shared.data.DatasetRepository
import com.pulsewrap.shared.engine.InsightEngine
import com.pulsewrap.shared.engine.MarkdownGenerator
import com.pulsewrap.shared.ui.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "PulseWrap"
    ) {
        MaterialTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                AppContent()
            }
        }
    }
}

@Composable
private fun AppContent() {
    val repository = DatasetRepository()
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }
    var currentInsights by remember { mutableStateOf<List<com.pulsewrap.shared.model.InsightCard>>(emptyList()) }
    var currentMarkdown by remember { mutableStateOf("") }
    var currentDatasetVariant by remember { mutableStateOf("A") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var exportSuccessMessage by remember { mutableStateOf<String?>(null) }
    
    fun generateRecap(variant: String) {
        isLoading = true
        errorMessage = null
        currentDatasetVariant = variant
        
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val result = repository.loadDataset(variant)
                result.fold(
                    onSuccess = { (daily, spend) ->
                        val insights = InsightEngine.computeInsights(daily, spend)
                        val meta = com.pulsewrap.shared.model.ReportMeta(
                            datasetName = "Demo $variant",
                            generationDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
                        )
                        val markdown = MarkdownGenerator.toMarkdown(insights, meta)
                        
                        currentInsights = insights
                        currentMarkdown = markdown
                        isLoading = false
                        currentScreen = Screen.Recap(variant)
                    },
                    onFailure = { error ->
                        errorMessage = error.message ?: "Failed to load dataset"
                        isLoading = false
                        currentScreen = Screen.Recap(variant)
                    }
                )
            } catch (e: Exception) {
                errorMessage = e.message ?: "An error occurred"
                isLoading = false
                currentScreen = Screen.Recap(variant)
            }
        }
    }
    
    fun exportMarkdown(markdown: String) {
        try {
            val dateFormat = SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault())
            val timestamp = dateFormat.format(Date())
            val filename = "PulseWrap_Report_$timestamp.md"
            val homeDir = System.getProperty("user.home")
            val file = File(homeDir, filename)
            
            file.writeText(markdown)
            exportSuccessMessage = "Exported to: ${file.absolutePath}"
        } catch (e: Exception) {
            exportSuccessMessage = "Export failed: ${e.message}"
        }
    }
    
    when (val screen = currentScreen) {
        is Screen.Home -> {
            HomeScreen(
                onGenerateRecap = { variant ->
                    generateRecap(variant)
                }
            )
        }
        is Screen.Recap -> {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (errorMessage != null) {
                ErrorScreen(
                    message = errorMessage!!,
                    onBack = { currentScreen = Screen.Home }
                )
            } else {
                RecapScreen(
                    insights = currentInsights,
                    markdown = currentMarkdown,
                    onBack = { currentScreen = Screen.Home },
                    onViewMarkdown = {
                        currentScreen = Screen.MarkdownPreview(
                            markdown = currentMarkdown,
                            datasetVariant = currentDatasetVariant
                        )
                    }
                )
            }
        }
        is Screen.MarkdownPreview -> {
            if (exportSuccessMessage != null) {
                AlertDialog(
                    onDismissRequest = { exportSuccessMessage = null },
                    title = { Text("Export") },
                    text = { Text(exportSuccessMessage!!) },
                    confirmButton = {
                        TextButton(onClick = { exportSuccessMessage = null }) {
                            Text("OK")
                        }
                    }
                )
            }
            
            MarkdownPreviewScreen(
                markdown = screen.markdown,
                onBack = {
                    currentScreen = Screen.Recap(screen.datasetVariant)
                },
                onShare = {},
                onExport = {
                    exportMarkdown(screen.markdown)
                },
                isAndroid = false
            )
        }
    }
}

@Composable
private fun ErrorScreen(message: String, onBack: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Error",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onBack) {
                Text("Go Back")
            }
        }
    }
}

