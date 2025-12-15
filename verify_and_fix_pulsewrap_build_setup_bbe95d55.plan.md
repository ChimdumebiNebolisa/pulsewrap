---
name: Verify and Fix PulseWrap Build Setup
overview: Verify the current PulseWrap repository state, test the build system on Windows, and fix any issues found. The Gradle wrapper, quickCheck task, and webApp module appear to already exist, so this plan focuses on verification and corrections.
todos: []
---

# PulseWrap Build Verification and Fix Plan

## Current State Assessment

Based on initial investigation:

- ✅ Gradle wrapper files exist and are committed (version 8.9)
- ✅ `quickCheck` task exists in [build.gradle.kts](build.gradle.kts)
- ✅ `webApp/` module exists and is wired in [settings.gradle.kts](settings.gradle.kts)
- ✅ README.md already has Windows-friendly commands
- ✅ `.gitignore` exists with `.tmp/` entry
- ✅ Repository is on `main` branch with correct remote

## Phase 0: Repository Reality Check

1. **Verify Git State**

- Confirm current branch: `git rev-parse --abbrev-ref HEAD`
- Check remote tracking: `git remote -v` and `git branch -vv`
- Verify wrapper files are tracked: `git ls-files | findstr gradlew`

2. **Verify Wrapper Files**

- Check `gradlew` and `gradlew.bat` exist at root
- Verify `gradle/wrapper/gradle-wrapper.properties` contains Gradle 8.9
- Confirm `gradle/wrapper/gradle-wrapper.jar` exists

3. **Verify Module Structure**

- Confirm `webApp/` directory exists
- Verify `webApp` is included in [settings.gradle.kts](settings.gradle.kts)
- Check [webApp/build.gradle.kts](webApp/build.gradle.kts) is properly configured

## Phase 1: Verify and Fix quickCheck Task

The current `quickCheck` task in [build.gradle.kts](build.gradle.kts) references `:webApp:build`. For wasmJs targets, this may not be the correct task.

1. **Discover Available Tasks**

- Run `.\gradlew.bat :webApp:tasks --all` to list all available tasks
- Identify the correct build/distribution task for wasmJs (likely `wasmJsBrowserDistribution` or `wasmJsBrowserProductionWebpack`)

2. **Update quickCheck Task** (if needed)

- If `:webApp:build` doesn't exist or isn't appropriate, update [build.gradle.kts](build.gradle.kts) to use the correct task
- Ensure the task dependencies are:
 - `:shared:check`
 - `:desktopApp:build`
 - Appropriate webApp task (e.g., `:webApp:wasmJsBrowserDistribution`)

## Phase 2: Verify README Windows-Friendliness

1. **Review [README.md](README.md)**

- Ensure all commands show both Windows (`.\gradlew.bat`) and Unix (`./gradlew`) variants
- Add note about wrapper automatically downloading Gradle if missing

2. **Update if Needed**

- Add Windows command examples where missing
- Add brief note about Gradle wrapper auto-download

## Phase 3: Build Verification

Run actual build commands to verify everything works:

1. **Basic Wrapper Test**

- `.\gradlew.bat --version` - Should show Gradle 8.9

2. **Module Build Tests**

- `.\gradlew.bat :shared:check` - Verify shared module builds and tests pass
- `.\gradlew.bat :desktopApp:build` - Verify desktop app builds
- `.\gradlew.bat :webApp:tasks --all` - Discover and verify webApp tasks

3. **quickCheck Task Test**

- `.\gradlew.bat quickCheck` - Should complete successfully
- If it fails, identify the failing task and fix the dependency

## Phase 4: Fix Any Issues Found

Based on verification results:

1. **If quickCheck fails:**

- Update task dependencies in [build.gradle.kts](build.gradle.kts)
- Re-run verification

2. **If webApp build fails:**

- Check [webApp/build.gradle.kts](webApp/build.gradle.kts) configuration
- Verify dependencies in [shared/build.gradle.kts](shared/build.gradle.kts) include wasmJs target
- Fix any configuration issues

3. **If wrapper issues found:**

- Regenerate wrapper if version is incorrect
- Ensure all wrapper files are present

## Phase 5: Final Verification

After all fixes:

1. Run full verification suite:

- `.\gradlew.bat --version`
- `.\gradlew.bat :shared:check`
- `.\gradlew.bat quickCheck`

2. Verify git status is clean (only expected files)

## Phase 6: Commit Any Fixes (if needed)

If any changes were made:

1. Review changes: `git status`
2. Stage relevant files
3. Commit with appropriate message:

- If only quickCheck fix: `M0.1: Fix quickCheck task dependencies`
- If README update: `M0.2: Update README with Windows commands`
- If multiple fixes: `M0: Fix build verification issues`

## Expected Outcomes

- All wrapper files present and committed
- `quickCheck` task works correctly on Windows
- All modules build successfully
- README has clear Windows instructions
- Repository is in a clean, buildable state

## Files Likely to Change

- [build.gradle.kts](build.gradle.kts) - May need quickCheck task update
- [README.md](README.md) - May need Windows command clarifications
- No wrapper regeneration needed (already at 8.9)