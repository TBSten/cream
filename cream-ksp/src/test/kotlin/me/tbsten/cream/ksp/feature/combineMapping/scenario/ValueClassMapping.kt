package me.tbsten.cream.ksp.feature.combineMapping.scenario

import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.classNameOf
import me.tbsten.cream.ksp.testing.poet.dataClass
import me.tbsten.cream.ksp.testing.poet.plusDeclarations
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios
import me.tbsten.cream.ksp.testing.poet.valueClass

/**
 * Automatic value class mapping (issue #21, always on) via `@CombineMapping` holders:
 * the N→1 combine wrap / unwrap picks the right source qualifier (`this.` for the primary source,
 * `sourceB.` for a secondary source).
 */
internal fun valueClassMappingScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        // Primary SourceA `id: String` -> Target `id: DomainId`: id gets `= DomainId(this.id)`.
        "wrapIntoValueClass" to
            combineMapping(
                mappingHolder(),
                sources =
                    listOf(
                        dataClass("SourceA", Prop("id")),
                        dataClass("SourceB", Prop("name")),
                    ),
                target = dataClass("Target", Prop("id", classNameOf("DomainId")), Prop("name")),
            ).plusDeclarations(valueClass("DomainId")),
        // Secondary SourceB `id: DomainId` -> Target `id: String`: id gets `= sourceB.id.value`.
        "unwrapFromSecondarySource" to
            combineMapping(
                mappingHolder(),
                sources =
                    listOf(
                        dataClass("SourceA", Prop("name")),
                        dataClass("SourceB", Prop("id", classNameOf("DomainId"))),
                    ),
                target = dataClass("Target", Prop("id"), Prop("name")),
            ).plusDeclarations(valueClass("DomainId")),
        // properties = [Map("rawId", "id")] renames AND the types differ by the value class: the
        // remapped resolution converts too, `id: DomainId = DomainId(this.rawId)`.
        "propertiesRenamedWrap" to
            combineMapping(
                mappingHolder(),
                sources =
                    listOf(
                        dataClass("SourceA", Prop("rawId")),
                        dataClass("SourceB", Prop("name")),
                    ),
                target = dataClass("Target", Prop("id", classNameOf("DomainId")), Prop("name")),
                properties = listOf("rawId" to "id"),
            ).plusDeclarations(valueClass("DomainId")),
    )
