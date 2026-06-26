package me.tbsten.cream.ksp.feature.copyFrom.scenario

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.TypeSpec
import me.tbsten.cream.CopyFrom
import me.tbsten.cream.CopyVisibility
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.classNameOf

/** [this] を target として `@CopyFrom([source])`（任意で `visibility = ...` / `kdoc = ...` / `funName = ...`）を付与する。 */
internal fun TypeSpec.withCopyFrom(
    source: ClassName,
    visibility: CopyVisibility? = null,
    kdoc: CodeBlock? = null,
    funName: CodeBlock? = null,
): TypeSpec =
    toBuilder()
        .addAnnotation(
            AnnotationSpec
                .builder(CopyFrom::class)
                .addMember("%T::class", source)
                .apply {
                    if (visibility != null) addMember("${CopyFrom::visibility.name} = %T.%L", CopyVisibility::class, visibility.name)
                    if (kdoc != null) addMember("%L = %L", CopyFrom::kdoc.name, kdoc)
                    if (funName != null) addMember("%L = %L", CopyFrom::funName.name, funName)
                }.build(),
        ).build()

/**
 * target（@CopyFrom 未付与・top-level）と source から、target に `@CopyFrom(source)`（任意で `visibility` /
 * `kdoc`）を付けた scenario を作る。@CopyFrom の参照は `source.name`（= top-level の単純名）。第 1 引数が常に
 * 注釈の付く宣言（= ファイル先頭に出る）になるよう target を先に取る。
 */
internal fun copyFrom(
    target: TypeSpec,
    source: TypeSpec,
    visibility: CopyVisibility? = null,
    kdoc: CodeBlock? = null,
    funName: CodeBlock? = null,
): SnapshotScenario = SnapshotScenario(target.withCopyFrom(classNameOf(source.name!!), visibility, kdoc, funName), source)
