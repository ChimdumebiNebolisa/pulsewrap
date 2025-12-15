buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.compose.multiplatform) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.kotlin.serialization) apply false
}

tasks.register("quickCheck") {
    group = "verification"
    description = "Quick build checks for shared, desktop, and web modules (no Android SDK required)"
    dependsOn(
        ":shared:desktopTest",
        ":desktopApp:build",
        ":webApp:build"
    )
}

tasks.register("androidCheck") {
    group = "verification"
    description = "Android build check (requires Android SDK configured)"
    dependsOn(":androidApp:assembleDebug")
}

