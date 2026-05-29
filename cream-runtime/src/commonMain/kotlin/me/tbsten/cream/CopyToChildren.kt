package me.tbsten.cream

@Target(AnnotationTarget.CLASS)
annotation class CopyToChildren(
    val notCopyToObject: Boolean = false,
    val kdoc: KDoc = KDoc(),
)
