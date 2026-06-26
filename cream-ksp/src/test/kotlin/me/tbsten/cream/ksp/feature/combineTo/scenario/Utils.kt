package me.tbsten.cream.ksp.feature.combineTo.scenario

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.TypeSpec
import me.tbsten.cream.CombineTo
import me.tbsten.cream.CopyVisibility
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.classNameOf

/** [this] を source として `@CombineTo([targets]…)`（任意で `visibility` / `kdoc` / `funName`）を付与する。 */
internal fun TypeSpec.withCombineTo(
    vararg targets: ClassName,
    visibility: CopyVisibility? = null,
    kdoc: CodeBlock? = null,
    funName: CodeBlock? = null,
): TypeSpec =
    toBuilder()
        .addAnnotation(
            AnnotationSpec
                .builder(CombineTo::class)
                .apply { targets.forEach { addMember("%T::class", it) } }
                .apply {
                    if (visibility != null) addMember("${CombineTo::visibility.name} = %T.%L", CopyVisibility::class, visibility.name)
                    if (kdoc != null) addMember("%L = %L", CombineTo::kdoc.name, kdoc)
                    if (funName != null) addMember("%L = %L", CombineTo::funName.name, funName)
                }.build(),
        ).build()

/**
 * source（@CombineTo 未付与・top-level）と target から、source に `@CombineTo(target)`（任意で `visibility` /
 * `kdoc` / `funName`）を付けた single-source scenario を作る。参照は `target.name`（= top-level の単純名）。
 */
internal fun combineTo(
    source: TypeSpec,
    target: TypeSpec,
    visibility: CopyVisibility? = null,
    kdoc: CodeBlock? = null,
    funName: CodeBlock? = null,
): SnapshotScenario =
    SnapshotScenario(
        source.withCombineTo(classNameOf(target.name!!), visibility = visibility, kdoc = kdoc, funName = funName),
        target,
    )

/**
 * 各 [sources] に `@CombineTo([target])`（任意で [visibility] / [kdoc] / [funName]）を付け、[target] と合わせた
 * combine scenario を作る。source が 2 つ以上なら N→1 combine（先頭が receiver、残りが leading param）になる。
 * 参照は `target.name`（= top-level の単純名）。
 */
internal fun combinedInto(
    target: TypeSpec,
    vararg sources: TypeSpec,
    visibility: CopyVisibility? = null,
    kdoc: CodeBlock? = null,
    funName: CodeBlock? = null,
): SnapshotScenario =
    SnapshotScenario(
        sources.map {
            it.withCombineTo(classNameOf(target.name!!), visibility = visibility, kdoc = kdoc, funName = funName)
        } + target,
    )
