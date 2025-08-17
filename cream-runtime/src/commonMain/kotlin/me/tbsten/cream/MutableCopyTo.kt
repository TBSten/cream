package me.tbsten.cream

import kotlin.reflect.KClass

/**
 * Generate `<annotated by MutableCopyTo class>.mutableCopyTo<targets class>()` mutable copy functions.
 *
 * This generates a function that copies properties from the source object to a mutable target object
 * with explicit parameter values. Properties with matching names and types use source values as defaults.
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
 * fun Hoge.mutableCopyToFuga(
 *   mutableTarget: Fuga,
 *   hogeProp1: String = this.hogeProp1,
 *   hogeProp2: Int = this.hogeProp2,
 *   fugaProp1: String,
 * ): Fuga {
 *   mutableTarget.hogeProp1 = hogeProp1
 *   mutableTarget.hogeProp2 = hogeProp2
 *   mutableTarget.fugaProp1 = fugaProp1
 *   return mutableTarget
 * }
 * ```
 *
 * # Usage
 *
 * ```kt
 * val source = Hoge("test1", 42)
 * val target = Fuga("old1", 0, "old_fuga")
 *
 * val result = source.mutableCopyToFuga(
 *   mutableTarget = target,
 *   fugaProp1 = "new_fuga"
 * )
 * // result.hogeProp1 == "test1" (from source)
 * // result.hogeProp2 == 42 (from source)
 * // result.fugaProp1 == "new_fuga" (explicitly set)
 * // result === target (same instance)
 * ```
 *
 * @see CopyTo
 */
@Target(AnnotationTarget.CLASS)
annotation class MutableCopyTo(
    vararg val targets: KClass<*>,
    val mutableCopyFunNamePrefix: String = "",
) {
    @Target(AnnotationTarget.PROPERTY, AnnotationTarget.TYPE_PARAMETER)
    annotation class Map(vararg val propertyNames: String)
}
