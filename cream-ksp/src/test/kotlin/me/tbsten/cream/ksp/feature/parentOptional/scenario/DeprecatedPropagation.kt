package me.tbsten.cream.ksp.feature.parentOptional.scenario

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeSpec
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.classNameOf
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

/**
 * `@Deprecated` on a merged child class / property is propagated onto the generated accessor
 * (issue #103 precedent for copy functions): callers reading the deprecated state through the
 * accessor keep seeing the deprecation, and a `DeprecationLevel.ERROR` source stays compilable
 * inside the equally-deprecated accessor.
 */
private fun deprecatedAnnotation(
    message: String,
    level: String? = null,
): AnnotationSpec =
    AnnotationSpec
        .builder(Deprecated::class)
        .addMember("%S", message)
        .apply { if (level != null) addMember("level = %T.%L", DeprecationLevel::class, level) }
        .build()

/** A child whose `@ParentOptional` constructor `val` also carries [propertyDeprecation]. */
private fun childWithDeprecatedProp(
    name: String,
    propName: String,
    propertyDeprecation: AnnotationSpec? = null,
    classDeprecation: AnnotationSpec? = null,
): TypeSpec =
    TypeSpec
        .classBuilder(name)
        .apply { classDeprecation?.let { addAnnotation(it) } }
        .addSuperinterface(classNameOf("Source"))
        .primaryConstructor(
            FunSpec
                .constructorBuilder()
                .addParameter(
                    ParameterSpec
                        .builder(propName, STRING)
                        .addAnnotation(parentOptional())
                        .apply { propertyDeprecation?.let { addAnnotation(it) } }
                        .build(),
                ).build(),
        ).addProperty(PropertySpec.builder(propName, STRING).initializer(propName).build())
        .build()

private fun deprecatedScenario(vararg children: TypeSpec): SnapshotScenario =
    SnapshotScenario(
        sealedInterfaceParent(
            "Source",
            children = children.toList() + objectChild("Empty", classNameOf("Source")),
        ),
    )

internal fun deprecatedScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        "deprecatedProperty" to
            deprecatedScenario(
                childWithDeprecatedProp("Child", "old", propertyDeprecation = deprecatedAnnotation("use new instead")),
            ),
        "deprecatedChildClass" to
            deprecatedScenario(
                childWithDeprecatedProp("Legacy", "value", classDeprecation = deprecatedAnnotation("legacy state")),
            ),
        "deprecatedLevelError" to
            deprecatedScenario(
                childWithDeprecatedProp("Child", "old", propertyDeprecation = deprecatedAnnotation("gone", level = "ERROR")),
            ),
        // Merged accessor: the first deprecation in branch order wins (Child1 is not deprecated,
        // Child2's property is — its message lands on the shared accessor).
        "mergedFirstDeprecationWins" to
            deprecatedScenario(
                childWithDeprecatedProp("Child1", "message"),
                childWithDeprecatedProp("Child2", "message", propertyDeprecation = deprecatedAnnotation("second contributor deprecated")),
            ),
    )
