package me.tbsten.cream.ksp.feature.parentOptional.scenario

import com.squareup.kotlinpoet.BOOLEAN
import com.squareup.kotlinpoet.DOUBLE
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.KModifier.LATEINIT
import com.squareup.kotlinpoet.LIST
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeAliasSpec
import com.squareup.kotlinpoet.TypeSpec
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.generator.clazz.GENERATED_PACKAGE
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.classNameOf
import me.tbsten.cream.ksp.testing.poet.dataClass
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

private fun singleChildScenario(vararg props: Prop): SnapshotScenario =
    SnapshotScenario(
        sealedInterfaceParent(
            "Source",
            children =
                listOf(
                    childClass("Child", classNameOf("Source"), props = props.toList()),
                    objectChild("Empty", classNameOf("Source")),
                ),
        ),
    )

/** A `Child` with `@ParentOptional` **body** properties instead of constructor `val`s. */
private fun bodyPropChildScenario(vararg bodyProps: PropertySpec): SnapshotScenario =
    SnapshotScenario(
        sealedInterfaceParent(
            "Source",
            children =
                listOf(
                    TypeSpec
                        .classBuilder("Child")
                        .addSuperinterface(classNameOf("Source"))
                        .apply { bodyProps.forEach { addProperty(it) } }
                        .build(),
                    objectChild("Empty", classNameOf("Source")),
                ),
        ),
    )

/**
 * `typealias UserId = String` as the property type: the alias must be *preserved* in the
 * generated signature (not expanded to `String`) — see the merge-mismatch counterpart in
 * `ParentOptionalInvalidUsageTest.mergedTypealiasVsExpandedType`.
 */
private fun typealiasScenario(): SnapshotScenario =
    SnapshotScenario(
        listOf(
            FileSpec
                .builder(GENERATED_PACKAGE, "$GENERATED_PACKAGE.Source")
                .addTypeAlias(TypeAliasSpec.builder("UserId", STRING).build())
                .addType(
                    sealedInterfaceParent(
                        "Source",
                        children =
                            listOf(
                                childClass("Child", classNameOf("Source"), props = listOf(parentOptionalProp("id", classNameOf("UserId")))),
                                objectChild("Empty", classNameOf("Source")),
                            ),
                    ),
                ).build(),
        ),
    )

/** A `data object` child declaring its own `@ParentOptional` body property. */
private fun objectChildPropertyScenario(): SnapshotScenario =
    SnapshotScenario(
        sealedInterfaceParent(
            "Source",
            children =
                listOf(
                    TypeSpec
                        .objectBuilder("Fixed")
                        .addSuperinterface(classNameOf("Source"))
                        .addProperty(
                            PropertySpec
                                .builder("note", STRING)
                                .addAnnotation(parentOptional())
                                .initializer("%S", "memo")
                                .build(),
                        ).build(),
                    objectChild("Empty", classNameOf("Source")),
                ),
        ),
    )

internal fun propertyShapeScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        "mixedPrimitives" to
            singleChildScenario(
                parentOptionalProp("name"),
                parentOptionalProp("age", INT),
                parentOptionalProp("active", BOOLEAN),
                parentOptionalProp("score", DOUBLE),
            ),
        "nullableProp" to singleChildScenario(parentOptionalProp("name", STRING.copy(nullable = true))),
        "collectionProp" to singleChildScenario(parentOptionalProp("tags", LIST.parameterizedBy(STRING))),
        "customTypeProp" to
            SnapshotScenario(
                sealedInterfaceParent(
                    "Source",
                    children = listOf(childClass("Child", classNameOf("Source"), props = listOf(parentOptionalProp("data", classNameOf("Data"))))),
                ),
                dataClass("Data", Prop("value")),
            ),
        "typealiasPreserved" to typealiasScenario(),
        "lateinitVarProp" to
            bodyPropChildScenario(
                PropertySpec
                    .builder("token", STRING)
                    .mutable()
                    .addModifiers(LATEINIT)
                    .addAnnotation(parentOptional())
                    .build(),
            ),
        "delegatedProp" to
            bodyPropChildScenario(
                PropertySpec
                    .builder("lazyValue", INT)
                    .delegate("lazy { 42 }")
                    .addAnnotation(parentOptional())
                    .build(),
            ),
        "objectChildProperty" to objectChildPropertyScenario(),
        "hardKeywordPropertyName" to singleChildScenario(parentOptionalProp("object")),
    )
