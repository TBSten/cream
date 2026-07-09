package me.tbsten.cream.ksp.feature.callFrom.scenario

import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeAliasSpec
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.classNameOf
import me.tbsten.cream.ksp.testing.poet.dataClass
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

/**
 * Types are matched through typealiases in both directions (aliased parameter vs. underlying
 * property and vice versa), and the alias name is preserved in the bridge signature.
 */
internal fun typealiasShapeScenarios(): Generator<SnapshotScenario> {
    val userId = TypeAliasSpec.builder("UserId", STRING).build()
    return Generator.snapshotScenarios(
        "typealiasedParameter" to
            callFrom(
                fn("load", param("id", classNameOf("UserId"))),
                dataClass("LoadArgs", Prop("id")),
                typeAliases = listOf(userId),
            ),
        "typealiasedProperty" to
            callFrom(
                fn("load", param("id")),
                dataClass("LoadArgs", Prop("id", classNameOf("UserId"))),
                typeAliases = listOf(userId),
            ),
    )
}
