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
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.kotlin.serialization) apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
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

