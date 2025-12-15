# PulseWrap — “Audit + Fix Everything” Cursor Prompt (Plan Mode)

## Cursor prompt (paste this whole thing into Cursor Plan Mode)

You are working inside this repo: **PulseWrap** (Kotlin Multiplatform + Compose Multiplatform). The goal is to make the repo buildable from a clean clone on **Windows** using **JDK 17 + Gradle Wrapper only** (no global Gradle install), and ensure the **web target** (wasmJs) is actually present and buildable if it exists locally.

### Context
- The GitHub repo currently appears to have `androidApp/`, `desktopApp/`, `shared/`, root docs, but the **Gradle wrapper is missing** and the README uses `./gradlew`, which breaks clean-clone builds on Windows.
- There were earlier local milestones mentioning `webApp/` (wasmJs). If `webApp/` exists locally, it must be committed and pushed. If it does not exist, create it (minimal skeleton) and wire it correctly.

### Hard requirements
- Must run on Windows via `.\gradlew.bat ...`
- Wrapper must be committed: `gradlew`, `gradlew.bat`, `gradle/wrapper/gradle-wrapper.jar`, `gradle/wrapper/gradle-wrapper.properties`
- Wrapper version: **Gradle 8.9** (AGP 8.7.3 compatibility)
- Keep changes minimal and reproducible
- Add a root Gradle convenience task: `quickCheck`
- Verify by actually running commands at the end

---

## Phase 0 — Repo reality check (do this first)
1. Print current branch and last commits:
   - `git rev-parse --abbrev-ref HEAD`
   - `git log --oneline -n 10`
2. Confirm remote + tracking:
   - `git remote -v`
   - `git branch -vv`
3. Confirm wrapper is missing (or incomplete):
   - check for `gradlew`, `gradlew.bat`, and `gradle/wrapper/gradle-wrapper.properties` at repo root.
4. Confirm whether `webApp/` exists:
   - `dir` (Windows) or `ls` (mac/linux)
   - If `webApp/` exists locally but not in git, you must commit it later.

If any of these checks show we’re on the wrong branch or remote, fix that first (checkout correct branch, set upstream, etc.) BEFORE doing build changes.

---

## Phase 1 — M0: Add Gradle Wrapper WITHOUT system Gradle
Goal: generate wrapper using a locally downloaded Gradle 8.9 distribution, then delete temp artifacts.

### Steps
1. Create a temp directory:
   - `mkdir .tmp`
2. Download Gradle 8.9 binary ZIP into `.tmp`:
   - Use PowerShell:
     - `powershell -NoProfile -Command "Invoke-WebRequest -Uri https://services.gradle.org/distributions/gradle-8.9-bin.zip -OutFile .tmp\gradle-8.9-bin.zip"`
3. Extract it:
   - `powershell -NoProfile -Command "Expand-Archive -Path .tmp\gradle-8.9-bin.zip -DestinationPath .tmp -Force"`
   - This should create `.tmp\gradle-8.9\`
4. Generate wrapper using downloaded Gradle:
   - `.\.tmp\gradle-8.9\bin\gradle.bat wrapper --gradle-version 8.9 --distribution-type bin`
5. Verify wrapper files now exist:
   - `gradlew`
   - `gradlew.bat`
   - `gradle\wrapper\gradle-wrapper.jar`
   - `gradle\wrapper\gradle-wrapper.properties`
6. Verify `gradle-wrapper.properties` contains:
   - `distributionUrl=https\://services.gradle.org/distributions/gradle-8.9-bin.zip`
7. Delete `.tmp`:
   - `rmdir /s /q .tmp` (Windows)
8. Add `.tmp/` to `.gitignore` (create `.gitignore` if missing).

---

## Phase 2 — M0.1: Make README Windows-friendly
Update README run commands so users see both:
- Windows: `.\gradlew.bat <task>`
- macOS/Linux: `./gradlew <task>`

Keep it short. Also add a note that the wrapper downloads Gradle automatically.

---

## Phase 3 — M0.2: Add root quickCheck task
In root `build.gradle.kts`, add:

- A `tasks.register("quickCheck")` that depends on:
  - `:shared:check`
  - `:desktopApp:build`
  - `:webApp:build` (ONLY if webApp exists and has a working “build” task)
- If `:webApp:build` is not a valid task for wasm, discover correct tasks via:
  - `.\gradlew.bat :webApp:tasks --all`
  - Then depend on the most appropriate build/distribution task (for example, often `wasmJsBrowserDistribution` or similar).
- Android is heavier. Only include `:androidApp:assembleDebug` if it completes in a reasonable time; otherwise document it separately in README.

The goal is:
- `.\gradlew.bat quickCheck`

---

## Phase 4 — M0.3: Ensure webApp exists + is wired correctly
If `webApp/` does not exist:
- Create `webApp/` as a KMP/Compose wasmJs module (minimal) that depends on `:shared`.
- Add it to `settings.gradle.kts`.
- Ensure it builds on Windows with the wrapper.
If `webApp/` exists locally but is uncommitted:
- Make sure it’s included in settings and depends on shared properly.
- Make sure it actually compiles (even if it only shows “Hello PulseWrap”).

---

## Phase 5 — Verification (must actually run these)
Run in repo root (Windows):
1. `.\gradlew.bat --version`
2. `.\gradlew.bat :shared:check`
3. `.\gradlew.bat quickCheck`

If anything fails:
- Fix the underlying issue, then rerun the exact same verification commands until green.

---

## Phase 6 — Commit + push (use this exact message)
After all checks pass:
- `git status` (ensure no temp artifacts)
- Commit with:
  - `M0: Add Gradle wrapper + quickCheck task`
- Push to the correct branch and verify GitHub shows the wrapper files.

If webApp was added/wired in this same work, it can be included in the same commit ONLY if the repo was previously unbuildable; otherwise split into a separate commit:
- `M1: Add webApp wasmJs target (skeleton)`

### Required output at the end
- A concise summary of files changed
- The exact commands you ran and their success
- The exact git commands used to commit + push

---

## Commands I (Mitch) will run after you finish (Windows)
- `.\gradlew.bat --version`
- `.\gradlew.bat quickCheck`
- Optional heavier checks:
  - `.\gradlew.bat :androidApp:assembleDebug`
  - `.\gradlew.bat :desktopApp:run`
  - `.\gradlew.bat :webApp:tasks --all`

---

## Suggested milestone commits (do NOT skip)
### Milestone M0 — Wrapper + quickCheck + README
```bash
git add gradlew gradlew.bat gradle/wrapper/ .gitignore README.md build.gradle.kts
git commit -m "M0: Add Gradle wrapper + quickCheck task"
git push
```

### Milestone M1 — Web module (only if needed / not already on GitHub)
```bash
git add settings.gradle.kts webApp/ shared/build.gradle.kts
git commit -m "M1: Add webApp wasmJs target (skeleton)"
git push
```
