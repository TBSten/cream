package me.tbsten.cream.ksp.testing.generator.clazz

import com.squareup.kotlinpoet.ANY
import com.squareup.kotlinpoet.ARRAY
import com.squareup.kotlinpoet.BOOLEAN
import com.squareup.kotlinpoet.BOOLEAN_ARRAY
import com.squareup.kotlinpoet.BYTE
import com.squareup.kotlinpoet.BYTE_ARRAY
import com.squareup.kotlinpoet.CHAR
import com.squareup.kotlinpoet.CHAR_ARRAY
import com.squareup.kotlinpoet.CHAR_SEQUENCE
import com.squareup.kotlinpoet.COLLECTION
import com.squareup.kotlinpoet.COMPARABLE
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.DOUBLE
import com.squareup.kotlinpoet.DOUBLE_ARRAY
import com.squareup.kotlinpoet.FLOAT
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.INT_ARRAY
import com.squareup.kotlinpoet.ITERABLE
import com.squareup.kotlinpoet.LIST
import com.squareup.kotlinpoet.LONG
import com.squareup.kotlinpoet.LONG_ARRAY
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.MAP
import com.squareup.kotlinpoet.MUTABLE_LIST
import com.squareup.kotlinpoet.MUTABLE_MAP
import com.squareup.kotlinpoet.NUMBER
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.SET
import com.squareup.kotlinpoet.SHORT
import com.squareup.kotlinpoet.SHORT_ARRAY
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.UNIT
import com.squareup.kotlinpoet.U_BYTE
import com.squareup.kotlinpoet.U_INT
import com.squareup.kotlinpoet.U_LONG
import com.squareup.kotlinpoet.U_SHORT
import com.squareup.kotlinpoet.WildcardTypeName
import io.kotest.property.Arb
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.map
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.generator.GeneratorBuilder
import me.tbsten.cream.ksp.testing.generator.generator

// 参考: https://kotlinlang.org/docs/types-overview.html 周辺。
// TODO(#127): エスケープが要る名前（backtick / 非ASCII）や source↔target の関係（マッチ/リネーム等）は別途。

/**
 * A generator of common property lists (analogous to the class-name generators), used as the default
 * for the `properties` argument of the builders.
 *
 * Representative values are deterministic property sets grouped by type category covering the main
 * shapes of the Kotlin type system (primitives, unsigned, special, collections incl. arrays, generics
 * incl. variance / star projections / nesting, nullables, function types). The [Arb] yields
 * random-length lists with index-based names. Extra cases can be contributed via [builder].
 */
internal fun Generator.Companion.properties(builder: GeneratorBuilder<List<PropertySpec>>.() -> Unit = {}): Generator<List<PropertySpec>> =
    generator {
        builder()
        "Primitive and basic types" case
            listOf(
                propertyOf("int", INT),
                propertyOf("long", LONG),
                propertyOf("float", FLOAT),
                propertyOf("double", DOUBLE),
                propertyOf("boolean", BOOLEAN),
                propertyOf("str", STRING),
                propertyOf("byte", BYTE),
                propertyOf("short", SHORT),
                propertyOf("char", CHAR),
            )
        "Unsigned and special types" case
            listOf(
                propertyOf("uint", U_INT),
                propertyOf("ulong", U_LONG),
                propertyOf("ubyte", U_BYTE),
                propertyOf("ushort", U_SHORT),
                propertyOf("any", ANY),
                propertyOf("anyNullable", ANY.copy(nullable = true)),
                propertyOf("unit", UNIT),
                propertyOf("number", NUMBER),
                propertyOf("charSeq", CHAR_SEQUENCE),
            )
        "Collection types" case
            listOf(
                propertyOf("list", LIST.parameterizedBy(STRING)),
                propertyOf("ids", SET.parameterizedBy(INT)),
                propertyOf("map", MAP.parameterizedBy(STRING, INT)),
                propertyOf("collection", COLLECTION.parameterizedBy(STRING)),
                propertyOf("iterable", ITERABLE.parameterizedBy(INT)),
                propertyOf("mutableList", MUTABLE_LIST.parameterizedBy(STRING)),
                propertyOf("mutableMap", MUTABLE_MAP.parameterizedBy(STRING, INT)),
                propertyOf("stringArray", ARRAY.parameterizedBy(STRING)),
                propertyOf("intArray", INT_ARRAY),
                propertyOf("longArray", LONG_ARRAY),
                propertyOf("byteArray", BYTE_ARRAY),
                propertyOf("shortArray", SHORT_ARRAY),
                propertyOf("charArray", CHAR_ARRAY),
                propertyOf("booleanArray", BOOLEAN_ARRAY),
                propertyOf("doubleArray", DOUBLE_ARRAY),
            )
        "Generic types" case
            listOf(
                propertyOf("pair", ClassName("kotlin", "Pair").parameterizedBy(STRING, INT)),
                propertyOf("outList", LIST.parameterizedBy(WildcardTypeName.producerOf(CHAR_SEQUENCE))),
                propertyOf("outArray", ARRAY.parameterizedBy(WildcardTypeName.producerOf(ANY))),
                propertyOf("inArray", ARRAY.parameterizedBy(WildcardTypeName.consumerOf(STRING))),
                propertyOf("inComparable", COMPARABLE.parameterizedBy(WildcardTypeName.consumerOf(INT))),
                propertyOf("starList", LIST.parameterizedBy(STAR)),
                propertyOf("starMap", MAP.parameterizedBy(STAR, STAR)),
                propertyOf("nestedList", LIST.parameterizedBy(LIST.parameterizedBy(STRING))),
                propertyOf("mapToList", MAP.parameterizedBy(STRING, LIST.parameterizedBy(INT))),
            )
        "Nullable types" case
            listOf(
                propertyOf("nullableInt", INT.copy(nullable = true)),
                propertyOf("nullableStr", STRING.copy(nullable = true)),
                propertyOf("nullableStringList", LIST.parameterizedBy(STRING).copy(nullable = true)),
                propertyOf("nullableElemList", LIST.parameterizedBy(STRING.copy(nullable = true))),
                propertyOf("nullableValueMap", MAP.parameterizedBy(STRING, INT.copy(nullable = true))),
            )
        "Function types" case
            listOf(
                propertyOf("action", LambdaTypeName.get(returnType = UNIT)),
                propertyOf("mapper", LambdaTypeName.get(parameters = listOf(ParameterSpec.unnamed(INT)), returnType = STRING)),
                propertyOf(
                    "biPredicate",
                    LambdaTypeName.get(
                        parameters = listOf(ParameterSpec.unnamed(INT), ParameterSpec.unnamed(STRING)),
                        returnType = BOOLEAN,
                    ),
                ),
                propertyOf("receiverFn", LambdaTypeName.get(receiver = STRING, returnType = INT)),
                propertyOf("suspendFn", LambdaTypeName.get(returnType = UNIT).copy(suspending = true)),
                propertyOf(
                    "nullableFn",
                    LambdaTypeName.get(parameters = listOf(ParameterSpec.unnamed(INT)), returnType = STRING).copy(nullable = true),
                ),
            )

        Arb.list(basicType().arb(), 0..3).map { types ->
            types.mapIndexed { index, type -> propertyOf("prop$index", type) }
        }
    }

private fun propertyOf(
    name: String,
    type: TypeName,
): PropertySpec = PropertySpec.builder(name, type).build()
