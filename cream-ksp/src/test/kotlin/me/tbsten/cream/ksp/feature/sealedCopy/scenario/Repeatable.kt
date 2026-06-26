package me.tbsten.cream.ksp.feature.sealedCopy.scenario

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.KModifier.OVERRIDE
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeSpec
import me.tbsten.cream.NonCopyableStrategy
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.classNameOf
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

private fun overridingObject(
    name: String,
    parent: ClassName,
): TypeSpec =
    TypeSpec
        .objectBuilder(name)
        .addSuperinterface(parent)
        .addProperty(
            PropertySpec
                .builder("name", STRING)
                .addModifiers(OVERRIDE)
                .initializer("%S", "empty")
                .build(),
        ).build()

internal fun repeatableScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        "stackedVariants" to
            SnapshotScenario(
                sealedInterfaceParent(
                    "Source",
                    abstractProps = listOf(Prop("name")),
                    children =
                        listOf(
                            childClass("Loading", classNameOf("Source"), overrides = listOf(Prop("name"))),
                            overridingObject("Empty", classNameOf("Source")),
                        ),
                ).withSealedCopy(nonCopyableStrategy = NonCopyableStrategy.RETURN_AS_IS, funName = CodeBlock.of("%S", "withUpdated"))
                    .withSealedCopy(nonCopyableStrategy = NonCopyableStrategy.RETURN_NULL, funName = CodeBlock.of("%S", "withUpdatedOrNull")),
            ),
        "duplicateFunNameRejected" to
            SnapshotScenario(
                sealedInterfaceParent(
                    "Source",
                    abstractProps = listOf(Prop("name")),
                    children = listOf(childClass("Loading", classNameOf("Source"), overrides = listOf(Prop("name")))),
                ).withSealedCopy(funName = CodeBlock.of("%S", "snapshot"))
                    .withSealedCopy(funName = CodeBlock.of("%S", "snapshot")),
            ),
    )
