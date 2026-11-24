### 2-1. Run scaffold

```sh
katachi scaffold --package com.example.app
```

Pass the app package name identified in step 1 to `--package`. It uses the version of the `katachi` command and the Kotlin version detected from the project, so normally no other arguments are needed. Where the verification module goes (`meta.moduleDir`), the test framework and how dependencies are written come from what you recorded in the checklist in 1-3. Add `--module-dir <path>` / `--test-framework junit5|kotest|kotest-<style>` (the styles are in 1-3) / `--deps catalog|literal` only to hard-code them without recording preferences.

**Add `--kotlin <version>` only in two cases:** `doctor` printed no `Kotlin:` line in 0-1 (the version could not be detected; without `--kotlin`, `scaffold` stops), or "When Kotlin is too old" in 0-2 found that the file `doctor` read the version from does not set the project's Kotlin version. `doctor` does not warn about a missing version: a missing `Kotlin:` line is the only sign. If the project has a Kotlin version elsewhere (the version catalog, `buildSrc`), pass that. If it does not use Kotlin yet (Java only), ask the user which version to use; katachi needs Kotlin 2.2 or later. Put the version you pass into the checklist's `meta.kotlin` too.

`scaffold` creates the whole verification module: its `build.gradle.kts`, `ProjectArchitecture.kt` and `ProjectArchitectureTest.kt`, the `include(...)` in the settings file, and, where the project needs them, the Kotlin JVM plugin in the root build file, aliases in `gradle/libs.versions.toml` and `mavenCentral()` in `pluginManagement { }`. Its output lists every file it created or changed. **Do not edit what it generated**, even the parts below that look like mistakes:

- `tasks.test { }` has `outputs.upToDateWhen { false }` and `outputs.cacheIf { false }`. katachi reads the whole repository, which is not an input of the Test task, so without them Gradle skips the test as `UP-TO-DATE` after files are added or moved, and the test succeeds even when there are violations.
- The Kotlin plugin line stays `kotlin("jvm")` (no version) even with a version catalog. `alias(libs.plugins.kotlin.jvm)` fails with "already on the classpath with an unknown version" when build-logic (includeBuild) already puts the Kotlin plugin on the classpath.
- If only subprojects declare the Kotlin plugin, with a version, the root build file gets nothing and the verification module carries the version. Gradle then warns `The Kotlin Gradle plugin was loaded multiple times in different subprojects ...`; that is expected with this setup. Adding the plugin to the root or removing the version makes plugin resolution fail instead.

When the root build file has a `buildscript { }` block, or the settings file has a shape `scaffold` cannot read, it does not edit that file and prints the line to add instead (2-2 says what to do with it).

Plugin version conflicts, the JUnit engine, the JVM toolchain (below), the compiler options the project's Kotlin version needs, and handling for Android / KMP projects are **all decided by `scaffold`.** Below Kotlin 2.4 it writes `-Xcontext-parameters`, which the DSL cannot be called without; from 2.4 on it leaves the flag out, because there it only warns that it is redundant. Below Kotlin 2.2 it stops and says so — katachi's metadata cannot be read by a compiler that old (`doctor` warns about it first; see "When Kotlin is too old" in 0-2). Even if you feel like fixing something after reading the generated files, do not fix it.

The JVM toolchain reuses the N the project already sets: `jvmToolchain(N)` or `JavaLanguageVersion.of(N)` in another module, the same forms inside buildSrc or build-logic, or a value read from the version catalog or a `val`. A directory that is a build of its own (it has its own settings file, and the root settings neither includes it nor pulls it in with `includeBuild`, like a docs snippet or a sample) is not read, for the toolchain, the Kotlin version or the test framework. katachi needs JDK 17 or later, so an N below 17 is not used. When no setting is found it writes no `jvmToolchain` and simply uses the JDK that runs Gradle (so it does not fail where JDK 17 is missing and no toolchain download is configured). Which of the two happened is in the scaffold output; copy it into the checklist's `meta.jvmToolchain`. `doctor` and `scaffold` also check that the JDK Gradle uses is 17 or later, and stop with a fix if it is not.

The one exception is when the generated file has to change for the build to run at all. That takes priority over the rule above — but it means katachi has a bug, so record it in `changedFiles` giving the reason, and add it to the checklist's `questions` as well.

Add `--no-konsist` only if it has been decided not to use `ktFileContent { }` (it comes with katachi-konsist). With this setup the test passes no `FileConstraintCheck()` to `assert()`, so a `fileConstraint` written in a `gradle { }` hook is not evaluated either, and the test fails with `[UncheckedFileConstraint]` (its How to fix is `Pass FileConstraintCheck() to assert()`). If it hasn't been decided, leave the default as is.

Once it's created, mark `2-1` done.
