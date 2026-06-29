package me.tbsten.cream.test.copyTo

import me.tbsten.cream.CopyTo

// data class -> annotation class copy (issue #132).
// Kotlin allows instantiating an annotation class via its constructor, so cream can build the
// target the same way it builds a regular class.
@CopyTo(AnnotationClassTarget::class)
data class AnnotationClassSource(
    val name: String,
    val count: Int,
)

annotation class AnnotationClassTarget(
    val name: String,
    val count: Int,
)
