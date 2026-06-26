package me.tbsten.cream.ksp.feature.copyFrom.scenario

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier.DATA
import com.squareup.kotlinpoet.KModifier.OVERRIDE
import com.squareup.kotlinpoet.KModifier.SEALED
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeSpec
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.classNameOf
import me.tbsten.cream.ksp.testing.poet.dataClass
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

private val copyTargetSimpleName = MemberName("me.tbsten.cream", "CopyTargetSimpleName")

private fun literal(name: String): CodeBlock = CodeBlock.of("%S", name)

private fun prefixedToken(prefix: String): CodeBlock = CodeBlock.of("%S + %M", prefix, copyTargetSimpleName)

private fun sealedTarget(): TypeSpec {
    fun leaf(name: String): TypeSpec =
        TypeSpec
            .classBuilder(name)
            .addModifiers(DATA)
            .addSuperinterface(classNameOf("Target"))
            .primaryConstructor(FunSpec.constructorBuilder().addParameter("name", STRING).build())
            .addProperty(
                PropertySpec
                    .builder("name", STRING)
                    .addModifiers(OVERRIDE)
                    .initializer("name")
                    .build(),
            ).build()
    return TypeSpec
        .interfaceBuilder("Target")
        .addModifiers(SEALED)
        .addProperty(PropertySpec.builder("name", STRING).build())
        .addType(leaf("First"))
        .addType(leaf("Second"))
        .build()
}

internal fun funNameScenarios(): Generator<SnapshotScenario> {
    val source = dataClass("Source", Prop("name"))
    return Generator.snapshotScenarios(
        "literalFunName" to copyFrom(dataClass("Target", Prop("name")), source, funName = literal("toState")),
        "tokenFunName" to copyFrom(dataClass("Target", Prop("name")), source, funName = prefixedToken("to")),
        "literalFunNameSealedRejected" to copyFrom(sealedTarget(), source, funName = literal("toState")),
    )
}
