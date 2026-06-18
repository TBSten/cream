package me.tbsten.cream.ksp.util.ksp

import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSType
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

/**
 * Read the value of the annotation argument called [name], cast to [T]. Returns `null` when the
 * argument is absent or its value is not a [T]. Replaces the repeated
 * `arguments.firstOrNull { it.name?.asString() == name }?.value as? T` boilerplate.
 *
 * Prefer the property-reference overloads over a string literal where a concrete annotation property
 * exists, so a rename of the property is caught by the compiler.
 */
internal inline fun <reified T> KSAnnotation.getArgument(name: String): T? =
    arguments
        .firstOrNull { it.name?.asString() == name }
        ?.value as? T

/**
 * Read the argument named by an annotation [property] whose declared type matches how KSP surfaces
 * the value (i.e. [String] / [Boolean] / numbers). The result type is inferred from the property, so
 * neither the name nor the type is spelled out, e.g. `getArgument(CopyToChildren::notCopyToObject)`.
 *
 * Note: a `KClass<*>` property has its own overload (KSP returns it as a [KSType]); arguments whose
 * KSP value type differs from the declared type in other ways (enums, nested annotations, arrays)
 * still need the string-named overload with the explicit value type.
 */
internal inline fun <reified T> KSAnnotation.getArgument(property: KProperty<T>): T? = getArgument<T>(property.name)

/**
 * Read a `KClass<*>` annotation argument named by [property]. KSP surfaces a class reference as a
 * [KSType], so this overload returns the [KSType] (no explicit type argument needed), e.g.
 * `getArgument(CopyMapping::source)`.
 */
@JvmName("getClassArgument")
internal fun KSAnnotation.getArgument(property: KProperty<KClass<*>>): KSType? = getArgument<KSType>(property.name)

/**
 * Read an `Array<*>` annotation argument named by [property]. KSP surfaces an array as a `List<*>`
 * (of `KSType` for `Array<KClass>`, of `KSAnnotation` for an array of nested annotations, …), so this
 * overload returns the `List<*>` (no explicit type argument needed), e.g.
 * `getArgument(CombineMapping::sources)`.
 */
@JvmName("getArrayArgument")
internal fun KSAnnotation.getArgument(property: KProperty<Array<*>>): List<*>? = getArgument<List<*>>(property.name)
