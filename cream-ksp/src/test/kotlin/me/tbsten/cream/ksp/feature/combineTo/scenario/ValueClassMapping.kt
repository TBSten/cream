package me.tbsten.cream.ksp.feature.combineTo.scenario

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.INT
import me.tbsten.cream.CombineTo
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.classNameOf
import me.tbsten.cream.ksp.testing.poet.dataClass
import me.tbsten.cream.ksp.testing.poet.plusDeclarations
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios
import me.tbsten.cream.ksp.testing.poet.valueClass

private fun combineToMapAnno(vararg targetPropertyNames: String): AnnotationSpec =
    AnnotationSpec.builder(CombineTo.Map::class).apply { targetPropertyNames.forEach { addMember("%S", it) } }.build()

private val COMBINE_TO_EXCLUDE = AnnotationSpec.builder(CombineTo.Exclude::class).build()

/**
 * Automatic value class mapping (issue #21, always on) in the N→1 combine path: the wrap /
 * unwrap default must pick the right source qualifier (`this.` for the receiver source,
 * `sourceB.` for a secondary source), plus the `@CombineTo`-specific `.Map` / `.Exclude`
 * (source-property side) interactions. The full core semantics matrix is pinned once in
 * CopyToSnapshotTest's family — the conversion core is annotation-independent.
 */
internal fun valueClassMappingScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        // SourceA `id: String` -> Target `id: DomainId`: `= DomainId(this.id)` when SourceA is the
        // receiver, `= DomainId(sourceA.id)` in the SourceB-receiver function.
        "wrapIntoValueClass" to
            combinedInto(
                dataClass("Target", Prop("id", classNameOf("DomainId")), Prop("name")),
                dataClass("SourceA", Prop("id")),
                dataClass("SourceB", Prop("name")),
            ).plusDeclarations(valueClass("DomainId")),
        // SourceB `id: DomainId` -> Target `id: String`: `= sourceB.id.value` in the
        // SourceA-receiver function (secondary-source qualifier + unwrap).
        "unwrapFromSecondarySource" to
            combinedInto(
                dataClass("Target", Prop("id"), Prop("name")),
                dataClass("SourceA", Prop("name")),
                dataClass("SourceB", Prop("id", classNameOf("DomainId"))),
            ).plusDeclarations(valueClass("DomainId")),
        // SourceA `id: Int` vs DomainId(value: String): underlying type mismatch, id stays required.
        "underlyingTypeMismatch" to
            combinedInto(
                dataClass("Target", Prop("id", classNameOf("DomainId")), Prop("name")),
                dataClass("SourceA", Prop("id", INT)),
                dataClass("SourceB", Prop("name")),
            ).plusDeclarations(valueClass("DomainId")),
        // @CombineTo.Map renames rawId -> id AND the types differ by the value class: the `.Map`
        // resolution converts too, `id: DomainId = DomainId(this.rawId)` / `DomainId(sourceA.rawId)`.
        "mapRenamedWrap" to
            combinedInto(
                dataClass("Target", Prop("id", classNameOf("DomainId")), Prop("name")),
                dataClass("SourceA", Prop("rawId", paramAnnotation = combineToMapAnno("id"))),
                dataClass("SourceB", Prop("name")),
            ).plusDeclarations(valueClass("DomainId")),
        // @CombineTo.Exclude wins over the conversion: id stays required, no default, and NO
        // "@Exclude ... has no effect" warning (suppressing a conversion default IS effective).
        "excludeSuppressesConversion" to
            combinedInto(
                dataClass("Target", Prop("id", classNameOf("DomainId")), Prop("name")),
                dataClass("SourceA", Prop("id", paramAnnotation = COMBINE_TO_EXCLUDE)),
                dataClass("SourceB", Prop("name")),
            ).plusDeclarations(valueClass("DomainId")),
    )
