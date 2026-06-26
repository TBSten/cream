package me.tbsten.cream.ksp.testing.generator.clazz

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.generator.util.mapLabel
import me.tbsten.cream.ksp.testing.generator.util.union

/** The kind of top-level declaration [typeSpec] can emit. */
internal enum class TypeKind { Class, Interface, Enum, Object }

/**
 * A generator that emits a class / interface / enum / object declaration as a built [TypeSpec],
 * delegating to the per-kind builders. Restrict or reorder the covered kinds via [kinds].
 */
internal fun Generator.Companion.typeSpec(
    name: Generator<String>,
    kinds: List<TypeKind> = TypeKind.entries,
    properties: Generator<List<PropertySpec>> = Generator.properties(),
    typeParameters: Generator<List<PropertySpec>> = Generator.typeParameters(),
    functions: List<Generator<FunSpec>> = emptyList(),
): Generator<TypeSpec> {
    require(kinds.isNotEmpty()) { "kinds must not be empty" }
    val typeParamCapable = listOf(properties, typeParameters).union()
    return kinds
        .map { kind ->
            when (kind) {
                TypeKind.Class -> classSpec(name = name, constructors = typeParamCapable.asPrimaryConstructor(), functions = functions)
                TypeKind.Enum -> enumSpec(name = name, constructors = properties.asPrimaryConstructor(), functions = functions)
                TypeKind.Object -> objectSpec(name, properties, functions)
                TypeKind.Interface -> interfaceSpec(name, typeParamCapable, functions)
            }.mapLabel { repLabel -> listOfNotNull(kind.name.lowercase(), repLabel).joinToString(", ") }
        }.union()
}
