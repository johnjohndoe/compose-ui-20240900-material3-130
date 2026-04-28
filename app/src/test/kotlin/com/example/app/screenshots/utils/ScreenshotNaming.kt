package com.example.app.screenshots.utils

import java.util.Locale

fun getScreenshotName(): String {
    val stackTrace = Thread.currentThread().stackTrace
    val testMethodName = stackTrace.firstOrNull {
        (it.className.contains("ComposableTests") || it.className.contains("ScreenTests")) && it.methodName.endsWith("test")
    }?.methodName ?: error("Could not find test method name in stack trace")

    return testMethodName.toScreenshotName()
}

fun String.toScreenshotName(): String {
    return this.replace("`", "")
        .split(" ")
        .filter { it.lowercase() != "test" }
        .joinToString("") { it.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() } }
        .replace(Regex("[^A-Za-z0-9]"), "")
}
