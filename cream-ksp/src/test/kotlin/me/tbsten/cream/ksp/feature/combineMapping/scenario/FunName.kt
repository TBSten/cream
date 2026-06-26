package me.tbsten.cream.ksp.feature.combineMapping.scenario

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

private val sources = listOf(dataClass("SourceA", Prop("name")), dataClass("SourceB", Prop("extra", INT)))
private val target = dataClass("Target", Prop("name"), Prop("extra", INT))

internal fun funNameScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        "literalFunName" to combineMapping(mappingHolder(), sources, target, funName = literal("toCombinedModel")),
        "tokenFunName" to combineMapping(mappingHolder(), sources, target, funName = prefixedToken("to")),
    )
