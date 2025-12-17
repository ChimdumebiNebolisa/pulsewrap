package com.pulsewrap.web

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.CanvasBasedWindow
import com.pulsewrap.shared.data.DatasetRepository
import com.pulsewrap.shared.engine.InsightEngine
import com.pulsewrap.shared.engine.MarkdownGenerator
import com.pulsewrap.shared.model.RecapData
import com.pulsewrap.shared.ui.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    CanvasBasedWindow(canvasElementId = "ComposeTarget") {
        com.pulsewrap.shared.ui.PulseWrapTheme {
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
    val repository = remember { DatasetRepository() }
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Landing) }
    var inputMode by remember { mutableStateOf(InputMode.Demo) }
    var recapData by remember { mutableStateOf<RecapData?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    fun generateRecap(variant: String) {
        isLoading = true
        errorMessage = null
        
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
                        
                        recapData = RecapData(
                            datasetName = "Demo $variant",
                            insights = insights,
                            markdown = markdown,
                            generatedAt = Clock.System.now()
                        )
                        isLoading = false
                        currentScreen = Screen.Recap
                    },
                    onFailure = { error ->
                        errorMessage = error.message ?: "Failed to load dataset"
                        isLoading = false
                        currentScreen = Screen.Recap
                    }
                )
            } catch (e: Exception) {
                errorMessage = e.message ?: "An error occurred"
                isLoading = false
                currentScreen = Screen.Recap
            }
        }
    }
    
    fun generateRecapFromUpload(kpiJson: String, spendJson: String) {
        isLoading = true
        errorMessage = null
        
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val result = repository.parseJsonDataset(kpiJson, spendJson)
                result.fold(
                    onSuccess = { (daily, spend) ->
                        val insights = InsightEngine.computeInsights(daily, spend)
                        val meta = com.pulsewrap.shared.model.ReportMeta(
                            datasetName = "Uploaded Dataset",
                            generationDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
                        )
                        val markdown = MarkdownGenerator.toMarkdown(insights, meta)
                        
                        recapData = RecapData(
                            datasetName = "Uploaded Dataset",
                            insights = insights,
                            markdown = markdown,
                            generatedAt = Clock.System.now()
                        )
                        isLoading = false
                        currentScreen = Screen.Recap
                    },
                    onFailure = { error ->
                        errorMessage = error.message ?: "Failed to parse uploaded dataset"
                        isLoading = false
                        currentScreen = Screen.Recap
                    }
                )
            } catch (e: Exception) {
                errorMessage = e.message ?: "An error occurred"
                isLoading = false
                currentScreen = Screen.Recap
            }
        }
    }
    
    fun exportMarkdown(markdown: String) {
        // Web export - download file using JS interop
        downloadFile(markdown, "PulseWrap_Report.md", "text/markdown")
    }
    
    fun copyToClipboard(text: String) {
        // Web copy to clipboard using JS interop
        copyTextToClipboard(text)
    }
    
    when (val screen = currentScreen) {
        is Screen.Landing -> {
            LandingScreen(
                onTryDemo = {
                    inputMode = InputMode.Demo
                    currentScreen = Screen.Input
                },
                onUploadData = {
                    inputMode = InputMode.Upload
                    currentScreen = Screen.Input
                }
            )
        }
        is Screen.Input -> {
            InputScreen(
                inputMode = inputMode,
                onGenerateDemo = { variant ->
                    generateRecap(variant)
                },
                onGenerateUpload = { kpiJson, spendJson ->
                    generateRecapFromUpload(kpiJson, spendJson)
                },
                onBack = {
                    currentScreen = Screen.Landing
                },
                onUploadKpiFile = {
                    pickFile(".json")
                },
                onUploadSpendFile = {
                    pickFile(".json")
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
                    onBack = { currentScreen = Screen.Input }
                )
            } else {
                RecapScreen(
                    recapData = recapData,
                    onBack = { currentScreen = Screen.Input },
                    onViewMarkdown = {
                        currentScreen = Screen.Report
                    },
                    onTryAnother = {
                        inputMode = InputMode.Demo
                        currentScreen = Screen.Input
                    }
                )
            }
        }
        is Screen.Report -> {
            val markdown = recapData?.markdown ?: ""
            MarkdownPreviewScreen(
                markdown = markdown,
                datasetName = recapData?.datasetName,
                onBack = {
                    currentScreen = Screen.Recap
                },
                onShare = {},
                onExport = {
                    exportMarkdown(markdown)
                },
                onCopy = {
                    copyToClipboard(markdown)
                },
                isAndroid = false
            )
        }
        // Keep deprecated screens for backward compatibility (suppress warnings in IDE)
        is Screen.Home -> {
            // Fallback to Landing
            currentScreen = Screen.Landing
        }
        is Screen.MarkdownPreview -> {
            // Redirect to Report
            currentScreen = Screen.Report
        }
    }
}

@JsExport
@JsName("downloadFile")
external fun downloadFile(content: String, filename: String, mimeType: String)

// Copy to clipboard using browser Clipboard API - simple implementation
// For now, just use the clipboard API directly via external function
@JsName("copyToClipboard")
external fun copyTextToClipboard(text: String)

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
