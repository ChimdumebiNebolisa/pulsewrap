package com.pulsewrap.shared.ui

sealed class Screen {
    object Landing : Screen()
    object Input : Screen()
    object Recap : Screen()
    object Report : Screen()
    @Deprecated("Use Landing and Input instead", ReplaceWith("Screen.Landing"))
    object Home : Screen()
    @Deprecated("Use Report instead", ReplaceWith("Screen.Report"))
    object MarkdownPreview : Screen()
}

