package me.tbsten.cream.ksp.feature.copyTo.scenario

import com.squareup.kotlinpoet.ANY
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeAliasSpec
import me.tbsten.cream.CopyTo
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.generator.clazz.GENERATED_PACKAGE
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.classNameOf
import me.tbsten.cream.ksp.testing.poet.dataClass
import me.tbsten.cream.ksp.testing.poet.inputFileSpec
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios
import me.tbsten.cream.ksp.testing.poet.valueClass

private val NULLABLE_STRING = STRING.copy(nullable = true)
private val DOMAIN_ID = classNameOf("DomainId")
private val NULLABLE_DOMAIN_ID = DOMAIN_ID.copy(nullable = true)

private fun copyToMapAnno(vararg targetPropertyNames: String): AnnotationSpec =
    AnnotationSpec.builder(CopyTo.Map::class).apply { targetPropertyNames.forEach { addMember("%S", it) } }.build()

private val COPY_TO_EXCLUDE = AnnotationSpec.builder(CopyTo.Exclude::class).build()

/**
 * Automatic value class mapping (issue #21, always on) through `@CopyTo`. This family carries the
 * full core semantics matrix once — the conversion logic is shared by every annotation, so the
 * sibling families only pin their annotation-specific resolution (`.Map` flavor, `.Exclude`
 * flavor, source qualifiers):
 *
 * - the plain wrap / unwrap / type-mismatch trio,
 * - every nullability cell (nullable source, nullable target, nullable underlying — supported
 *   cells get `?.let { V(it) }` / `?.` defaults, unsound cells stay required and warn),
 * - chained value classes (`V2(V1(x))` / `x.rawId.value`),
 * - a typealias parameter type (the constructor call renders the resolved class),
 * - `.Map`-renamed conversion, `.Exclude` precedence (suppresses the conversion, no warning),
 * - determinism when both directions apply (unwrap wins),
 * - constructor / underlying-property visibility (private skips + warns, internal in-module works),
 * - mixed conversion (unwrap + re-wrap between two value classes) never happens.
 */
internal fun valueClassMappingScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        // String -> DomainId(value: String): id gets `= DomainId(this.id)`.
        "wrapIntoValueClass" to
            SnapshotScenario(
                dataClass("Source", Prop("id"), Prop("name")).withCopyTo(classNameOf("Target")),
                dataClass("Target", Prop("id", DOMAIN_ID), Prop("name")),
                valueClass("DomainId"),
            ),
        // DomainId(value: String) -> String: id gets `= this.id.value`.
        "unwrapFromValueClass" to
            SnapshotScenario(
                dataClass("Source", Prop("id", DOMAIN_ID), Prop("name")).withCopyTo(classNameOf("Target")),
                dataClass("Target", Prop("id"), Prop("name")),
                valueClass("DomainId"),
            ),
        // Source `id: Int` vs DomainId(value: String): underlying type mismatch, id stays required.
        "underlyingTypeMismatch" to
            SnapshotScenario(
                dataClass("Source", Prop("id", INT), Prop("name")).withCopyTo(classNameOf("Target")),
                dataClass("Target", Prop("id", DOMAIN_ID), Prop("name")),
                valueClass("DomainId"),
            ),
        // String -> DomainId?: the non-null wrap upcasts into the nullable parameter.
        "wrapIntoNullableTarget" to
            SnapshotScenario(
                dataClass("Source", Prop("id"), Prop("name")).withCopyTo(classNameOf("Target")),
                dataClass("Target", Prop("id", NULLABLE_DOMAIN_ID), Prop("name")),
                valueClass("DomainId"),
            ),
        // String? -> DomainId?: id gets `= this.id?.let { DomainId(it) }`.
        "wrapFromNullableSourceIntoNullableTarget" to
            SnapshotScenario(
                dataClass("Source", Prop("id", NULLABLE_STRING), Prop("name")).withCopyTo(classNameOf("Target")),
                dataClass("Target", Prop("id", NULLABLE_DOMAIN_ID), Prop("name")),
                valueClass("DomainId"),
            ),
        // String? -> DomainId (non-null): unsound (null cannot wrap), id stays required + warning.
        "wrapFromNullableSourceIntoNonNullTarget" to
            SnapshotScenario(
                dataClass("Source", Prop("id", NULLABLE_STRING), Prop("name")).withCopyTo(classNameOf("Target")),
                dataClass("Target", Prop("id", DOMAIN_ID), Prop("name")),
                valueClass("DomainId"),
            ),
        // DomainId -> String?: the non-null unwrap upcasts into the nullable parameter.
        "unwrapIntoNullableTarget" to
            SnapshotScenario(
                dataClass("Source", Prop("id", DOMAIN_ID), Prop("name")).withCopyTo(classNameOf("Target")),
                dataClass("Target", Prop("id", NULLABLE_STRING), Prop("name")),
                valueClass("DomainId"),
            ),
        // DomainId? -> String?: id gets `= this.id?.value`.
        "unwrapFromNullableSourceIntoNullableTarget" to
            SnapshotScenario(
                dataClass("Source", Prop("id", NULLABLE_DOMAIN_ID), Prop("name")).withCopyTo(classNameOf("Target")),
                dataClass("Target", Prop("id", NULLABLE_STRING), Prop("name")),
                valueClass("DomainId"),
            ),
        // DomainId? -> String (non-null): `this.id?.value` is nullable, id stays required + warning.
        "unwrapFromNullableSourceIntoNonNullTarget" to
            SnapshotScenario(
                dataClass("Source", Prop("id", NULLABLE_DOMAIN_ID), Prop("name")).withCopyTo(classNameOf("Target")),
                dataClass("Target", Prop("id"), Prop("name")),
                valueClass("DomainId"),
            ),
        // DomainId(value: String?): a nullable source feeds the nullable underlying directly,
        // `= DomainId(this.id)` even into a non-null parameter.
        "wrapNullableUnderlyingFromNullableSource" to
            SnapshotScenario(
                dataClass("Source", Prop("id", NULLABLE_STRING), Prop("name")).withCopyTo(classNameOf("Target")),
                dataClass("Target", Prop("id", DOMAIN_ID), Prop("name")),
                valueClass("DomainId", underlyingType = NULLABLE_STRING),
            ),
        // DomainId(value: String?) -> String?: unwrapping yields the nullable underlying, `= this.id.value`.
        "unwrapNullableUnderlyingIntoNullableTarget" to
            SnapshotScenario(
                dataClass("Source", Prop("id", DOMAIN_ID), Prop("name")).withCopyTo(classNameOf("Target")),
                dataClass("Target", Prop("id", NULLABLE_STRING), Prop("name")),
                valueClass("DomainId", underlyingType = NULLABLE_STRING),
            ),
        // DomainId(value: String?) -> String (non-null): the unwrapped value is nullable, id stays
        // required + warning.
        "unwrapNullableUnderlyingIntoNonNullTarget" to
            SnapshotScenario(
                dataClass("Source", Prop("id", DOMAIN_ID), Prop("name")).withCopyTo(classNameOf("Target")),
                dataClass("Target", Prop("id"), Prop("name")),
                valueClass("DomainId", underlyingType = NULLABLE_STRING),
            ),
        // String -> UserId(rawId: RawId(value: String)): id gets `= UserId(RawId(this.id))`.
        "chainedWrap" to
            SnapshotScenario(
                dataClass("Source", Prop("id"), Prop("name")).withCopyTo(classNameOf("Target")),
                dataClass("Target", Prop("id", classNameOf("UserId")), Prop("name")),
                valueClass("RawId"),
                valueClass("UserId", underlyingType = classNameOf("RawId"), underlyingName = "rawId"),
            ),
        // UserId(rawId: RawId(value: String)) -> String: id gets `= this.id.rawId.value`.
        "chainedUnwrap" to
            SnapshotScenario(
                dataClass("Source", Prop("id", classNameOf("UserId")), Prop("name")).withCopyTo(classNameOf("Target")),
                dataClass("Target", Prop("id"), Prop("name")),
                valueClass("RawId"),
                valueClass("UserId", underlyingType = classNameOf("RawId"), underlyingName = "rawId"),
            ),
        // String? -> UserId?: the chain wraps inside the safe call, `= this.id?.let { UserId(RawId(it)) }`.
        "chainedWrapFromNullableSourceIntoNullableTarget" to
            SnapshotScenario(
                dataClass("Source", Prop("id", NULLABLE_STRING), Prop("name")).withCopyTo(classNameOf("Target")),
                dataClass("Target", Prop("id", classNameOf("UserId").copy(nullable = true)), Prop("name")),
                valueClass("RawId"),
                valueClass("UserId", underlyingType = classNameOf("RawId"), underlyingName = "rawId"),
            ),
        // typealias DomainIdAlias = DomainId as the parameter type: the wrap constructor call
        // renders the RESOLVED class (`DomainId(this.id)`), not the alias.
        "typealiasTarget" to
            SnapshotScenario(
                listOf(
                    inputFileSpec(
                        GENERATED_PACKAGE,
                        dataClass("Source", Prop("id"), Prop("name")).withCopyTo(classNameOf("Target")),
                        dataClass("Target", Prop("id", classNameOf("DomainIdAlias")), Prop("name")),
                        valueClass("DomainId"),
                    ).toBuilder()
                        .addTypeAlias(TypeAliasSpec.builder("DomainIdAlias", DOMAIN_ID).build())
                        .build(),
                ),
            ),
        // @CopyTo.Map renames rawId -> id AND the types differ by the value class: the `.Map`
        // resolution converts too, `id: DomainId = DomainId(this.rawId)`.
        "mapRenamedWrap" to
            SnapshotScenario(
                dataClass("Source", Prop("rawId", paramAnnotation = copyToMapAnno("id")), Prop("name"))
                    .withCopyTo(classNameOf("Target")),
                dataClass("Target", Prop("id", DOMAIN_ID), Prop("name")),
                valueClass("DomainId"),
            ),
        // @CopyTo.Exclude wins over the conversion: id stays required, no default, and NO
        // "@Exclude ... has no effect" warning (suppressing a conversion default IS effective).
        "excludeSuppressesConversion" to
            SnapshotScenario(
                dataClass("Source", Prop("id", paramAnnotation = COPY_TO_EXCLUDE), Prop("name"))
                    .withCopyTo(classNameOf("Target")),
                dataClass("Target", Prop("id", DOMAIN_ID), Prop("name")),
                valueClass("DomainId"),
            ),
        // Holder(wrapper: Wrapper) -> Wrapper(raw: Any): BOTH directions apply (unwrap extracts
        // the exact Wrapper, wrap would box the Holder into Any) — unwrap wins deterministically,
        // `= this.item.wrapper`.
        "unwrapWinsOverWrap" to
            SnapshotScenario(
                dataClass("Source", Prop("item", classNameOf("Holder")), Prop("name")).withCopyTo(classNameOf("Target")),
                dataClass("Target", Prop("item", classNameOf("Wrapper")), Prop("name")),
                valueClass("Wrapper", underlyingType = ANY, underlyingName = "raw"),
                valueClass("Holder", underlyingType = classNameOf("Wrapper"), underlyingName = "wrapper"),
            ),
        // A private value class constructor cannot be called from generated code: id stays
        // required + warning.
        "privateConstructorSkipsWrap" to
            SnapshotScenario(
                dataClass("Source", Prop("id"), Prop("name")).withCopyTo(classNameOf("Target")),
                dataClass("Target", Prop("id", DOMAIN_ID), Prop("name")),
                valueClass("DomainId", constructorVisibility = KModifier.PRIVATE),
            ),
        // An internal constructor in the SAME compilation is callable: id wraps normally.
        "internalConstructorWraps" to
            SnapshotScenario(
                dataClass("Source", Prop("id"), Prop("name")).withCopyTo(classNameOf("Target")),
                dataClass("Target", Prop("id", DOMAIN_ID), Prop("name")),
                valueClass("DomainId", constructorVisibility = KModifier.INTERNAL),
            ),
        // A private underlying property cannot be read from generated code: id stays required + warning.
        "privateUnderlyingSkipsUnwrap" to
            SnapshotScenario(
                dataClass("Source", Prop("id", DOMAIN_ID), Prop("name")).withCopyTo(classNameOf("Target")),
                dataClass("Target", Prop("id"), Prop("name")),
                valueClass("DomainId", propertyVisibility = KModifier.PRIVATE),
            ),
        // Two DIFFERENT value classes over the same underlying: unwrap + re-wrap is never mixed,
        // id stays required.
        "differentValueClassesMismatch" to
            SnapshotScenario(
                dataClass("Source", Prop("id", classNameOf("SourceId")), Prop("name")).withCopyTo(classNameOf("Target")),
                dataClass("Target", Prop("id", classNameOf("TargetId")), Prop("name")),
                valueClass("SourceId"),
                valueClass("TargetId"),
            ),
    )
