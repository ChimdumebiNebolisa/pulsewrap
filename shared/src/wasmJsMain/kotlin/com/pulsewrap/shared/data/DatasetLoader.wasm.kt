package com.pulsewrap.shared.data

actual object DatasetLoader {
    actual fun loadText(path: String): String {
        // M2: Inline JSON strings for MVP - will be populated in M4
        return when (path) {
            "kpi_daily_A.json" -> """
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
            """.trimIndent()
            "category_spend_A.json" -> """
                [
                  { "date": "2025-11-01", "category": "Ads",   "amount": 120 },
                  { "date": "2025-11-01", "category": "Cloud", "amount": 90  },
                  { "date": "2025-11-02", "category": "Ads",   "amount": 140 },
                  { "date": "2025-11-03", "category": "Tools", "amount": 60  }
                ]
            """.trimIndent()
            "kpi_daily_B.json" -> """
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
            """.trimIndent()
            "category_spend_B.json" -> """
                [
                  { "date": "2025-11-01", "category": "Payroll", "amount": 400 },
                  { "date": "2025-11-01", "category": "Cloud",   "amount": 120 },
                  { "date": "2025-11-02", "category": "Payroll", "amount": 420 },
                  { "date": "2025-11-02", "category": "Tools",   "amount": 80  },
                  { "date": "2025-11-03", "category": "R&D",     "amount": 650 },
                  { "date": "2025-11-04", "category": "R&D",     "amount": 500 }
                ]
            """.trimIndent()
            else -> throw RuntimeException("Unknown dataset file: $path")
        }
    }
}

