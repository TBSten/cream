package me.tbsten.cream.ksp.feature.callFrom.scenario

import com.squareup.kotlinpoet.AnnotationSpec
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.dataClass
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

/**
 * `@Deprecated(WARNING)` on the annotated function / a matched source property is propagated
 * onto the bridge, so the bridge's references compile warning-free (`-Werror`-safe). `ERROR` /
 * `HIDDEN` levels cannot be referenced from generated code at all and are diagnostics
 * (covered in `CallFromInvalidUsageTest`).
 */
internal fun deprecatedShapeScenarios(): Generator<SnapshotScenario> {
    fun deprecated(message: String): AnnotationSpec =
        AnnotationSpec
            .builder(Deprecated::class)
            .addMember("%S", message)
            .build()
    return Generator.snapshotScenarios(
        "deprecatedFunction" to
            callFrom(
                fn("legacyProcess", param("value"))
                    .toBuilder()
                    .addAnnotation(deprecated("Use processV2 instead."))
                    .build(),
                dataClass("ProcessArgs", Prop("value")),
            ),
        "deprecatedMatchedSourceProperty" to
            callFrom(
                fn("track", param("name"), param("legacyId")),
                dataClass(
                    "TrackArgs",
                    Prop("name"),
                    Prop("legacyId", paramAnnotation = deprecated("Use uuid instead.")),
                ),
            ),
    )
}
