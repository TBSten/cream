package me.tbsten.cream.ksp.feature.copyTo.scenario

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.TypeSpec
import me.tbsten.cream.CopyTo
import me.tbsten.cream.CopyVisibility
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.classNameOf

/** [this] を source として `@CopyTo([target])`（任意で `visibility = ...` / `kdoc = ...` / `funName = ...`）を付与する。 */
internal fun TypeSpec.withCopyTo(
    target: ClassName,
    visibility: CopyVisibility? = null,
    kdoc: CodeBlock? = null,
    funName: CodeBlock? = null,
): TypeSpec =
    toBuilder()
        .addAnnotation(
            AnnotationSpec
                .builder(CopyTo::class)
                .addMember("%T::class", target)
                .apply {
                    if (visibility != null) addMember("${CopyTo::visibility.name} = %T.%L", CopyVisibility::class, visibility.name)
                    if (kdoc != null) addMember("%L = %L", CopyTo::kdoc.name, kdoc)
                    if (funName != null) addMember("%L = %L", CopyTo::funName.name, funName)
                }.build(),
        ).build()

/**
 * source（@CopyTo 未付与・top-level）と target から、source に `@CopyTo(target)`（任意で `visibility` /
 * `kdoc` / `funName`）を付けた scenario を作る。@CopyTo の参照は `target.name`（= top-level の単純名）。
 */
internal fun copyTo(
    source: TypeSpec,
    target: TypeSpec,
    visibility: CopyVisibility? = null,
    kdoc: CodeBlock? = null,
    funName: CodeBlock? = null,
): SnapshotScenario = SnapshotScenario(source.withCopyTo(classNameOf(target.name!!), visibility, kdoc, funName), target)
