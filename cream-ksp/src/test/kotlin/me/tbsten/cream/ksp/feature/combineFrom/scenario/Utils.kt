package me.tbsten.cream.ksp.feature.combineFrom.scenario

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.TypeSpec
import me.tbsten.cream.CombineFrom
import me.tbsten.cream.CopyVisibility
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.classNameOf

/** [this] を target として `@CombineFrom([sources]…)`（任意で `visibility` / `kdoc` / `funName`）を付与する。 */
internal fun TypeSpec.withCombineFrom(
    vararg sources: ClassName,
    visibility: CopyVisibility? = null,
    kdoc: CodeBlock? = null,
    funName: CodeBlock? = null,
): TypeSpec =
    toBuilder()
        .addAnnotation(
            AnnotationSpec
                .builder(CombineFrom::class)
                .apply { sources.forEach { addMember("%T::class", it) } }
                .apply {
                    if (visibility != null) addMember("${CombineFrom::visibility.name} = %T.%L", CopyVisibility::class, visibility.name)
                    if (kdoc != null) addMember("%L = %L", CombineFrom::kdoc.name, kdoc)
                    if (funName != null) addMember("%L = %L", CombineFrom::funName.name, funName)
                }.build(),
        ).build()

/**
 * target（@CombineFrom 未付与・top-level）と sources から、target に `@CombineFrom(sources…)`（任意で `visibility` /
 * `kdoc` / `funName`）を付けた scenario を作る。第 1 引数が常に注釈の付く宣言（= ファイル先頭に出る target）に
 * なるよう target を先に取る。参照は各 source の単純名。
 */
internal fun combineFrom(
    target: TypeSpec,
    vararg sources: TypeSpec,
    visibility: CopyVisibility? = null,
    kdoc: CodeBlock? = null,
    funName: CodeBlock? = null,
): SnapshotScenario =
    SnapshotScenario(
        listOf(
            target.withCombineFrom(
                *sources.map { classNameOf(it.name!!) }.toTypedArray(),
                visibility = visibility,
                kdoc = kdoc,
                funName = funName,
            ),
        ) + sources,
    )
