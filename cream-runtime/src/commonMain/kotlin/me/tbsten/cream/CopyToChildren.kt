package me.tbsten.cream

@Target(AnnotationTarget.CLASS)
annotation class CopyToChildren(
    val notCopyToObject: Boolean = false,
) {
    @Target(AnnotationTarget.PROPERTY)
    annotation class Exclude
}
