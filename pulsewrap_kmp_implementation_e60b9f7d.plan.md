---
name: PulseWrap KMP Implementation
overview: Build a Kotlin Multiplatform app (Android + Desktop) that generates Wrapped-style KPI recaps from local JSON datasets, with shared business logic and platform-specific share/export functionality.
todos:
  - id: setup-gradle
    content: Create Gradle configuration files (settings.gradle.kts, root build.gradle.kts, module build files) with KMP, Compose Multiplatform, and required dependencies
    status: pending
  - id: create-models
    content: Create data models (KpiDaily, CategorySpend, InsightCard, ReportMeta) with kotlinx.serialization annotations
    status: pending
    dependencies:
      - setup-gradle
  - id: create-json-datasets
    content: Create JSON dataset files (A and B variants) in Android assets and shared resources directories
    status: pending
    dependencies:
      - setup-gradle
  - id: implement-dataset-loader
    content: Implement expect/actual DatasetLoader for Android (assets) and Desktop (classpath resources)
    status: pending
    dependencies:
      - create-models
      - create-json-datasets
  - id: implement-repository
    content: Create DatasetRepository with JSON parsing and error handling
    status: pending
    dependencies:
      - implement-dataset-loader
  - id: implement-insight-engine
    content: Implement computeInsights() function calculating all 8+ insights (totals, best days, spikes, burn rate, runway, top category)
    status: pending
    dependencies:
      - create-models
      - implement-repository
  - id: implement-markdown-generator
    content: Implement toMarkdown() function formatting insights as markdown report
    status: pending
    dependencies:
      - implement-insight-engine
  - id: create-utilities
    content: Create formatting utilities (currency, dates, numbers)
    status: pending
    dependencies:
      - create-models
  - id: implement-shared-ui
    content: Create shared Compose screens (HomeScreen, RecapScreen, MarkdownPreviewScreen) with navigation
    status: pending
    dependencies:
      - implement-insight-engine
      - implement-markdown-generator
      - create-utilities
  - id: implement-android-app
    content: Create Android MainActivity, manifest, and ShareHandler with ACTION_SEND intent
    status: pending
    dependencies:
      - implement-shared-ui
  - id: implement-desktop-app
    content: Create Desktop Main.kt entry point and ExportHandler with file save dialog
    status: pending
    dependencies:
      - implement-shared-ui
  - id: add-error-handling
    content: Add comprehensive error handling throughout (missing files, empty data, invalid dates, division by zero)
    status: pending
    dependencies:
      - implement-repository
      - implement-insight-engine
  - id: write-unit-tests
    content: Write unit tests for insight calculations, markdown generation, and data loading
    status: pending
    dependencies:
      - implement-insight-engine
      - implement-markdown-generator
  - id: create-documentation
    content: Create README.md, LICENSE (MIT), and ESSAY.md skeleton with required sections
    status: pending
---

# PulseWrap Kotlin Multiplatform Implementation Plan

## Project Structure

The project will be organized as a Kotlin Multiplatform project with three main modules:

- `shared/` - Common business logic, models, UI, and calculations
- `androidApp/` - Android-specific entry point and share functionality
- `desktopApp/` - Desktop JVM entry point and file export functionality

## Architecture Overview

```
shared/
├── commonMain/
│   ├── model/          # Data classes (KpiDaily, CategorySpend, InsightCard)
│   ├── data/           # DatasetLoader (expect), Repository
│   ├── engine/         # computeInsights(), toMarkdown()
│   ├── ui/             # Shared Compose screens (Home, Recap, MarkdownPreview)
│   └── util/           # Currency/date formatting
├── androidMain/        # DatasetLoader actual (Android assets)
└── desktopMain/        # DatasetLoader actual (classpath resources)

androidApp/
└── src/main/
    ├── assets/         # JSON dataset files
    └── kotlin/         # MainActivity, ShareHandler

desktopApp/
└── src/main/
    ├── resources/      # JSON dataset files (via shared)
    └── kotlin/         # Main.kt, ExportHandler
```

## Implementation Steps

### Phase 1: Project Setup & Gradle Configuration

**Files to create:**

- `settings.gradle.kts` - KMP project configuration
- `build.gradle.kts` (root) - Shared dependencies
- `shared/build.gradle.kts` - Shared module config with Compose Multiplatform
- `androidApp/build.gradle.kts` - Android app config
- `desktopApp/build.gradle.kts` - Desktop JVM config
- `gradle.properties` - JDK 17, Kotlin version settings

**Key dependencies:**

- Kotlin Multiplatform plugin
- Compose Multiplatform (android + desktop)
- kotlinx.serialization (JSON)
- kotlinx.datetime
- JDK 17 toolchain

### Phase 2: Data Models & Serialization

**Files in `shared/commonMain/model/`:**

- `KpiDaily.kt` - Data class with @Serializable, fields: date, revenue, expenses, activeUsers, newUsers, cashBalance (optional)
- `CategorySpend.kt` - Data class with @Serializable, fields: date, category, amount
- `InsightCard.kt` - Data class for story cards (title, primaryValue, supportingDetail)
- `ReportMeta.kt` - Metadata for markdown report (dataset name, generation date)

**Implementation:**

- Use `@Serializable` with `kotlinx.serialization.json.Json`
- Date parsing using `kotlinx.datetime.LocalDate`
- Handle optional `cashBalance` field gracefully

### Phase 3: Resource Loading (expect/actual)

**Files:**

- `shared/commonMain/data/DatasetLoader.kt` - `expect object DatasetLoader { fun loadText(path: String): String }`
- `shared/androidMain/data/DatasetLoader.android.kt` - Load from `androidApp/src/main/assets/`
- `shared/desktopMain/data/DatasetLoader.desktop.kt` - Load from classpath resources

**JSON files to create:**

- `androidApp/src/main/assets/kpi_daily_A.json`
- `androidApp/src/main/assets/category_spend_A.json`
- `androidApp/src/main/assets/kpi_daily_B.json`
- `androidApp/src/main/assets/category_spend_B.json`
- `shared/src/commonMain/resources/` (same files for desktop)

### Phase 4: Data Repository

**File: `shared/commonMain/data/DatasetRepository.kt`**

- `fun loadDataset(variant: String): Result<Pair<List<KpiDaily>, List<CategorySpend>>>`
- Handles file loading, JSON parsing, error handling
- Returns Result type for error propagation

### Phase 5: Insight Calculation Engine

**File: `shared/commonMain/engine/InsightEngine.kt`**

- `fun computeInsights(daily: List<KpiDaily>, spend: List<CategorySpend>): List<InsightCard>`

**Insights to compute (minimum 8):**

1. Total Revenue - sum of all revenue
2. Total Expenses - sum of all expenses
3. Net Profit - revenue - expenses
4. Best Revenue Day - max revenue with date
5. Highest Expenses Day - max expenses with date
6. Average Daily Active Users - mean of activeUsers
7. Peak New Users Day - max newUsers with date
8. Biggest Revenue Spike - max day-over-day increase
9. Burn Rate - average daily expenses
10. Runway (if cashBalance exists) - cashBalance / burnRate
11. Top Spending Category - category with highest total spend

**File: `shared/commonMain/engine/MarkdownGenerator.kt`**

- `fun toMarkdown(insights: List<InsightCard>, meta: ReportMeta): String`
- Formats insights as markdown with headers, values, and details

### Phase 6: Utility Functions

**File: `shared/commonMain/util/Formatters.kt`**

- `fun formatCurrency(amount: Double): String` - "$1,200.00"
- `fun formatDate(date: LocalDate): String` - "Nov 1, 2025"
- `fun formatNumber(value: Int): String` - "1,200"

### Phase 7: Shared UI Components

**Files in `shared/commonMain/ui/`:**

- `HomeScreen.kt` - Dataset picker (A/B), "Generate Recap" button
- `RecapScreen.kt` - Scrollable LazyColumn of InsightCard composables
- `MarkdownPreviewScreen.kt` - Scrollable TextField/TextArea showing markdown
- `InsightCard.kt` - Reusable card component (title, primary value, detail)
- `Navigation.kt` - Simple navigation state management

**Design:**

- Use Material 3 theming
- Consistent spacing and typography
- Error states with user-friendly messages
- Loading states during data processing

### Phase 8: Android App

**Files:**

- `androidApp/src/main/AndroidManifest.xml` - App configuration
- `androidApp/src/main/kotlin/MainActivity.kt` - Compose entry point, navigation
- `androidApp/src/main/kotlin/ShareHandler.kt` - Share intent with ACTION_SEND, MIME text/markdown

**Implementation:**

- Use `androidx.compose.ui.platform.ClipboardManager` and `Intent.ACTION_SEND`
- Handle share button in MarkdownPreviewScreen

### Phase 9: Desktop App

**Files:**

- `desktopApp/src/main/kotlin/Main.kt` - JVM entry point with Compose Desktop
- `desktopApp/src/main/kotlin/ExportHandler.kt` - File save dialog or home directory fallback

**Implementation:**

- Use `java.awt.FileDialog` or `javax.swing.JFileChooser` for save dialog
- Fallback: write to `System.getProperty("user.home")` with timestamp filename
- Show confirmation dialog with saved path

### Phase 10: Error Handling

**Throughout codebase:**

- Handle missing JSON files gracefully
- Handle empty datasets
- Skip invalid date records (log warning, continue)
- Handle division by zero (burn rate = 0 → omit runway)
- Show user-friendly error messages in UI

### Phase 11: Unit Tests

**Files in `shared/commonTest/`:**

- `InsightEngineTest.kt` - Test totals, net profit, revenue spike, top category
- `MarkdownGeneratorTest.kt` - Test markdown output format
- `DatasetRepositoryTest.kt` - Test data loading (with test JSON)

**Test cases:**

- Total revenue/expenses calculation
- Net profit calculation
- Biggest revenue spike detection
- Top spending category aggregation
- Runway omitted when cashBalance absent

### Phase 12: Documentation

**Files:**

- `README.md` - Project description, features, tech stack, run instructions for both platforms, folder structure
- `LICENSE` - MIT license text
- `ESSAY.md` - Skeleton with headings: Background, Idea & Purpose, Tech Used, How It Works, What I'd Improve Next

## Key Implementation Details

### Resource Loading Strategy

- Android: Use `AssetManager.open()` from context
- Desktop: Use `ClassLoader.getResourceAsStream()` from shared resources
- Both platforms load from their respective resource locations

### Navigation

- Simple state-based navigation (sealed class for screens)
- Back button handling on both platforms
- Pass dataset variant and insights between screens

### Markdown Export Format

```
# PulseWrap KPI Recap

**Dataset:** Demo A
**Generated:** November 3, 2025

## Total Revenue
$3,700.00

## Net Profit
$1,630.00

## Best Revenue Day
$1,600.00
November 3, 2025

...
```

### Testing Strategy

- Unit tests for pure calculation functions
- Use test JSON fixtures in `commonTest/resources/`
- Mock DatasetLoader for repository tests