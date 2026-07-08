package me.tbsten.cream.ksp.feature.copyMapping.scenario

import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.classNameOf
import me.tbsten.cream.ksp.testing.poet.dataClass
import me.tbsten.cream.ksp.testing.poet.plusDeclarations
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios
import me.tbsten.cream.ksp.testing.poet.valueClass

/**
 * Automatic value class mapping (issue #21, always on) via `@CopyMapping` holders:
 * library-to-library copies wrap / unwrap too, including the `canReverse` reverse-direction
 * function (forward wraps, reverse unwraps — same annotation, both directions in one golden).
 */
internal fun valueClassMappingScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        // Source `id: String` -> Target `id: DomainId`: id gets `= DomainId(this.id)`.
        "wrapIntoValueClass" to
            copyMapping(
                mappingHolder(),
                dataClass("Source", Prop("id"), Prop("name")),
                dataClass("Target", Prop("id", classNameOf("DomainId")), Prop("name")),
            ).plusDeclarations(valueClass("DomainId")),
        // Source `id: DomainId` -> Target `id: String`: id gets `= this.id.value`.
        "unwrapFromValueClass" to
            copyMapping(
                mappingHolder(),
                dataClass("Source", Prop("id", classNameOf("DomainId")), Prop("name")),
                dataClass("Target", Prop("id"), Prop("name")),
            ).plusDeclarations(valueClass("DomainId")),
        // canReverse: the forward function wraps (`DomainId(this.id)`), the generated reverse
        // function unwraps (`this.id.value`).
        "canReverseWrapForwardUnwrapReverse" to
            copyMapping(
                mappingHolder(),
                dataClass("Source", Prop("id"), Prop("name")),
                dataClass("Target", Prop("id", classNameOf("DomainId")), Prop("name")),
                canReverse = true,
            ).plusDeclarations(valueClass("DomainId")),
        // properties = [Map("rawId", "id")] renames AND the types differ by the value class: the
        // remapped resolution converts too, `id: DomainId = DomainId(this.rawId)`.
        "propertiesRenamedWrap" to
            copyMapping(
                mappingHolder(),
                dataClass("Source", Prop("rawId"), Prop("name")),
                dataClass("Target", Prop("id", classNameOf("DomainId")), Prop("name")),
                properties = listOf("rawId" to "id"),
            ).plusDeclarations(valueClass("DomainId")),
    )
