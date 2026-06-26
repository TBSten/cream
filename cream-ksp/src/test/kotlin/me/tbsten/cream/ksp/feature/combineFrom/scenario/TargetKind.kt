package me.tbsten.cream.ksp.feature.combineFrom.scenario

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

internal fun targetKindScenarios(): Generator<SnapshotScenario> {
    val source = dataClass("Source", Prop("name"))
    return Generator.snapshotScenarios(
        "objectTarget" to combineFrom(TypeSpec.objectBuilder("Target").build(), source),
        "sealedInterfaceTarget" to combineFrom(sealedInterface("Target", "name"), source),
        // TODO(#132): combine lacks copy's concreteClassRejection, so abstract/inner/privateConstructor
        // targets emit uncompilable generated code (error on the generated file) instead of a clean
        // cream rejection. These goldens freeze that known bug until #132 is fixed.
        "abstractTarget" to combineFrom(clazz("Target", Prop("name"), Prop("extra", INT), modifiers = listOf(ABSTRACT)), source),
        "nonSealedInterfaceTarget" to
            combineFrom(TypeSpec.interfaceBuilder("Target").addProperty(PropertySpec.builder("name", STRING).build()).build(), source),
        "enumTarget" to
            combineFrom(
                TypeSpec
                    .enumBuilder("Target")
                    .addEnumConstant("A")
                    .addEnumConstant("B")
                    .build(),
                source,
            ),
        "innerTarget" to
            SnapshotScenario(
                classWithNested("Outer", clazz("Target", Prop("name")).asInner().withCombineFrom(classNameOf("Source"))),
                source,
            ),
        "privateConstructorTarget" to
            combineFrom(clazz("Target", Prop("name"), Prop("extra", INT), constructorVisibility = PRIVATE), source),
    )
}
