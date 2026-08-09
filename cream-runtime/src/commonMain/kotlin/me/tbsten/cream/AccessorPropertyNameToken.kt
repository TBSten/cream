@file:Suppress("ktlint:standard:property-naming")

package me.tbsten.cream

/**
 * The name cream gives a generated accessor by default — the annotated property's own name.
 *
 * [ParentOptional.propertyName] is a **template string**, the way `funName` is for the copy/combine
 * annotations, and this token is its default value. Embed it to derive a name instead of spelling
 * one out; because it is a `const val`, interpolation and `+` both stay compile-time constants:
 *
 * ```kt
 * import me.tbsten.cream.*
 *
 * sealed interface MyState {
 *     data class Success(
 *         @ParentOptional                                                         // data
 *         val data: String,
 *         @ParentOptional(propertyName = "${DefaultAccessorPropertyName}OrNull")  // countOrNull
 *         val count: Int,
 *         @ParentOptional(propertyName = "resultCode")                            // resultCode
 *         val code: Int,
 *     ) : MyState
 * }
 * ```
 *
 * The project-wide naming options (`cream.copyFunNamePrefix` / `cream.copyFunNamingStrategy` /
 * `cream.escapeDot`) compose *function* names and do not apply here. The resolved name decides
 * which contributions **merge**, so two children stay separate when their base names differ even
 * under the same template. As with `funName`, an illegal identifier is not rejected by cream — it
 * surfaces as a compilation error in the generated file.
 */
public const val DefaultAccessorPropertyName: String = "{{cream:DefaultAccessorPropertyName}}"
