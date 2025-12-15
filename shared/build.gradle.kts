plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
}

val hasAndroidSdk = try {
    project.findProperty("android.sdk.dir") != null || 
    System.getenv("ANDROID_HOME") != null ||
    file("${System.getProperty("user.home")}/.android/sdk").exists()
} catch (e: Exception) {
    false
}

// Conditionally apply Android plugin only when SDK is available
if (hasAndroidSdk) {
    apply(plugin = libs.plugins.android.library.get().pluginId)
    
    configure<com.android.build.gradle.LibraryExtension> {
        namespace = "com.pulsewrap.shared"
        compileSdk = 35
        
        sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
        sourceSets["main"].res.srcDirs("src/androidMain/res")
        sourceSets["main"].resources.srcDirs("src/commonMain/resources")
        
        defaultConfig {
            minSdk = 24
        }
        
        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }
    }
}

kotlin {
    if (hasAndroidSdk) {
        androidTarget {
            compilations.all {
                kotlinOptions {
                    jvmTarget = "17"
                }
            }
        }
    }
    
    jvm("desktop") {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }
    
    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
    wasmJs {
        browser {
            commonWebpackConfig {
                outputFileName = "shared.js"
            }
        }
        binaries.executable()
    }
    
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material3)
                api(compose.ui)
                api(compose.components.resources)
                api(compose.components.uiToolingPreview)
                
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlinx.datetime)
                implementation(libs.kotlinx.coroutines.core)
            }
        }
        
        if (hasAndroidSdk) {
            val androidMain by getting
        }
        
        val desktopMain by getting
        
        val wasmJsMain by getting
        
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

// Make Android test tasks optional when SDK is not available
if (!hasAndroidSdk) {
    tasks.matching { it.name.contains("Android", ignoreCase = true) || it.name.contains("testDebugUnitTest") || it.name.contains("DebugUnitTest") }.configureEach {
        enabled = false
    }
    
    // Customize check task to exclude Android tests when SDK is missing
    tasks.named("check").configure {
        setDependsOn(dependsOn.filterNot { 
            it.toString().contains("Android", ignoreCase = true) || 
            it.toString().contains("testDebugUnitTest") ||
            it.toString().contains("DebugUnitTest")
        })
    }
}

