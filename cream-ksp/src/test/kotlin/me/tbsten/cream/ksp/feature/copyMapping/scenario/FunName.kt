package me.tbsten.cream.ksp.feature.copyMapping.scenario

import com.squareup.kotlinpoet.CodeBlock
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
    val source = dataClass("Source", Prop("name"))
    val target = dataClass("Target", Prop("name"))
    return Generator.snapshotScenarios(
        "literalFunName" to copyMapping(mappingHolder(), source, target, funName = literal("toTargetModel")),
        "tokenFunName" to copyMapping(mappingHolder(), source, target, funName = prefixedToken("to")),
        "tokenFunNameReversible" to copyMapping(mappingHolder(), source, target, canReverse = true, funName = prefixedToken("to")),
        "literalFunNameReversibleRejected" to copyMapping(mappingHolder(), source, target, canReverse = true, funName = literal("toModel")),
    )
}
