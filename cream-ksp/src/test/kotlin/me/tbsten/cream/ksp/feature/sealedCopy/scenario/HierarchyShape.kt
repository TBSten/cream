package me.tbsten.cream.ksp.feature.sealedCopy.scenario

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.KModifier.OVERRIDE
import com.squareup.kotlinpoet.KModifier.SEALED
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.classNameOf
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

private fun nestedBranch(
    name: String,
    parent: ClassName,
    vararg leaves: TypeSpec,
): TypeSpec =
    TypeSpec
        .interfaceBuilder(name)
        .addModifiers(SEALED)
        .addSuperinterface(parent)
        .apply { leaves.forEach { addType(it) } }
        .build()

private fun copyableNonDataChild(
    parent: ClassName,
    self: ClassName,
    prop: Prop,
): TypeSpec {
    val copyFun =
        FunSpec
            .builder("copy")
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
        ).addFunction(copyFun)
        .build()
}

internal fun hierarchyShapeScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        "dataClassChildren" to
            sealedCopy(
                sealedInterfaceParent(
                    "Source",
                    abstractProps = listOf(Prop("value")),
                    children =
                        listOf(
                            childClass("Loading", classNameOf("Source"), overrides = listOf(Prop("value"))),
                            childClass("Success", classNameOf("Source"), overrides = listOf(Prop("value")), extras = listOf(Prop("count", INT))),
                        ),
                ),
            ),
        "transitiveNestedSealed" to
            sealedCopy(
                sealedInterfaceParent(
                    "Source",
                    abstractProps = listOf(Prop("id")),
                    children =
                        listOf(
                            childClass("Loading", classNameOf("Source"), overrides = listOf(Prop("id"))),
                            nestedBranch(
                                "Branch",
                                classNameOf("Source"),
                                childClass("Done", classNameOf("Source", "Branch"), overrides = listOf(Prop("id")), extras = listOf(Prop("note"))),
                                childClass("Pending", classNameOf("Source", "Branch"), overrides = listOf(Prop("id"))),
                            ),
                        ),
                ),
            ),
        "noAbstractProperties" to
            sealedCopy(
                sealedInterfaceParent(
                    "Source",
                    children =
                        listOf(
                            childClass("Loading", classNameOf("Source"), extras = listOf(Prop("a"))),
                            childClass("Success", classNameOf("Source"), extras = listOf(Prop("a"), Prop("b", INT))),
                        ),
                ),
            ),
        "nonDataClassWithCopyMember" to
            sealedCopy(
                sealedInterfaceParent(
                    "Source",
                    abstractProps = listOf(Prop("value")),
                    children =
                        listOf(
                            childClass("Loading", classNameOf("Source"), overrides = listOf(Prop("value"))),
                            copyableNonDataChild(classNameOf("Source"), classNameOf("Source", "Manual"), Prop("value")),
                        ),
                ),
            ),
    )
