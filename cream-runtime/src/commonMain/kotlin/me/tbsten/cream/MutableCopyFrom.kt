package me.tbsten.cream

import kotlin.reflect.KClass

/**
 * Generate `<sources class>.mutableCopyTo<annotated by MutableCopyFrom class>()` mutable copy functions.
 *
 * Similar to `@CopyFrom`, but generates mutable copy functions that copy properties from the source object
 * to an existing mutable target object with explicit parameter values.
 *
 * # Example
 *
 * ```kt
 * data class DataLayerModel(
 *   val data: Data,
 * )
 *
 * @MutableCopyFrom(DataLayerModel::class)
 * data class DomainLayerModel(
 *   var data: Data,
 * )
 *
 * // Auto generate
 *
 * fun DataLayerModel.mutableCopyToDomainLayerModel(
 *   mutableTarget: DomainLayerModel,
 *   data: Data = this.data,
 * ): DomainLayerModel {
 *   mutableTarget.data = data
 *   return mutableTarget
 * }
 * ```
 *
 * # Usage
 *
 * ```kt
 * val source = DataLayerModel(Data("test"))
 * val target = DomainLayerModel(Data("old"))
 *
 * val result = source.mutableCopyToDomainLayerModel(
 *   mutableTarget = target,
 *   data = Data("new")
 * )
 * // result.data == Data("new")
 * // result === target (same instance)
 * ```
 *
 * @see CopyFrom
 * @see MutableCopyTo
 */
@Target(AnnotationTarget.CLASS)
annotation class MutableCopyFrom(
    vararg val sources: KClass<*>,
) {
    @Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.TYPE_PARAMETER)
    annotation class Map(vararg val propertyNames: String)

    @Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.TYPE_PARAMETER)
    annotation class Exclude(val value: String)
}
