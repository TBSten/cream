package me.tbsten.cream.ksp.testing.generator.clazz

import com.squareup.kotlinpoet.CHAR_SEQUENCE
import com.squareup.kotlinpoet.COMPARABLE
import com.squareup.kotlinpoet.LIST
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.MAP
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeVariableName
import io.kotest.property.Arb
import io.kotest.property.arbitrary.constant
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.generator.GeneratorBuilder
import me.tbsten.cream.ksp.testing.generator.generator

/**
 * A generator of property lists that reference type variables (`T`, `K`/`V`, …), so a class or
 * interface built from them becomes generic (its type parameters are derived from the member types
 * via [addDerivedTypeVariables]). Extra cases can be contributed via [builder], mirroring
 * [Generator.properties].
 */
internal fun Generator.Companion.typeParameters(builder: GeneratorBuilder<List<PropertySpec>>.() -> Unit = {}): Generator<List<PropertySpec>> =
    generator {
        builder()
        val t = TypeVariableName("T")
        "Single type parameter" case
            listOf(
                propertyOf("single", t),
                propertyOf("items", LIST.parameterizedBy(t)),
                propertyOf("produce", LambdaTypeName.get(returnType = t)),
            )
        val k = TypeVariableName("K")
        val v = TypeVariableName("V")
        "Two type parameters" case
            listOf(
                propertyOf("key", k),
                propertyOf("mapped", v),
                propertyOf("pairs", MAP.parameterizedBy(k, v)),
                propertyOf("convert", LambdaTypeName.get(parameters = listOf(ParameterSpec.unnamed(k)), returnType = v)),
            )
        val boundedT = TypeVariableName("T", COMPARABLE.parameterizedBy(TypeVariableName("T")), CHAR_SEQUENCE)
        "Multiple bounds" case
            listOf(
                propertyOf("current", boundedT),
                propertyOf("currentItems", LIST.parameterizedBy(boundedT)),
            )

        Arb.constant(listOf(propertyOf("single", t), propertyOf("items", LIST.parameterizedBy(t))))
    }

private fun propertyOf(
    name: String,
    type: TypeName,
): PropertySpec = PropertySpec.builder(name, type).build()
