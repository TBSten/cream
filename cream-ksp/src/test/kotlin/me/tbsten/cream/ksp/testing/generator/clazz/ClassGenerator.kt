package me.tbsten.cream.ksp.testing.generator.clazz

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.generator.util.combine
import me.tbsten.cream.ksp.testing.generator.util.combineToList
import me.tbsten.cream.ksp.testing.generator.util.constant
import me.tbsten.cream.ksp.testing.generator.util.map

/**
 * A generator of class declarations as a built [TypeSpec] (mirrors [TypeSpec.classBuilder]).
 *
 * Constructors are first-class because cream emits a copy function per constructor: [constructors]
 * yields the class's constructor [FunSpec]s. `constructors[0]` is the primary constructor (its
 * parameters are turned into `val` properties — FunSpec carries no `val` info, so [withPrimaryConstructor]
 * adds it); the rest are secondary constructors (the builder appends `: this(TODO(), ...)` so they always
 * compile — cream only reads their parameter signatures). [properties] are genuine body `val`s (not
 * constructor parameters) and default to empty so they don't clash with the primary constructor. Use
 * [asPrimaryConstructor] to lift a property list into a single primary constructor.
 *
 * Callers must keep parameter / property names distinct (and secondary signatures distinct from each
 * other and the primary) to stay valid.
 */
internal fun Generator.Companion.classSpec(
    name: Generator<String>,
    constructors: Generator<List<FunSpec>> = Generator.properties().asPrimaryConstructor(),
    properties: Generator<List<PropertySpec>> = Generator.constant(emptyList()),
    functions: List<Generator<FunSpec>> = emptyList(),
): Generator<TypeSpec> =
    combine(name, constructors, properties, functions.combineToList()) { className, ctors, props, funs ->
        TypeSpec
            .classBuilder(className)
            .withMembers(ctors, props, funs)
            .addDerivedTypeVariables(ctors.flatMap { ctor -> ctor.parameters.map { it.type } } + props.map { it.type })
            .build()
    }

/**
 * Shorthand for the common case: a `data class` whose primary-constructor parameters are
 * [properties]. Equivalent to [classSpec] with a single primary constructor plus `KModifier.DATA`.
 */
internal fun Generator.Companion.dataClassSpec(
    name: Generator<String>,
    properties: Generator<List<PropertySpec>> = Generator.properties(),
    functions: List<Generator<FunSpec>> = emptyList(),
): Generator<TypeSpec> =
    classSpec(
        name = name,
        constructors = properties.asPrimaryConstructor(),
        functions = functions,
    ).map { it.toBuilder().addModifiers(KModifier.DATA).build() }

/**
 * A generator of object declarations as a built [TypeSpec] (mirrors [TypeSpec.objectBuilder]). Each
 * property becomes a body `val` initialised with `TODO()`.
 */
internal fun Generator.Companion.objectSpec(
    name: Generator<String>,
    properties: Generator<List<PropertySpec>> = Generator.properties(),
    functions: List<Generator<FunSpec>> = emptyList(),
): Generator<TypeSpec> =
    combine(name, properties, functions.combineToList()) { objectName, props, funs ->
        TypeSpec
            .objectBuilder(objectName)
            .withBodyProperties(props)
            .addFunctions(funs)
            .build()
    }

/**
 * Lifts a property-list generator into a single primary-constructor generator: each property becomes a
 * constructor parameter (and, via [classSpec], a `val`). The common bridge from [properties] /
 * [Generator.properties] to a [classSpec] / [enumSpec] `constructors` argument.
 */
internal fun Generator<List<PropertySpec>>.asPrimaryConstructor(): Generator<List<FunSpec>> =
    map { props ->
        listOf(
            FunSpec
                .constructorBuilder()
                .apply { props.forEach { addParameter(ParameterSpec.builder(it.name, it.type).build()) } }
                .build(),
        )
    }

internal fun TypeSpec.Builder.withMembers(
    constructors: List<FunSpec>,
    bodyProperties: List<PropertySpec>,
    functions: List<FunSpec>,
    secondaryConstructorModifiers: List<KModifier> = emptyList(),
): TypeSpec.Builder {
    constructors.firstOrNull()?.let { primary ->
        withPrimaryConstructor(primary)
        constructors.drop(1).forEach { addFunction(it.asSecondaryConstructor(primary.parameters.size, secondaryConstructorModifiers)) }
    }
    withBodyProperties(bodyProperties)
    addFunctions(functions)
    return this
}

private fun TypeSpec.Builder.withPrimaryConstructor(constructor: FunSpec): TypeSpec.Builder {
    primaryConstructor(constructor)
    constructor.parameters.forEach { addProperty(PropertySpec.builder(it.name, it.type).initializer("%N", it.name).build()) }
    return this
}

private fun FunSpec.asSecondaryConstructor(
    primaryParamCount: Int,
    modifiers: List<KModifier>,
): FunSpec =
    toBuilder()
        .addModifiers(modifiers)
        .callThisConstructor(*Array(primaryParamCount) { "TODO()" })
        .build()

private fun TypeSpec.Builder.withBodyProperties(properties: List<PropertySpec>): TypeSpec.Builder =
    apply { properties.forEach { addProperty(it.toBuilder().initializer("TODO()").build()) } }
