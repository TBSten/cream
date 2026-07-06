[← README](../../README.md) | [日本語](./multiplatform.ja.md)

# Kotlin Multiplatform support

cream.kt is designed with Kotlin Multiplatform (KMP) in mind. This page covers which
platforms each published artifact supports, the one KSP limitation you should know about
(`commonMain`), and the recommended workaround setup.

## Supported targets

| Artifact | Role | Platforms |
|---|---|---|
| `me.tbsten.cream:cream-runtime` | The annotations you write in your code | All Kotlin platforms |
| `me.tbsten.cream:cream-ksp` | The KSP processor | JVM only |
| `me.tbsten.cream:cream-ksp-shared` | Shared logic used internally by the processor | JVM / JS / WasmJs (you never depend on this directly) |

The generated copy functions are **plain Kotlin source files**. They are added to the processed
source set and compiled for every target of your module exactly like hand-written code.

There is no platform-specific runtime machinery.

## The commonMain limitation

KSP does not support generating code into intermediate source sets such as `commonMain`
([google/ksp#567](https://github.com/google/ksp/issues/567)).

By default KSP only runs in the per-platform compilations, so code generated from `commonMain`
classes lands in the platform source sets and **cannot be called from `commonMain` code**.

In other words, without additional setup you cannot "annotate a class in `commonMain` and call
the generated copy function from `commonMain`".

The workaround below solves this by running cream in the **metadata (common) compilation** and
wiring its output back into `commonMain`.

Trade-off: with the workaround applied, only `commonMain` is processed. cream annotations placed
in platform source sets (`androidMain`, `jvmMain`, `iosMain`, …) are **not processed** because
the per-platform KSP tasks are disabled. Put your annotated classes in `commonMain`. For the same
reason, the workaround cannot be combined in the same module with another KSP plugin that needs
to process platform source sets.

## Workaround setup

Add the processor to the `kspCommonMainMetadata` configuration and wire the generated sources
back into `commonMain`:

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

Generated files are written under `build/generated/ksp/metadata/commonMain/kotlin/`.

Note: the snippet above **disables every non-metadata `ksp*` task**, so as-is it also stops the
tasks of any other KSP processor in the module (e.g. kotest's KSP plugin). To combine them,
adjust the `if` condition so the other processors' tasks (e.g. the `*Test` ones) are left enabled.

## See also

- [Setup](../../README.md#setup) — the basic (non-KMP) setup
