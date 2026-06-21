package me.tbsten.cream.ksp.testing.generator.clazz

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.generator.util.combine
import me.tbsten.cream.ksp.testing.generator.util.combineToList
import me.tbsten.cream.ksp.testing.generator.util.constant

private val ENUM_CONSTANTS = listOf("A", "B")

/**
 * A generator of enum declarations as a built [TypeSpec] (mirrors [TypeSpec.enumBuilder]) with a
 * fixed set of constants `A`, `B`. Constructors and body [properties] follow the same model as
 * [classSpec]; each constant passes `TODO()` for every primary-constructor parameter.
 */
internal fun Generator.Companion.enumSpec(
    name: Generator<String>,
    constructors: Generator<List<FunSpec>> = Generator.constant(emptyList()),
    properties: Generator<List<PropertySpec>> = Generator.constant(emptyList()),
    functions: List<Generator<FunSpec>> = emptyList(),
): Generator<TypeSpec> =
    combine(name, constructors, properties, functions.combineToList()) { enumName, ctors, props, funs ->
        TypeSpec
            .enumBuilder(enumName)
            .withMembers(ctors, props, funs, secondaryConstructorModifiers = listOf(KModifier.PRIVATE))
            .also { builder ->
                val primaryParamCount = ctors.firstOrNull()?.parameters?.size ?: 0
                ENUM_CONSTANTS.forEach { constant ->
                    if (primaryParamCount == 0) {
                        builder.addEnumConstant(constant)
                    } else {
                        builder.addEnumConstant(constant, enumConstant(primaryParamCount))
                    }
                }
            }.build()
    }

private fun enumConstant(parameterCount: Int): TypeSpec =
    TypeSpec
        .anonymousClassBuilder()
        .apply { repeat(parameterCount) { addSuperclassConstructorParameter("TODO()") } }
        .build()
