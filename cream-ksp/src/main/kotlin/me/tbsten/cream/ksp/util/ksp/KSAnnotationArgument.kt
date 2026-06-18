package me.tbsten.cream.ksp.util.ksp

import com.google.devtools.ksp.symbol.KSAnnotation

/**
 * Read the value of the annotation argument called [name], cast to [T]. Returns `null` when the
 * argument is absent or its value is not a [T]. Replaces the repeated
 * `arguments.firstOrNull { it.name?.asString() == name }?.value as? T` boilerplate.
 */
internal inline fun <reified T> KSAnnotation.getArgument(name: String): T? =
    arguments
        .firstOrNull { it.name?.asString() == name }
        ?.value as? T
