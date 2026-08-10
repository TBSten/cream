package me.tbsten.cream.ksp.feature.copyFrom.scenario

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.INT
import me.tbsten.cream.CopyFrom
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.classNameOf
import me.tbsten.cream.ksp.testing.poet.dataClass
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios
import me.tbsten.cream.ksp.testing.poet.valueClass

private fun copyFromMapAnno(vararg sourcePropertyNames: String): AnnotationSpec =
    AnnotationSpec.builder(CopyFrom.Map::class).apply { sourcePropertyNames.forEach { addMember("%S", it) } }.build()

private val COPY_FROM_EXCLUDE = AnnotationSpec.builder(CopyFrom.Exclude::class).build()

/**
 * Automatic value class mapping (issue #21, always on) through `@CopyFrom`: wrap / unwrap
 * defaults for a name-matched property whose type differs only by a `value class` wrapper, the
 * type-mismatch case that pins when NO conversion happens (the parameter stays required), plus
 * the `@CopyFrom`-specific `.Map` (target-parameter side) and `.Exclude` (target-parameter side)
 * interactions. The full core semantics matrix (nullability, chains, typealias, visibility,
 * direction determinism) is pinned once in CopyToSnapshotTest's family — the conversion core is
 * annotation-independent. The first declaration is the annotated target, mirroring
 * `copyFrom(...)` in Utils.kt.
 */
internal fun valueClassMappingScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        // String -> DomainId(value: String): id gets `= DomainId(this.id)`.
        "wrapIntoValueClass" to
            SnapshotScenario(
                dataClass("Target", Prop("id", classNameOf("DomainId")), Prop("name")).withCopyFrom(classNameOf("Source")),
                dataClass("Source", Prop("id"), Prop("name")),
                valueClass("DomainId"),
            ),
        // DomainId(value: String) -> String: id gets `= this.id.value`.
        "unwrapFromValueClass" to
            SnapshotScenario(
                dataClass("Target", Prop("id"), Prop("name")).withCopyFrom(classNameOf("Source")),
                dataClass("Source", Prop("id", classNameOf("DomainId")), Prop("name")),
                valueClass("DomainId"),
            ),
        // Source `id: Int` vs DomainId(value: String): underlying type mismatch, id stays required.
        "underlyingTypeMismatch" to
            SnapshotScenario(
                dataClass("Target", Prop("id", classNameOf("DomainId")), Prop("name")).withCopyFrom(classNameOf("Source")),
                dataClass("Source", Prop("id", INT), Prop("name")),
                valueClass("DomainId"),
            ),
        // @CopyFrom.Map("rawId") on the target parameter renames AND the types differ by the value
        // class: the `.Map` resolution converts too, `id: DomainId = DomainId(this.rawId)`.
        "mapRenamedWrap" to
            SnapshotScenario(
                dataClass("Target", Prop("id", classNameOf("DomainId"), paramAnnotation = copyFromMapAnno("rawId")), Prop("name"))
                    .withCopyFrom(classNameOf("Source")),
                dataClass("Source", Prop("rawId"), Prop("name")),
                valueClass("DomainId"),
            ),
        // @CopyFrom.Exclude wins over the conversion: id stays required, no default, and NO
        // "@Exclude ... has no effect" warning (suppressing a conversion default IS effective).
        "excludeSuppressesConversion" to
            SnapshotScenario(
                dataClass("Target", Prop("id", classNameOf("DomainId"), paramAnnotation = COPY_FROM_EXCLUDE), Prop("name"))
                    .withCopyFrom(classNameOf("Source")),
                dataClass("Source", Prop("id"), Prop("name")),
                valueClass("DomainId"),
            ),
    )
