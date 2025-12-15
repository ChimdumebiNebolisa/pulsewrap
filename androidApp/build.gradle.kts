plugins {
    alias(libs.plugins.android.application)
    kotlin("android")
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.pulsewrap.android"
    compileSdk = 35
    
    defaultConfig {
        applicationId = "com.pulsewrap.android"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }
    
    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(project(":shared"))
    
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.foundation)
    
    implementation("androidx.activity:activity-compose:1.9.2")
    implementation("androidx.core:core-ktx:1.15.0")
}

