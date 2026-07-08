package me.tbsten.cream.ksp.feature.childOptionals.scenario

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.KModifier.SEALED
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeSpec
import me.tbsten.cream.ParentOptional
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.classNameOf
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

private fun parentOptionalAnnotation(propertyName: String? = null): AnnotationSpec =
    AnnotationSpec
        .builder(ParentOptional::class)
        .apply {
            if (propertyName != null) addMember("%L = %S", ParentOptional::propertyName.name, propertyName)
        }.build()

/**
 * A transitive intermediate sealed type (`Middle`) declaring its own `@ParentOptional`
 * property: not visible on the swept parent and declared by no leaf, so only the
 * intermediate-type sweep can pick it up — with a single `is Middle` branch covering
 * every leaf below it. The leaf's own property keeps being swept as usual.
 */
private fun intermediateOwnPropertyScenario(): SnapshotScenario {
    val middle =
        TypeSpec
            .classBuilder("Middle")
            .addModifiers(SEALED)
            .addSuperinterface(classNameOf("Source"))
            .primaryConstructor(
                FunSpec
                    .constructorBuilder()
                    .addParameter(
                        ParameterSpec
                            .builder("session", STRING)
                            .addAnnotation(parentOptionalAnnotation())
                            .build(),
                    ).build(),
            ).addProperty(PropertySpec.builder("session", STRING).initializer("session").build())
            .build()
    val leaf =
        TypeSpec
            .classBuilder("Leaf")
            .superclass(classNameOf("Middle"))
            .addSuperclassConstructorParameter("session")
            .primaryConstructor(
                FunSpec
                    .constructorBuilder()
                    .addParameter("session", STRING)
                    .addParameter("extra", INT)
                    .build(),
            ).addProperty(PropertySpec.builder("extra", INT).initializer("extra").build())
            .build()
    return childOptionals(
        sealedInterfaceParent("Source", children = listOf(objectChild("Other", classNameOf("Source")))),
        middle,
        leaf,
    )
}

internal fun parentOptionalInteropScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        "parentOptionalRenameRespected" to
            childOptionals(
                sealedInterfaceParent(
                    "Source",
                    children =
                        listOf(
                            childClass(
                                "Success",
                                classNameOf("Source"),
                                props = listOf(Prop("data", paramAnnotation = parentOptionalAnnotation(propertyName = "dataOrNull"))),
                            ),
                            objectChild("Loading", classNameOf("Source")),
                        ),
                ),
            ),
        "ownershipSplitAcrossAncestors" to
            childOptionals(
                sealedInterfaceParent(
                    "Root",
                    children =
                        listOf(
                            nestedSealed(
                                "Middle",
                                classNameOf("Root"),
                                childClass(
                                    "Leaf",
                                    classNameOf("Root", "Middle"),
                                    props = listOf(Prop("data", paramAnnotation = parentOptionalAnnotation())),
                                ),
                            ),
                            objectChild("Other", classNameOf("Root")),
                        ),
                ),
            ),
        // An override of a parent-visible property normally gets no accessor, but an explicit
        // @ParentOptional rename must generate the renamed accessor instead of dropping silently.
        "overrideRenameOnParentVisibleProperty" to
            childOptionals(
                sealedInterfaceParent(
                    "Source",
                    abstractProps = listOf(Prop("data")),
                    children =
                        listOf(
                            childClass(
                                "Success",
                                classNameOf("Source"),
                                overrides =
                                    listOf(
                                        Prop("data", paramAnnotation = parentOptionalAnnotation(propertyName = "dataOrNull")),
                                    ),
                            ),
                            childClass("Failure", classNameOf("Source"), overrides = listOf(Prop("data"))),
                        ),
                ),
            ),
        "intermediateSealedOwnProperty" to intermediateOwnPropertyScenario(),
    )
