package me.tbsten.cream.ksp.testing.generator.clazz

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.generator.util.combine
import me.tbsten.cream.ksp.testing.generator.util.combineToList

/**
 * A generator of interface declarations as a built [TypeSpec] (mirrors [TypeSpec.interfaceBuilder]).
 * Each property becomes an abstract `val`; each function keeps its body as a default method.
 */
internal fun Generator.Companion.interfaceSpec(
    name: Generator<String>,
    properties: Generator<List<PropertySpec>> = Generator.properties(),
    functions: List<Generator<FunSpec>> = emptyList(),
): Generator<TypeSpec> =
    combine(name, properties, functions.combineToList()) { interfaceName, props, funs ->
        TypeSpec
            .interfaceBuilder(interfaceName)
            .apply { props.forEach { addProperty(it) } }
            .addFunctions(funs)
            .addDerivedTypeVariables(props.map { it.type })
            .build()
    }
