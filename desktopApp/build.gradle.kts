plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(project(":shared"))
    implementation(compose.desktop.currentOs)
}

compose.desktop {
    application {
        mainClass = "com.pulsewrap.desktop.MainKt"
        
        nativeDistributions {
            targetFormats(org.jetbrains.compose.desktop.application.dsl.TargetFormat.Dmg, org.jetbrains.compose.desktop.application.dsl.TargetFormat.Msi, org.jetbrains.compose.desktop.application.dsl.TargetFormat.Deb)
            packageName = "PulseWrap"
            packageVersion = "1.0.0"
            
            windows {
                menuGroup = "PulseWrap"
                upgradeUuid = "18159995-d967-4cd2-8885-77BFA97CFA9F"
            }
        }
    }
}

