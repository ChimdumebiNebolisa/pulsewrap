plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.compiler)
    id("org.jetbrains.compose") version "1.6.10"
}

kotlin {
    wasmJs {
        moduleName = "pulsewrap-web"
        browser {
            commonWebpackConfig {
                outputFileName = "pulsewrap-web.js"
            }
        }
        binaries.executable()
    }
    
    sourceSets {
        val wasmJsMain by getting {
            dependencies {
                implementation(project(":shared"))
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
            }
        }
    }
}

