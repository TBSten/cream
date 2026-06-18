package me.tbsten.cream

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@RequiresOptIn(message = "This API is used internally within cream.kt. Direct use is not recommended.")
public annotation class InternalCreamApi
