package me.tbsten.cream.ksp.feature.combineFrom.scenario

import com.squareup.kotlinpoet.AnnotationSpec
import me.tbsten.cream.CombineFrom
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.classNameOf
import me.tbsten.cream.ksp.testing.poet.dataClass
import me.tbsten.cream.ksp.testing.poet.plusDeclarations
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios
import me.tbsten.cream.ksp.testing.poet.valueClass

private fun combineFromMapAnno(vararg sourcePropertyNames: String): AnnotationSpec =
    AnnotationSpec.builder(CombineFrom.Map::class).apply { sourcePropertyNames.forEach { addMember("%S", it) } }.build()

/**
 * Automatic value class mapping (issue #21, always on) through `@CombineFrom` (target-annotated
 * N→1 combine): the same combine emission path as `@CombineTo`, so this family only pins the
 * annotation-specific bits — the plain wrap and the `@CombineFrom.Map` (target-parameter side)
 * renamed conversion. The full core semantics matrix is pinned once in CopyToSnapshotTest's
 * family — the conversion core is annotation-independent.
 */
internal fun valueClassMappingScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        // SourceA `id: String` -> Target `id: DomainId`: `= DomainId(this.id)` when SourceA is the
        // receiver, `= DomainId(sourceA.id)` in the SourceB-receiver function.
        "wrapIntoValueClass" to
            combineFrom(
                dataClass("Target", Prop("id", classNameOf("DomainId")), Prop("name")),
                dataClass("SourceA", Prop("id")),
                dataClass("SourceB", Prop("name")),
            ).plusDeclarations(valueClass("DomainId")),
        // @CombineFrom.Map("rawId") on the target parameter renames AND the types differ by the
        // value class: the `.Map` resolution converts too, `id: DomainId = DomainId(this.rawId)`.
        "mapRenamedWrap" to
            combineFrom(
                dataClass(
                    "Target",
                    Prop("id", classNameOf("DomainId"), paramAnnotation = combineFromMapAnno("rawId")),
                    Prop("name"),
                ),
                dataClass("SourceA", Prop("rawId")),
                dataClass("SourceB", Prop("name")),
            ).plusDeclarations(valueClass("DomainId")),
    )
