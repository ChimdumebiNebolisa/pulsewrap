package com.pulsewrap.web

import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Picks a file and returns its content as a String.
 * Uses JS interop to create a file input element and read the file.
 */
suspend fun pickFile(accept: String = ".json"): String = suspendCancellableCoroutine { continuation ->
    val input = document.createElement("input") as HTMLInputElement
    input.type = "file"
    input.accept = accept
    input.style.display = "none"
    
    input.onchange = {
        val file = input.files?.get(0)
        if (file != null) {
            val reader = FileReader()
            reader.onload = {
                val content = reader.result
                document.body?.removeChild(input)
                continuation.resume(content)
            }
            reader.onerror = {
                document.body?.removeChild(input)
                continuation.resumeWithException(Exception("Failed to read file"))
            }
            reader.readAsText(file)
        } else {
            document.body?.removeChild(input)
            continuation.cancel()
        }
    }
    
    input.oncancel = {
        document.body?.removeChild(input)
        continuation.cancel()
    }
    
    document.body?.appendChild(input)
    input.click()
}

@JsName("document")
external val document: Document

@JsName("FileReader")
external class FileReader {
    var onload: (() -> Unit)?
    var onerror: (() -> Unit)?
    val result: String
    fun readAsText(file: File)
}

external interface Document {
    fun createElement(tagName: String): HTMLElement
    val body: HTMLElement?
}

external interface HTMLElement {
    fun appendChild(child: HTMLElement)
    fun removeChild(child: HTMLElement)
}

external interface HTMLInputElement : HTMLElement {
    var type: String
    var accept: String
    var style: Style
    var files: FileList?
    var onchange: (() -> Unit)?
    var oncancel: (() -> Unit)?
    fun click()
}

external interface Style {
    var display: String
}

external interface FileList {
    fun get(index: Int): File?
}

external interface File {
    val name: String
    val size: Long
}

