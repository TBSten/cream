package me.tbsten.cream.test.architecture.support

/** `cream-runtime`'s only source set: every annotation users write lives here. */
internal const val RUNTIME_SOURCE_DIR = "cream-runtime/src/commonMain/kotlin/me/tbsten/cream"

/** The root package of the KSP processor (`me.tbsten.cream.ksp`). */
internal const val KSP_MAIN_DIR = "cream-ksp/src/main/kotlin/me/tbsten/cream/ksp"

/** The root package of the processor's JVM tests. */
internal const val KSP_TEST_DIR = "cream-ksp/src/test/kotlin/me/tbsten/cream/ksp"

/** The annotated classes of the `test` module, which KSP generates code from. */
internal const val INTEGRATION_TEST_DATA_DIR = "test/src/commonMain/kotlin/me/tbsten/cream/test"

/** The kotest specs of the `test` module, which call the generated functions. */
internal const val INTEGRATION_TEST_DIR = "test/src/commonTest/kotlin/me/tbsten/cream/test"

/** This definition itself. */
internal const val ARCHITECTURE_TEST_DIR = "architecture-test/src/test/kotlin/me/tbsten/cream/test/architecture"

/** Sources of the `buildLogic` included build's convention plugins. */
internal const val CONVENTION_SOURCE_DIR = "buildLogic/convention/src/main/kotlin"

/** Where `katachiDocs` writes the pages generated from this definition (committed, checked by CI). */
internal const val ARCHITECTURE_DOCS_DIR = "doc/internal/architecture"
