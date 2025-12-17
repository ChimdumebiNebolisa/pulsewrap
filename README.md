# PulseWrap

**Transform structured datasets into human-readable KPI summaries**

---

## Table of Contents

- [Project Description](#project-description)
- [Features](#features)
- [Technical Implementation](#technical-implementation)
- [How to Run](#how-to-run)
- [Future Plans](#future-plans)
- [License](#license)

---

## Project Description

PulseWrap generates Wrapped-style KPI recaps from structured JSON datasets. It processes daily KPI records and category spending data to produce deterministic insights with narrative summaries and markdown export capabilities.

**Problem**: Extracting meaningful insights from structured datasets requires manual analysis and formatting. PulseWrap automates insight generation, trend computation, and narrative output, making data interpretation faster and more accessible.

**Solution**: A Kotlin Multiplatform application that computes 10+ financial and engagement metrics from JSON input, groups insights by category, generates human-readable narratives, and exports formatted reports across Android, desktop, and web platforms.

---

## Features

### Core Capabilities

- **Deterministic Insight Generation**: Computes 10+ metrics including total revenue, expenses, net profit, best revenue day, highest expense day, average daily active users, peak new users day, biggest revenue spike, burn rate, and runway calculations
- **Structured Narrative Output**: Generates contextual summaries that highlight profitability status and key performance highlights
- **Multi-Platform Preview**: Shared UI layer renders consistently across Android, desktop (JVM), and web (Wasm) targets
- **Markdown Export**: Generates formatted markdown reports with dataset metadata and all computed insights
- **Platform-Specific Sharing**:
  - Android: System share intent integration
  - Desktop: File export to user home directory
  - Web: In-browser preview and copy functionality

### Data Processing

- JSON dataset loading with `kotlinx.serialization`
- Date parsing and validation using `kotlinx.datetime`
- Currency and number formatting utilities
- Date range extraction and formatting
- Category spending aggregation

### UI Components

- Landing screen with dataset selection
- Input screen for dataset variant selection
- Recap screen with grouped insight cards
- Markdown preview screen
- Responsive card layouts with tier-based styling

---

## Technical Implementation

### Architecture

**Shared Core Logic** (`shared/commonMain`):
- `engine/`: Pure functions for insight computation (`InsightEngine`), markdown generation (`MarkdownGenerator`), narrative generation (`NarrativeGenerator`), and caption formatting (`CaptionGenerator`)
- `model/`: Data classes with serialization support (`KpiDaily`, `CategorySpend`, `InsightCard`, `RecapData`)
- `data/`: Repository pattern with platform-specific resource loading (`DatasetRepository`, `DatasetLoader`)
- `util/`: Formatting helpers for currency, dates, and numbers (`Formatters`, `DateRangeUtils`)

**UI Layer** (`shared/commonMain/ui`):
- Compose Multiplatform screens shared across all targets
- Material 3 design system
- Platform-agnostic navigation and state management

**Platform-Specific Implementations**:
- `androidMain/`: Android resource loading and share intent integration
- `desktopMain/`: Desktop file system access for export
- `wasmJsMain/`: Web resource loading and browser APIs

### Key Decisions

**Kotlin Multiplatform**: Enables code sharing for business logic and UI across three platforms. The shared codebase reduces maintenance overhead and ensures consistent calculation results across targets.

**Compose Multiplatform**: Provides declarative UI framework that compiles to native Android views, Swing/Compose for Desktop, and WebAssembly DOM bindings. Single UI codebase maintains visual consistency.

**Pure Function Architecture**: Insight computation functions are stateless and deterministic, making them easily testable and platform-independent.

**Tradeoffs**:
- WebAssembly target requires separate resource loading implementation due to limited file system access
- Platform-specific sharing implementations needed due to different system APIs
- Conditional Android plugin application when SDK is unavailable enables builds without Android toolchain

### Libraries

- **Compose Multiplatform** (1.6.10): UI framework
- **kotlinx.serialization** (1.7.0): JSON parsing
- **kotlinx.datetime** (0.6.1): Date handling and formatting
- **kotlinx.coroutines** (1.8.0): Asynchronous operations
- **Kotlin** (2.0.21): Language version

---

## How to Run

### Prerequisites

- JDK 17 or higher
- Gradle (wrapper included, downloads automatically)
- For Android: Android SDK (optional, project builds without it)
- For Web: Modern browser with WebAssembly support (Chrome/Chromium recommended)

### Desktop

```bash
# Windows
.\gradlew.bat :desktopApp:run

# macOS/Linux
./gradlew :desktopApp:run
```

The application launches with a window interface. Select a dataset variant (Demo A or Demo B) to generate insights.

### Android

```bash
# Build APK
.\gradlew.bat :androidApp:assembleDebug  # Windows
./gradlew :androidApp:assembleDebug      # macOS/Linux
```

Open the project in Android Studio, connect a device or start an emulator, and run the `androidApp` configuration.

**Sample Data**: JSON datasets are included in `androidApp/src/main/assets/`:
- `kpi_daily_A.json` / `kpi_daily_B.json`
- `category_spend_A.json` / `category_spend_B.json`

### Web (Wasm)

```bash
# Start development server
.\gradlew.bat :webApp:wasmJsBrowserDevelopmentRun  # Windows
./gradlew :webApp:wasmJsBrowserDevelopmentRun      # macOS/Linux
```

Open the URL shown in the terminal (typically `http://localhost:8080`) in a browser.

### Build Verification

```bash
# Quick check (shared, desktop, web - no Android SDK required)
.\gradlew.bat quickCheck  # Windows
./gradlew quickCheck      # macOS/Linux

# Full check including Android (requires Android SDK)
.\gradlew.bat androidCheck  # Windows
./gradlew androidCheck      # macOS/Linux
```

---

## Future Plans

- **Additional Export Formats**: PDF generation, CSV export for raw insights
- **Extended Insight Types**: Trend analysis (week-over-week, month-over-month), seasonality detection, anomaly identification
- **Custom Dataset Input**: File picker for user-provided JSON datasets
- **Data Visualization**: Charts and graphs for revenue trends, expense breakdowns, user growth curves
- **Comparison Mode**: Side-by-side analysis of multiple datasets
- **Template System**: Customizable narrative templates and insight prioritization
- **Performance Optimizations**: Lazy loading for large datasets, incremental insight computation

---

## License

MIT License - see [LICENSE](LICENSE) file for details
