package me.tbsten.cream.ksp.feature.parentOptional.scenario

import com.squareup.kotlinpoet.CHAR_SEQUENCE
import com.squareup.kotlinpoet.COMPARABLE
import com.squareup.kotlinpoet.KModifier.DATA
import com.squareup.kotlinpoet.KModifier.SEALED
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.TypeVariableName
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.classNameOf
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

private fun pinnedHierarchy(
    parentParam: TypeVariableName,
    childParam: TypeVariableName,
): TypeSpec =
    sealedInterfaceParent(
        "Source",
        typeVariables = listOf(parentParam),
        children =
            listOf(
                childClass(
                    "Filled",
                    classNameOf("Source").parameterizedBy(childParam),
                    props = listOf(parentOptionalProp("item", childParam)),
                    typeVariables = listOf(childParam),
                ),
            ),
    )

private fun chainedHierarchy(): List<TypeSpec> {
    val t = TypeVariableName("T")
    val e = TypeVariableName("E")
    val x = TypeVariableName("X")
    val root =
        TypeSpec
            .interfaceBuilder("Root")
            .addModifiers(SEALED)
            .addTypeVariable(t)
            .build()
    val middle =
        TypeSpec
            .interfaceBuilder("Middle")
            .addModifiers(SEALED)
            .addTypeVariable(e)
            .addSuperinterface(classNameOf("Root").parameterizedBy(e))
            .build()
    val leaf =
        childClass(
            "Leaf",
            classNameOf("Middle").parameterizedBy(x),
            props = listOf(parentOptionalProp("item", x)),
            modifiers = listOf(DATA),
            typeVariables = listOf(x),
        )
    return listOf(root, middle, leaf)
}

/**
 * `Root` / `Mid` are non-generic, but the leaf is generic and its annotated property does NOT
 * reference the leaf's type parameter. The `Root` accessor cannot see `Mid`'s pinning info
 * (`Root` is not a direct supertype of `Leaf`), so its `is` branch must star-project:
 * `is Leaf<*>` — a bare `is Leaf` would not compile ("one type argument expected").
 */
private fun genericLeafUnderIndirectAncestorHierarchy(): List<TypeSpec> {
    val t = TypeVariableName("T")
    val root =
        TypeSpec
            .interfaceBuilder("Root")
            .addModifiers(SEALED)
            .build()
    val mid =
        TypeSpec
            .interfaceBuilder("Mid")
            .addModifiers(SEALED)
            .addSuperinterface(classNameOf("Root"))
            .build()
    val leaf =
        childClass(
            "Leaf",
            classNameOf("Mid"),
            props = listOf(parentOptionalProp("label"), Prop("value", t)),
            typeVariables = listOf(t),
        )
    return listOf(root, mid, leaf)
}

internal fun genericsScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        "directPinnedTypeParam" to SnapshotScenario(pinnedHierarchy(TypeVariableName("T"), TypeVariableName("E"))),
        "boundedTypeParam" to
            SnapshotScenario(
                pinnedHierarchy(
                    TypeVariableName("T", COMPARABLE.parameterizedBy(TypeVariableName("T"))),
                    TypeVariableName("E", COMPARABLE.parameterizedBy(TypeVariableName("E"))),
                ),
            ),
        // A type parameter with TWO upper bounds cannot be declared inline on the generated
        // accessor — it needs a `where` clause on the extension *property* (valid Kotlin, but
        // easy to regress), so this pins the rendered `where T : Comparable<T>, T : CharSequence`.
        "multiBoundWhereClause" to
            SnapshotScenario(
                pinnedHierarchy(
                    TypeVariableName("T", COMPARABLE.parameterizedBy(TypeVariableName("T")), CHAR_SEQUENCE),
                    TypeVariableName("E", COMPARABLE.parameterizedBy(TypeVariableName("E")), CHAR_SEQUENCE),
                ),
            ),
        "chainedTypeParamRejected" to SnapshotScenario(chainedHierarchy()),
        "genericLeafUnderIndirectAncestor" to SnapshotScenario(genericLeafUnderIndirectAncestorHierarchy()),
    )
