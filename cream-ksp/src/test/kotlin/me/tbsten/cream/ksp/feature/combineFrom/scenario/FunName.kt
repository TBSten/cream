package me.tbsten.cream.ksp.feature.combineFrom.scenario

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.MemberName
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.dataClass
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

private val copyTargetSimpleName = MemberName("me.tbsten.cream", "CopyTargetSimpleName")

private fun literal(name: String): CodeBlock = CodeBlock.of("%S", name)

private fun prefixedToken(prefix: String): CodeBlock = CodeBlock.of("%S + %M", prefix, copyTargetSimpleName)

internal fun funNameScenarios(): Generator<SnapshotScenario> {
    val target = dataClass("Target", Prop("name"), Prop("extra", INT))
    val sourceA = dataClass("SourceA", Prop("name"))
    val sourceB = dataClass("SourceB", Prop("extra", INT))
    return Generator.snapshotScenarios(
        "literalFunName" to combineFrom(target, sourceA, sourceB, funName = literal("toTargetState")),
        "tokenFunName" to combineFrom(target, sourceA, sourceB, funName = prefixedToken("to")),
    )
}
