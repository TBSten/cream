package me.tbsten.cream.ksp.testing.generator.clazz

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.generator.util.map

/** Default package used when rendering generated classes into compilable source. */
internal const val GENERATED_PACKAGE = "me.tbsten.cream.generated"

/** Renders a single [TypeSpec] into a compilable Kotlin source file string. */
internal fun TypeSpec.toKotlinSource(packageName: String = GENERATED_PACKAGE): String =
    FileSpec
        .builder(packageName, name ?: "Generated")
        .addType(this)
        .build()
        .toString()

/** Lifts a class generator into a source-string generator for `compileWithCream`. */
internal fun Generator<TypeSpec>.toSource(packageName: String = GENERATED_PACKAGE): Generator<String> = map { it.toKotlinSource(packageName) }
