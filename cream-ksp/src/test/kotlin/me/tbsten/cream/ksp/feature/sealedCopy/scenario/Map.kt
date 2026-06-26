package me.tbsten.cream.ksp.feature.sealedCopy.scenario

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier.OVERRIDE
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import me.tbsten.cream.SealedCopy
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.classNameOf
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

private fun mappedChild(
    parent: ClassName,
    self: ClassName,
    prop: Prop,
): TypeSpec {
    val cloneWith =
        FunSpec
            .builder("cloneWith")
            .addAnnotation(AnnotationSpec.builder(SealedCopy.Map::class).build())
            .addParameter(ParameterSpec.builder(prop.name, prop.type).defaultValue("this.%N", prop.name).build())
            .returns(self)
            .addStatement("return %T(%N)", self, prop.name)
            .build()
    return TypeSpec
        .classBuilder(self.simpleName)
        .addSuperinterface(parent)
        .primaryConstructor(FunSpec.constructorBuilder().addParameter(prop.name, prop.type).build())
        .addProperty(
            PropertySpec
                .builder(prop.name, prop.type)
                .addModifiers(OVERRIDE)
                .initializer(prop.name)
                .build(),
        ).addFunction(cloneWith)
        .build()
}

internal fun mapScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        "mappedNonDataChild" to
            sealedCopy(
                sealedInterfaceParent(
                    "Source",
                    abstractProps = listOf(Prop("name")),
                    children = listOf(mappedChild(classNameOf("Source"), classNameOf("Source", "Custom"), Prop("name"))),
                ),
            ),
        "mappedAmongDataChildren" to
            sealedCopy(
                sealedInterfaceParent(
                    "Source",
                    abstractProps = listOf(Prop("name")),
                    children =
                        listOf(
                            childClass("Loading", classNameOf("Source"), overrides = listOf(Prop("name"))),
                            mappedChild(classNameOf("Source"), classNameOf("Source", "Custom"), Prop("name")),
                        ),
                ),
            ),
    )
