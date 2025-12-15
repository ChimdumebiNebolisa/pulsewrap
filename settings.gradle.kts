pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "PulseWrap"

include(":shared")
include(":androidApp")
include(":desktopApp")
include(":webApp")

