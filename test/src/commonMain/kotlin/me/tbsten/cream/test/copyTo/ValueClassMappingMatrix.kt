package me.tbsten.cream.test.copyTo

import me.tbsten.cream.CopyTo
import kotlin.jvm.JvmInline

// E2E coverage for every SUPPORTED cell of the value-class conversion matrix (issue #21):
// nullability cells, nullable underlying, chained value classes, typealias resolution, `.Map`
// renaming, `.Exclude` precedence, internal constructors, and direction determinism. The
// unsupported cells (nullable source -> non-null param etc.) leave the parameter required and are
// pinned by the `valueClassMapping` snapshot family in cream-ksp.

// --- nullability matrix ---

// Wrap `String` -> `AutoMappedId?`: the non-null wrap upcasts into the nullable parameter.
@CopyTo(WrapIntoNullableTarget::class)
data class WrapIntoNullableSource(
    val id: String,
)

data class WrapIntoNullableTarget(
    val id: AutoMappedId?,
)

// Wrap `String?` -> `AutoMappedId?` via `this.id?.let { AutoMappedId(it) }`.
@CopyTo(WrapNullableSourceTarget::class)
data class WrapNullableSourceSource(
    val id: String?,
)

data class WrapNullableSourceTarget(
    val id: AutoMappedId?,
)

// Unwrap `AutoMappedId` -> `String?`: the non-null unwrap upcasts into the nullable parameter.
@CopyTo(UnwrapIntoNullableTarget::class)
data class UnwrapIntoNullableSource(
    val id: AutoMappedId,
)

data class UnwrapIntoNullableTarget(
    val id: String?,
)

// Unwrap `AutoMappedId?` -> `String?` via `this.id?.value`.
@CopyTo(UnwrapNullableSourceTarget::class)
data class UnwrapNullableSourceSource(
    val id: AutoMappedId?,
)

data class UnwrapNullableSourceTarget(
    val id: String?,
)

// --- nullable underlying ---

@JvmInline
value class NullableInnerId(
    val value: String?,
)

// Wrap `String?` -> `NullableInnerId` (non-null): the nullable source feeds the nullable
// underlying directly.
@CopyTo(NullableInnerWrapTarget::class)
data class NullableInnerWrapSource(
    val id: String?,
)

data class NullableInnerWrapTarget(
    val id: NullableInnerId,
)

// Unwrap `NullableInnerId` -> `String?`: unwrapping yields the nullable underlying.
@CopyTo(NullableInnerUnwrapTarget::class)
data class NullableInnerUnwrapSource(
    val id: NullableInnerId,
)

data class NullableInnerUnwrapTarget(
    val id: String?,
)

// --- chained value classes ---

@JvmInline
value class ChainedRawId(
    val value: String,
)

@JvmInline
value class ChainedUserId(
    val rawId: ChainedRawId,
)

// Wrap `String` -> `ChainedUserId` via `ChainedUserId(ChainedRawId(this.id))`.
@CopyTo(ChainedWrapTarget::class)
data class ChainedWrapSource(
    val id: String,
)

data class ChainedWrapTarget(
    val id: ChainedUserId,
)

// Unwrap `ChainedUserId` -> `String` via `this.id.rawId.value`.
@CopyTo(ChainedUnwrapTarget::class)
data class ChainedUnwrapSource(
    val id: ChainedUserId,
)

data class ChainedUnwrapTarget(
    val id: String,
)

// Chained wrap inside the safe call: `String?` -> `ChainedUserId?` via
// `this.id?.let { ChainedUserId(ChainedRawId(it)) }`.
@CopyTo(ChainedNullableWrapTarget::class)
data class ChainedNullableWrapSource(
    val id: String?,
)

data class ChainedNullableWrapTarget(
    val id: ChainedUserId?,
)

// --- typealias resolution ---

typealias AutoMappedIdAlias = AutoMappedId

// Wrap through an aliased parameter type: the generated call constructs the resolved class.
@CopyTo(TypealiasWrapTarget::class)
data class TypealiasWrapSource(
    val id: String,
)

data class TypealiasWrapTarget(
    val id: AutoMappedIdAlias,
)

// Unwrap from an aliased source property type.
@CopyTo(TypealiasUnwrapTarget::class)
data class TypealiasUnwrapSource(
    val id: AutoMappedIdAlias,
)

data class TypealiasUnwrapTarget(
    val id: String,
)

// --- `.Map` renaming + conversion ---

// @CopyTo.Map renames rawId -> id AND the types differ by the value class: the `.Map` resolution
// converts too.
@CopyTo(MapRenamedWrapTarget::class)
data class MapRenamedWrapSource(
    @CopyTo.Map("id") val rawId: String,
)

data class MapRenamedWrapTarget(
    val id: AutoMappedId,
)

// --- `.Exclude` precedence ---

// @CopyTo.Exclude wins over the conversion: `id` stays a REQUIRED parameter (this only compiles
// because the test passes it explicitly).
@CopyTo(ExcludedWrapTarget::class)
data class ExcludedWrapSource(
    @CopyTo.Exclude val id: String,
    val name: String,
)

data class ExcludedWrapTarget(
    val id: AutoMappedId,
    val name: String,
)

// --- constructor visibility ---

@JvmInline
value class InternalCtorId internal constructor(
    val value: String,
)

// An internal constructor in the same module is callable from generated code.
@CopyTo(InternalCtorWrapTarget::class)
data class InternalCtorWrapSource(
    val id: String,
)

data class InternalCtorWrapTarget(
    val id: InternalCtorId,
)

// --- direction determinism ---

@JvmInline
value class AmbiguousWrapper(
    val raw: Any,
)

@JvmInline
value class AmbiguousHolder(
    val wrapper: AmbiguousWrapper,
)

// BOTH directions apply (`AmbiguousWrapper(this.item)` would also compile): unwrap wins and the
// generated default extracts the exact wrapper, `this.item.wrapper`.
@CopyTo(UnwrapWinsTarget::class)
data class UnwrapWinsSource(
    val item: AmbiguousHolder,
)

data class UnwrapWinsTarget(
    val item: AmbiguousWrapper,
)
