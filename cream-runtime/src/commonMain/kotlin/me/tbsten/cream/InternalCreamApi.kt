package me.tbsten.cream

/**
 * Marks declarations that cream.kt uses internally.
 *
 * They are public only so that generated code and cream's own modules can reach them. They are
 * not part of the supported API and may change without notice, so using them requires an explicit
 * opt-in.
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@RequiresOptIn(message = "This API is used internally within cream.kt. Direct use is not recommended.")
public annotation class InternalCreamApi
