package me.tbsten.cream.ksp.feature.callFrom.scenario

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier.DATA
import com.squareup.kotlinpoet.KModifier.INTERNAL
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeSpec
import me.tbsten.cream.CopyVisibility
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.classNameOf
import me.tbsten.cream.ksp.testing.poet.clazz
import me.tbsten.cream.ksp.testing.poet.dataClass
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

internal fun visibilityScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        "internalFunction" to
            callFrom(
                FunSpec
                    .builder("audit")
                    .addModifiers(INTERNAL)
                    .addParameter("name", STRING)
                    .build(),
                dataClass("AuditArgs", Prop("name")),
            ),
        "visibilityOverridePublic" to
            callFrom(
                fn("audit", param("name")),
                dataClass("AuditArgs", Prop("name")),
                visibility = CopyVisibility.PUBLIC,
            ),
        "visibilityOverrideInternal" to
            callFrom(
                fn("audit", param("name")),
                dataClass("AuditArgs", Prop("name")),
                visibility = CopyVisibility.INTERNAL,
            ),
        // INHERIT is clamped to `internal` when the bridge references an internal symbol: a
        // public extension on an internal receiver / a public function exposing an internal
        // parameter type would fail user-side compilation.
        "internalEnclosingClassClampsInherit" to
            SnapshotScenario(
                TypeSpec
                    .classBuilder("Holder")
                    .addModifiers(INTERNAL)
                    .addFunction(fn("member", param("value")).withCallFrom(classNameOf("MemberArgs")))
                    .build(),
                dataClass("MemberArgs", Prop("value")),
            ),
        "internalSourceClassClampsInherit" to
            callFrom(
                fn("audit", param("name")),
                clazz("AuditArgs", Prop("name"), modifiers = listOf(INTERNAL, DATA)),
            ),
    )
