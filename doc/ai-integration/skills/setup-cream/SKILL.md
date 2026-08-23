---
name: setup-cream
description: Set up cream.kt — a KSP plugin that generates cross-class copy functions — in a Kotlin or Kotlin Multiplatform module. Applies the KSP plugin, adds the cream-runtime / cream-ksp dependencies, and wires up the commonMain workaround that KMP requires. Use when adding cream.kt to a project for the first time.
license: Apache-2.0
---

# Setup cream.kt skill

Sets [cream.kt](https://github.com/TBSten/cream) up in a Kotlin module, right now.

cream.kt is a KSP plugin that generates copy functions across classes (`Loading` -> `Success`,
data model -> domain model). Setting it up means applying KSP, adding two dependencies, and — on
Kotlin Multiplatform — applying a workaround for a KSP limitation around `commonMain`.

## Prerequisites

- [ ] A Gradle project using the Kotlin plugin.
    - Maven and other build systems are not supported. Stop and tell the user.

## Workflow

### 1. Survey the project

Determine all of the following before editing anything:

- [ ] **Which module** to set cream.kt up in. If the project has more than one module and the user
      did not name one, ask.
- [ ] **Single-platform or Kotlin Multiplatform**, i.e. whether the target module applies
      `org.jetbrains.kotlin.multiplatform`. This decides whether step 4 is needed.
- [ ] **The Kotlin version**. KSP2 works across a range of Kotlin versions, so this is not a
      lookup key any more — you need it only if the project turns out to require the deprecated
      KSP1 (see step 2).
- [ ] **Whether a version catalog** (`gradle/libs.versions.toml`) is in use. If it is, declare the
      versions there rather than hard-coding them in the build script.
- [ ] **Whether another KSP processor** already runs in that module (Room, kotest, Koin, ...).
      This matters for step 4 — see [Notes](#notes).

### 2. Resolve versions

Resolve the latest cream.kt version:

```shell
gh release view --repo TBSten/cream --json tagName -q .tagName
```

If `gh` is unavailable, read Maven Central directly:

```shell
curl -s https://repo1.maven.org/maven2/me/tbsten/cream/cream-runtime/maven-metadata.xml
```

For KSP, take the latest release from <https://github.com/google/ksp/releases>.

Since **KSP 2.3.0** the version is a plain single version (`2.3.0`, `2.3.1`, ... ) and is
**no longer tied to the Kotlin compiler version** — KSP2 is a standalone tool built on stable
compiler APIs rather than a compiler plugin, so it is compatible across a range of Kotlin
versions. Do not try to match the first component to the project's Kotlin version; they are
independent (KSP `2.3.11` is built against Kotlin `2.3.20`).

Versions in the old `<kotlin-version>-<ksp-version>` form (up to `2.2.21-2.0.5`) are KSP1, which is
**deprecated**. Only use one if the project is pinned to a Kotlin version that KSP2 does not
support yet; in that case pick the entry matching the project's Kotlin version exactly.

### 3. Apply the plugin and dependencies

```kts
// module/build.gradle.kts
plugins {
    id("com.google.devtools.ksp") version "<ksp-version>"
}

dependencies {
    implementation("me.tbsten.cream:cream-runtime:<cream-version>")
    ksp("me.tbsten.cream:cream-ksp:<cream-version>")
}
```

`cream-runtime` holds the annotations you write; `cream-ksp` is the processor that reads them.

With a version catalog, declare them there and alias instead:

```toml
# gradle/libs.versions.toml
[versions]
cream = "<cream-version>"
ksp = "<ksp-version>"

[libraries]
cream-runtime = { module = "me.tbsten.cream:cream-runtime", version.ref = "cream" }
cream-ksp = { module = "me.tbsten.cream:cream-ksp", version.ref = "cream" }

[plugins]
ksp = { id = "com.google.devtools.ksp", version.ref = "ksp" }
```

For a Kotlin Multiplatform module, put `cream-runtime` in the `commonMain` source set's
dependencies, and skip the `ksp(...)` line — step 4 replaces it.

### 4. Kotlin Multiplatform workaround (KMP modules only)

KSP cannot generate code into intermediate source sets such as `commonMain`
([google/ksp#567](https://github.com/google/ksp/issues/567)). Without the workaround below,
functions generated from `commonMain` classes land in the platform source sets and **cannot be
called from `commonMain`**.

```kts
// module/build.gradle.kts
dependencies {
    add("kspCommonMainMetadata", "me.tbsten.cream:cream-ksp:<cream-version>")
}

fun Project.setupKspForMultiplatformWorkaround() {
    kotlin.sourceSets.commonMain {
        kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
    }

    tasks.configureEach {
        if (name.startsWith("ksp") && name != "kspCommonMainKotlinMetadata") {
            dependsOn(tasks.named("kspCommonMainKotlinMetadata"))
            enabled = false
        }
    }
}
setupKspForMultiplatformWorkaround()
```

This runs cream in the metadata (common) compilation and wires its output back into `commonMain`.
Generated files land in `build/generated/ksp/metadata/commonMain/kotlin/`.

Details: [Kotlin Multiplatform support](https://github.com/TBSten/cream/blob/main/doc/customization/multiplatform.md)

### 5. Decide the generated function naming style

**The defaults are the recommendation — but ask before committing to them.** Function names are
baked into every call site, so changing the style later is a project-wide rename; setup is the one
cheap moment to decide, which is why it is worth a question rather than an assumption. If the user
has no preference, keep the defaults and write no `ksp { }` block at all.

Ask two things. Use an Ask-style tool if one is available.

```
Q1. What should generated copy functions be called?

- [ ] copyToXxx  (default)
- [ ] toXxx           — shortest at the call site
- [ ] transitionToXxx — reads well for UI state machines
- [ ] mapToXxx        — reads well for cross-layer model mapping
```

```
Q2. How much of the target class name should the function name include?
    (for a transition com.example.Aaa.Bbb -> com.example.Aaa.Bbb.Ccc.Ddd)

- [ ] under-package (default) — copyToAaaBbbCccDdd
- [ ] diff                    — copyToCccDdd
- [ ] inner-name              — copyToBbbCccDdd
- [ ] simple-name             — copyToDdd
- [ ] full-name               — copyToComExampleAaaBbbCccDdd
```

`diff` and `simple-name` give the shortest names but collide more easily when two targets share a
simple name; `under-package` is the safe default. If the user has no preference, say you are
keeping the defaults and move on.

Write out only the options that differ from the defaults — an `arg(...)` restating a default is
noise:

```kts
ksp {
    arg("cream.copyFunNamePrefix", "to")
    arg("cream.copyFunNamingStrategy", "diff")
}
```

When a prefix ends with a letter, the first character of the following name is capitalized
(`to` + `uiStateSuccess` -> `toUiStateSuccess`).

Names can also be overridden per declaration with the `funName` annotation argument, so a
module-wide choice is not final for individual cases. The remaining options
(`cream.escapeDot`, `cream.notCopyToObject`, `cream.defaultVisibility`,
`cream.autoValueClassMapping`) have sensible defaults — add them only when the user asks for that
specific behaviour.

Full list: [KSP Options](https://github.com/TBSten/cream/blob/main/doc/customization/options.md),
naming in detail: [Function name](https://github.com/TBSten/cream/blob/main/doc/customization/fun-name.md)

### 6. Verify

Setup is not done until generated code has actually been compiled. A build that compiles zero
annotations proves nothing, so verify with a real annotation.

1. Add a throwaway pair in the module's main source set (`commonMain` for KMP):

    ```kt
    import me.tbsten.cream.CopyTo

    @CopyTo(CreamSetupCheckTarget::class)
    data class CreamSetupCheckSource(val value: String)

    data class CreamSetupCheckTarget(val value: String, val extra: Int)
    ```

2. Run the processor:

    ```shell
    # KMP
    ./gradlew :<module>:kspCommonMainKotlinMetadata
    # single-platform
    ./gradlew :<module>:compileKotlin
    ```

3. Confirm the generated file exists:

    ```shell
    find <module>/build/generated/ksp -name '*.kt'
    ```

   It should contain a `fun CreamSetupCheckSource.<prefix>CreamSetupCheckTarget(...)` — with the
   defaults, `copyToCreamSetupCheckTarget`. Read the real name from the file rather than assuming
   it; this also confirms the naming style chosen in step 5 took effect.

4. **On KMP, compile against it.** `kspCommonMainKotlinMetadata` only runs the processor — it
   compiles nothing, so it reports `BUILD SUCCESSFUL` even when `commonMain` cannot see the
   generated code. That is exactly the failure the workaround exists to prevent, so step 2 does not
   verify it and a real compile task is required. Add a call next to the throwaway classes:

    ```kt
    internal fun creamSetupCheckCall(): CreamSetupCheckTarget =
        CreamSetupCheckSource("v").copyToCreamSetupCheckTarget(extra = 1)
    ```

    then compile the common metadata plus at least one platform:

    ```shell
    ./gradlew :<module>:compileCommonMainKotlinMetadata :<module>:compileKotlinJvm
    ```

    In the output the per-platform `ksp*` tasks should be `SKIPPED` — that is the workaround
    disabling them — while the compile tasks succeed. If the module has a native or web target,
    compile one of those too; they break in ways the JVM target does not.

5. Remove the throwaway classes and the call.

Then report to the user: the module, the versions, whether the KMP workaround was applied, and
where generated sources land.

## Notes

- **Only `commonMain` is processed on KMP, and it fails silently.** The workaround disables every
  non-metadata `ksp*` task, so a cream annotation in `androidMain` / `jvmMain` / `iosMain` produces
  no file, **no error and no warning** — the build succeeds and the function simply never exists.
  Annotated classes must live in `commonMain`. If a generated function is missing, check which
  source set its class is in before anything else.
- **The workaround also disables other KSP processors' tasks** in the same module. If the module
  already runs Room, kotest, Koin or similar, narrow the `if` condition so their tasks stay
  enabled (e.g. leave the `*Test` tasks alone) — and tell the user you did so, and why.
- **`cream-ksp` is JVM-only**, but that is a build-time concern. The generated code is plain
  Kotlin source compiled for every target of the module, so there is no platform-specific runtime.
- **Version catalogs**: if the project uses one, declare `cream` and `ksp` there and reference
  them from the build script. Do not mix styles within one project.

## References

- [cream.kt README](https://github.com/TBSten/cream/blob/main/README.md) — setup and quick start
- [Kotlin Multiplatform support](https://github.com/TBSten/cream/blob/main/doc/customization/multiplatform.md)
- [KSP Options](https://github.com/TBSten/cream/blob/main/doc/customization/options.md)
- [Refactor by cream skill](https://github.com/TBSten/cream/blob/main/doc/ai-integration/skills/refactor-by-cream/SKILL.md) — apply cream.kt to existing code once setup is done
