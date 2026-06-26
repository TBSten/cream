package me.tbsten.cream.ksp.feature.combineTo.scenario

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.MemberName
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.classNameOf
import me.tbsten.cream.ksp.testing.poet.dataClass
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

private val copyTargetSimpleName = MemberName("me.tbsten.cream", "CopyTargetSimpleName")

private fun literal(name: String): CodeBlock = CodeBlock.of("%S", name)

private fun prefixedToken(prefix: String): CodeBlock = CodeBlock.of("%S + %M", prefix, copyTargetSimpleName)

private fun twoTargets(funName: CodeBlock): SnapshotScenario =
    SnapshotScenario(
        dataClass("SourceA", Prop("name"))
            .withCombineTo(classNameOf("TargetA"), classNameOf("TargetB"), funName = funName),
        dataClass("SourceB", Prop("extra", INT))
            .withCombineTo(classNameOf("TargetA"), classNameOf("TargetB"), funName = funName),
        dataClass("TargetA", Prop("name"), Prop("extra", INT)),
        dataClass("TargetB", Prop("name"), Prop("extra", INT)),
    )

private fun singleTarget(funName: CodeBlock): SnapshotScenario =
    combinedInto(
        dataClass("Target", Prop("name"), Prop("extra", INT)),
        dataClass("SourceA", Prop("name")),
        dataClass("SourceB", Prop("extra", INT)),
        funName = funName,
    )

internal fun funNameScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        "literalFunName" to singleTarget(literal("toTargetState")),
        "tokenFunName" to singleTarget(prefixedToken("to")),
        "tokenFunNameMultiTarget" to twoTargets(prefixedToken("to")),
        "literalFunNameMultiTargetRejected" to twoTargets(literal("toTarget")),
    )
