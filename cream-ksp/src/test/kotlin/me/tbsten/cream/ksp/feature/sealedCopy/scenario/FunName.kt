package me.tbsten.cream.ksp.feature.sealedCopy.scenario

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.MemberName
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.classNameOf
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

private val copyTargetSimpleName = MemberName("me.tbsten.cream", "CopyTargetSimpleName")

private fun literal(name: String): CodeBlock = CodeBlock.of("%S", name)

private fun prefixedToken(prefix: String): CodeBlock = CodeBlock.of("%S + %M", prefix, copyTargetSimpleName)

internal fun funNameScenarios(): Generator<SnapshotScenario> {
    val parent =
        sealedInterfaceParent(
            "Source",
            abstractProps = listOf(Prop("name")),
            children = listOf(childClass("Child", classNameOf("Source"), overrides = listOf(Prop("name")))),
        )
    return Generator.snapshotScenarios(
        "literalFunName" to sealedCopy(parent, funName = literal("withUpdated")),
        "tokenFunName" to sealedCopy(parent, funName = prefixedToken("to")),
    )
}
