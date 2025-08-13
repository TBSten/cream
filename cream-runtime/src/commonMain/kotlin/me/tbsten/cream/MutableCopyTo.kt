package me.tbsten.cream

import kotlin.reflect.KClass

/**
 * Generate `<annotated by MutableCopyTo class>.copyTo<targets class>()` mutable copy functions.
 *
 * This generates a function that copies properties from the source object to a mutable target object,
 * and provides a scope for customizing the copied values.
 *
 * # Example
 *
 * ```kt
 * @MutableCopyTo(Fuga::class)
 * data class Hoge(
 *   val hogeProp1: String,
 *   val hogeProp2: Int,
 * )
 *
 * data class Fuga(
 *   var hogeProp1: String,
 *   var hogeProp2: Int,
 *   var fugaProp1: String,
 * )
 *
 * // Auto generate
 *
 * fun Hoge.copyToFuga(
 *   fuga: Fuga,
 *   block: CopyToFugaScope.() -> Unit = {},
 * ) {
 *   fuga.hogeProp1 = hogeProp1
 *   fuga.hogeProp2 = hogeProp2
 *   // Apply customizations
 *   CopyToFugaScope(this, fuga).block()
 * }
 *
 * class CopyToFugaScope(val hoge: Hoge, val fuga: Fuga) {
 *   var hogeProp1: String
 *     get() = fuga.hogeProp1
 *     set(value) { fuga.hogeProp1 = value }
 *   var hogeProp2: Int
 *     get() = fuga.hogeProp2
 *     set(value) { fuga.hogeProp2 = value }
 *   var fugaProp1: String
 *     get() = fuga.fugaProp1
 *     set(value) { fuga.fugaProp1 = value }
 * }
 * ```
 *
 * @see CopyTo
 */
@Target(AnnotationTarget.CLASS)
annotation class MutableCopyTo(
    vararg val targets: KClass<*>,
) {
    @Target(AnnotationTarget.PROPERTY, AnnotationTarget.TYPE_PARAMETER)
    annotation class Map(vararg val propertyNames: String)
}
