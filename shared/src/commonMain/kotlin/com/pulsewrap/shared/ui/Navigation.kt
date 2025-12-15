package com.pulsewrap.shared.ui

sealed class Screen {
    object Home : Screen()
    data class Recap(val datasetVariant: String) : Screen()
    data class MarkdownPreview(val markdown: String, val datasetVariant: String) : Screen()
}

