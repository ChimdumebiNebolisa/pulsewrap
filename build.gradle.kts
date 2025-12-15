buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    id("org.jetbrains.compose") version "1.6.10" apply false
}

tasks.register("quickCheck") {
    group = "verification"
    description = "Quick build checks for shared, desktop, and web modules"
    dependsOn(
        ":shared:check",
        ":desktopApp:build",
        ":webApp:build"
    )
}

