# PulseWrap (Insightify KPI Recap) — Kotlin Multiplatform Project Plan (Cursor Plan Mode)

> NOTE: This document is intentionally provided as **plain Markdown text** (with LaTeX kept as literal `\( ... \)` / `\[ ... \]`) so you can paste it into Cursor without anything getting “rendered” or cut off.

---

## 0) One-line goal
Build a **Kotlin Multiplatform** app called **PulseWrap** that generates a **Wrapped-style KPI recap** from local JSON datasets, running on **Android + Desktop (JVM)**, with **shared business logic** and platform-specific **share/export**.

---

## 1) Target platforms & constraints
- Platforms:
  - Android (Compose)
  - Desktop JVM (Compose)
- Must run from a clean clone using **JDK 17**.
- No app store publishing required.
- Keep it **offline-first** (no required external APIs).
- Scope must be a “real app,” not Hello World.

---

## 2) Deliverables checklist (what must exist in repo)
- Kotlin Multiplatform project using **Compose Multiplatform**.
- Modules:
  - `shared/` (models + parsing + calculations + shared UI)
  - `androidApp/`
  - `desktopApp/`
- App features:
  1) Pick Demo Dataset A/B
  2) Generate recap (story cards)
  3) View Markdown report preview
  4) Android: Share Markdown via intent
  5) Desktop: Export Markdown to file
- Repo files at root:
  - `README.md` with run steps for Android + Desktop + features + tech stack
  - `LICENSE` (MIT)
  - `ESSAY.md` with headings + TODO placeholders

---

## 3) Tech stack (required)
- Kotlin Multiplatform + Gradle Kotlin DSL
- Compose Multiplatform
- `kotlinx.serialization` (JSON)
- `kotlinx.datetime` (dates)

Optional:
- Minimal logging (Kermit/Napier) if needed, but keep scope small.

---

## 4) Data model & datasets (local JSON)

### 4.1 Datasets required
Include **two demo variants** (A and B). Each variant has:
- `kpi_daily_A.json`, `category_spend_A.json`
- `kpi_daily_B.json`, `category_spend_B.json`

### 4.2 JSON schema

#### KPI Daily Record
Fields:
- `date`: string in `YYYY-MM-DD`
- `revenue`: number
- `expenses`: number
- `activeUsers`: integer
- `newUsers`: integer
- `cashBalance` (optional): number (if present, runway can be computed)

#### Category Spend Record
Fields:
- `date`: string in `YYYY-MM-DD`
- `category`: string
- `amount`: number

---

### 4.3 Example JSON (Demo A)

`kpi_daily_A.json`
```json
[
  {
    "date": "2025-11-01",
    "revenue": 1200,
    "expenses": 700,
    "activeUsers": 95,
    "newUsers": 10,
    "cashBalance": 8000
  },
  {
    "date": "2025-11-02",
    "revenue": 900,
    "expenses": 650,
    "activeUsers": 102,
    "newUsers": 14,
    "cashBalance": 8250
  },
  {
    "date": "2025-11-03",
    "revenue": 1600,
    "expenses": 720,
    "activeUsers": 120,
    "newUsers": 18,
    "cashBalance": 9130
  }
]
```

`category_spend_A.json`
```json
[
  { "date": "2025-11-01", "category": "Ads",   "amount": 120 },
  { "date": "2025-11-01", "category": "Cloud", "amount": 90  },
  { "date": "2025-11-02", "category": "Ads",   "amount": 140 },
  { "date": "2025-11-03", "category": "Tools", "amount": 60  }
]
```

---

### 4.4 Example JSON (Demo B) — must include a dip + a spike + different top category

`kpi_daily_B.json`
```json
[
  {
    "date": "2025-11-01",
    "revenue": 1500,
    "expenses": 900,
    "activeUsers": 140,
    "newUsers": 22,
    "cashBalance": 12000
  },
  {
    "date": "2025-11-02",
    "revenue": 650,
    "expenses": 980,
    "activeUsers": 110,
    "newUsers": 9,
    "cashBalance": 11670
  },
  {
    "date": "2025-11-03",
    "revenue": 2100,
    "expenses": 1050,
    "activeUsers": 170,
    "newUsers": 35,
    "cashBalance": 12720
  },
  {
    "date": "2025-11-04",
    "revenue": 1900,
    "expenses": 870,
    "activeUsers": 160,
    "newUsers": 18,
    "cashBalance": 13750
  }
]
```

`category_spend_B.json`
```json
[
  { "date": "2025-11-01", "category": "Payroll", "amount": 400 },
  { "date": "2025-11-01", "category": "Cloud",   "amount": 120 },
  { "date": "2025-11-02", "category": "Payroll", "amount": 420 },
  { "date": "2025-11-02", "category": "Tools",   "amount": 80  },
  { "date": "2025-11-03", "category": "R&D",     "amount": 650 },
  { "date": "2025-11-04", "category": "R&D",     "amount": 500 }
]
```

In Demo B, the top category should be **R&D** (different from Demo A’s **Ads**).

---

## 5) Insights to compute (the “story cards”)
Compute **at least 8 insights** from the selected dataset.

### 5.1 Definitions (use these exact formulas)

Let daily records be indexed by day \( i \) with:
- \( R_i \) = revenue
- \( E_i \) = expenses
- \( A_i \) = activeUsers
- \( N_i \) = newUsers

**Totals**
- Total revenue: \( R_{tot} = \sum_i R_i \)
- Total expenses: \( E_{tot} = \sum_i E_i \)
- Net profit: \( P_{net} = R_{tot} - E_{tot} \)

**Best revenue day**
- Best day: \( \arg\max_i R_i \)

**Highest expenses day**
- Highest expense day: \( \arg\max_i E_i \)

**Average daily active users**
- \( \overline{A} = \frac{1}{n}\sum_i A_i \)

**Peak new users day**
- \( \arg\max_i N_i \)

**Biggest revenue spike (day-over-day)**
- \( \Delta R_i = R_i - R_{i-1} \) for \( i \ge 2 \)
- Biggest spike day: \( \arg\max_i \Delta R_i \)

**Burn rate (avg daily expenses)**
- \( \text{BurnRate} = \frac{E_{tot}}{n} \)

**Runway (optional; only if cashBalance exists)**
- Use most recent cash balance \( C_{last} \)
- If \( \text{BurnRate} > 0 \), runway in days:
  - \( \text{RunwayDays} = \frac{C_{last}}{\text{BurnRate}} \)
- If \( \text{BurnRate} = 0 \), omit runway.

**Top spending category**
- Sum by category: \( S(c) = \sum_{j \in \text{spend where category}=c} amount_j \)
- Top category: \( \arg\max_c S(c) \)

### 5.2 Output formatting per card
Each card should show:
- Title (e.g., “Best Revenue Day”)
- Primary value (e.g., `$1,600`)
- Supporting detail (e.g., date + short sentence)

---

## 6) App screens & UX (minimal but polished)

### 6.1 Navigation
Home → Recap → Markdown Preview  
Back navigation works on both platforms.

### 6.2 Screen specs

#### Home Screen
- App title: PulseWrap
- Dataset picker: Demo A / Demo B
- Button: “Generate Recap”

#### Recap Screen
- Scrollable list/grid of story cards
- Consistent spacing/typography
- Error state if dataset missing/empty

#### Markdown Preview Screen
- Show generated Markdown in a scrollable text area
- Buttons:
  - Android: “Share”
  - Desktop: “Export .md”

---

## 7) Export / Share requirements

### 7.1 Android Share
- Use `ACTION_SEND`
- MIME type `text/markdown` (fallback `text/plain`)
- Share the full Markdown report string

### 7.2 Desktop Export
- Preferred: save dialog
- Acceptable fallback: write to user home directory with timestamp:
  - `PulseWrap_Report_YYYYMMDD_HHMM.md`
- Show confirmation message with the saved path

---

## 8) Architecture (make shared code real)

### 8.1 Shared layers (in `shared`)
- `model/` data classes + serialization
- `data/` loader + repository
- `engine/` calculations + formatting
- `ui/` shared Compose screens
- `util/` formatting (currency, dates)

### 8.2 Resource loading (must work on Android + Desktop)
Implement an `expect/actual` loader:

**commonMain**
- `expect object DatasetLoader { fun loadText(path: String): String }`

**androidMain**
- Load from `androidApp/src/main/assets/` using Android assets.

**desktopMain**
- Load from `shared/src/commonMain/resources/` using classpath stream (e.g., `getResourceAsStream`).

Fallback allowed if needed:
- Embed JSON as multiline strings in commonMain (still keep files in repo for transparency).

### 8.3 Pure functions for engine
- `fun computeInsights(daily: List<KpiDaily>, spend: List<CategorySpend>): List<InsightCard>`
- `fun toMarkdown(insights: List<InsightCard>, meta: ReportMeta): String`

---

## 9) Error handling requirements
Handle gracefully with UI messaging:
- Missing resource file
- Empty dataset
- Date parsing errors (skip invalid records, show warning count)
- Division by zero (burn rate = 0 → runway omitted)

---

## 10) Testing (minimum)
Add unit tests in `shared` for:
- Totals/net profit
- Biggest revenue spike
- Top spending category
- Runway omitted when cashBalance absent

---

## 11) README / LICENSE / ESSAY templates

### 11.1 README.md must include
- What PulseWrap is (1–2 sentences)
- Features list
- Tech stack
- How to run Desktop
- How to run Android
- Folder structure overview
- Screenshots (optional)

### 11.2 LICENSE
- MIT license text at repo root

### 11.3 ESSAY.md headings (placeholders)
- Background
- Idea & Purpose
- Tech Used (KMP, Compose, serialization)
- How It Works
- What I’d Improve Next

---

## 12) Milestones & acceptance criteria

### Milestone 1 — Project bootstrapped, runs on both
Acceptance:
- Desktop launches
- Android launches (emulator)
- Navigation works

### Milestone 2 — Data loading + engine
Acceptance:
- Demo A/B loads
- 8 insights computed correctly

### Milestone 3 — UI story cards + Markdown preview
Acceptance:
- Cards render on both platforms
- Markdown preview displays generated report

### Milestone 4 — Android share + Desktop export
Acceptance:
- Share intent works
- Export creates `.md` file and shows success message

### Milestone 5 — Packaging
Acceptance:
- README complete
- MIT LICENSE present
- ESSAY.md skeleton present
- Repo is clean and reproducible

---

## 13) Implementation notes (avoid time sinks)
- Prefer Android + Desktop only (no iOS) to avoid Mac/Xcode dependency.
- Avoid external APIs for MVP.
- Avoid complex charting; story cards with numbers are enough.
- Optimize for a clean demo flow in < 90 seconds.
