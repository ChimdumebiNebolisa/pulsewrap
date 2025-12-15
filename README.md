# PulseWrap

PulseWrap is a Kotlin Multiplatform application that generates Wrapped-style KPI recaps from local JSON datasets. It runs on both Android and Desktop (JVM) platforms, sharing business logic across platforms while providing platform-specific share/export functionality.

## Features

- **Dataset Selection**: Choose between Demo A and Demo B datasets
- **KPI Insights**: Automatically calculates 8+ insights including:
  - Total Revenue and Expenses
  - Net Profit
  - Best Revenue Day
  - Highest Expenses Day
  - Average Daily Active Users
  - Peak New Users Day
  - Biggest Revenue Spike
  - Burn Rate
  - Runway (when cash balance is available)
  - Top Spending Category
- **Story Cards**: Beautiful, scrollable cards displaying each insight
- **Markdown Export**: Generate and preview markdown reports
- **Platform-Specific Sharing**:
  - Android: Share via system share intent
  - Desktop: Export to file in user home directory

## Tech Stack

- **Kotlin Multiplatform**: Shared business logic across platforms
- **Compose Multiplatform**: Modern declarative UI framework
- **kotlinx.serialization**: JSON parsing
- **kotlinx.datetime**: Date handling
- **Gradle Kotlin DSL**: Build configuration

## Requirements

- JDK 17 or higher
- Android Studio (for Android development)
- IntelliJ IDEA or Android Studio (for Desktop development)

## How to Run

### Desktop

1. Ensure you have JDK 17 installed
2. Open the project in IntelliJ IDEA or Android Studio
3. Run the `desktopApp` configuration, or execute:
   ```bash
   ./gradlew :desktopApp:run
   ```

### Android

1. Open the project in Android Studio
2. Ensure you have an Android emulator running or a device connected
3. Select the `androidApp` run configuration
4. Click Run, or execute:
   ```bash
   ./gradlew :androidApp:installDebug
   ```

## Folder Structure

```
pulsewrap/
├── shared/                    # Shared module
│   ├── commonMain/           # Common code
│   │   ├── model/           # Data models
│   │   ├── data/            # Repository and data loading
│   │   ├── engine/          # Insight calculation and markdown generation
│   │   ├── ui/              # Shared Compose UI components
│   │   └── util/            # Utility functions
│   ├── androidMain/         # Android-specific implementations
│   ├── desktopMain/         # Desktop-specific implementations
│   └── commonTest/          # Unit tests
├── androidApp/              # Android application
│   └── src/main/
│       ├── assets/          # JSON dataset files
│       └── kotlin/          # MainActivity
└── desktopApp/              # Desktop application
    └── src/main/
        └── kotlin/          # Main.kt entry point
```

## Project Structure

The project follows a clean architecture approach:

- **Models**: Data classes with serialization support
- **Data Layer**: Repository pattern with platform-specific resource loading
- **Engine**: Pure functions for calculations and markdown generation
- **UI**: Shared Compose screens with platform-specific entry points
- **Utilities**: Formatting helpers for currency, dates, and numbers

## License

MIT License - see LICENSE file for details

