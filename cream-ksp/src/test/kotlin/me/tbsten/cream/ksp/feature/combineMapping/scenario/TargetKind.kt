package me.tbsten.cream.ksp.feature.combineMapping.scenario

import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.KModifier.ABSTRACT
import com.squareup.kotlinpoet.KModifier.PRIVATE
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeSpec
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.asInner
import me.tbsten.cream.ksp.testing.poet.classNameOf
import me.tbsten.cream.ksp.testing.poet.classWithNested
import me.tbsten.cream.ksp.testing.poet.clazz
import me.tbsten.cream.ksp.testing.poet.dataClass
import me.tbsten.cream.ksp.testing.poet.sealedInterface
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

private val sourceA = dataClass("SourceA", Prop("name"))
private val sourceB = dataClass("SourceB", Prop("extra", INT))

private fun toTarget(target: TypeSpec): SnapshotScenario = combineMapping(mappingHolder(), listOf(sourceA, sourceB), target)

internal fun targetKindScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        "objectTarget" to toTarget(TypeSpec.objectBuilder("Target").build()),
        "sealedInterfaceTarget" to toTarget(sealedInterface("Target", "name")),
        // TODO(#132): combine reuses appendCombineToFunction, which lacks copy's concreteClassRejection, so
        // abstract/inner/privateConstructor targets emit uncompilable generated code (error on the generated
        // file) instead of a clean cream rejection. These goldens freeze that known bug until #132 is fixed.
        "abstractTarget" to toTarget(clazz("Target", Prop("name"), Prop("extra", INT), modifiers = listOf(ABSTRACT))),
        "nonSealedInterfaceTarget" to
            toTarget(TypeSpec.interfaceBuilder("Target").addProperty(PropertySpec.builder("name", STRING).build()).build()),
        "enumTarget" to
            toTarget(
                TypeSpec
                    .enumBuilder("Target")
                    .addEnumConstant("A")
                    .addEnumConstant("B")
                    .build(),
            ),
        "innerTarget" to
            SnapshotScenario(
                listOf(
                    mappingHolder().withCombineMapping(
                        listOf(classNameOf("SourceA"), classNameOf("SourceB")),
                        classNameOf("Outer", "Target"),
                    ),
                    sourceA,
                    sourceB,
                    classWithNested("Outer", clazz("Target", Prop("name"), Prop("extra", INT)).asInner()),
                ),
            ),
        "privateConstructorTarget" to toTarget(clazz("Target", Prop("name"), Prop("extra", INT), constructorVisibility = PRIVATE)),
    )
