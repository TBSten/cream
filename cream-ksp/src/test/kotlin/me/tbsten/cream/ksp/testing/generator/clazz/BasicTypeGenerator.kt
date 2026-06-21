package me.tbsten.cream.ksp.testing.generator.clazz

import com.squareup.kotlinpoet.BOOLEAN
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.LIST
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeName
import io.kotest.property.Arb
import io.kotest.property.arbitrary.of
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.generator.generator

/**
 * A generator of basic, built-in [TypeName]s usable as property / parameter / return types.
 *
 * "Basic" means representatives stay resolvable within a single compilation unit (no user-defined
 * types): `String`, `Int`, `Boolean`, `String?`, `List<String>`.
 */
internal fun Generator.Companion.basicType(): Generator<TypeName> =
    generator {
        "String" case STRING
        "Int" case INT
        "Boolean" case BOOLEAN
        "String?" case STRING.copy(nullable = true)
        "List<String>" case LIST.parameterizedBy(STRING)
        Arb.of(
            STRING,
            INT,
            BOOLEAN,
            STRING.copy(nullable = true),
            LIST.parameterizedBy(STRING),
        )
    }
