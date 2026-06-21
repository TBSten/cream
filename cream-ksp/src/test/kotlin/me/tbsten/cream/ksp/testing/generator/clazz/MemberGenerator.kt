package me.tbsten.cream.ksp.testing.generator.clazz

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.generator.util.map

/**
 * A generator of member [PropertySpec]s with a fixed [name] and a varying [type].
 *
 * The returned spec carries no initializer / modifier wiring: whether it becomes a
 * primary-constructor `val` or a body property is decided by [clazz] depending on the class kind.
 */
internal fun Generator.Companion.property(
    name: String = "prop",
    type: Generator<TypeName> = basicType(),
): Generator<PropertySpec> =
    type.map { typeName ->
        PropertySpec.builder(name, typeName).build()
    }

/**
 * A generator of member [FunSpec]s with a fixed [name] and a varying [returnType].
 *
 * The body is a type-agnostic `TODO()`, so the function compiles for any return type but throws at
 * runtime. Suitable for codegen / snapshot inputs, not for tests that execute the generated code.
 */
internal fun Generator.Companion.function(
    name: String = "method",
    returnType: Generator<TypeName> = basicType(),
): Generator<FunSpec> =
    returnType.map { ret ->
        FunSpec
            .builder(name)
            .returns(ret)
            .addStatement("TODO()")
            .build()
    }
