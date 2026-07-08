package me.tbsten.cream.ksp.feature.copyMapping.scenario

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.TypeSpec
import me.tbsten.cream.CopyMapping
import me.tbsten.cream.CopyVisibility
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.classNameOf

/** `@CopyMapping` を載せる holder。生成関数の可視性は visibility 引数（無ければ target）に従う。 */
internal fun mappingHolder(): TypeSpec = TypeSpec.objectBuilder("Mapping").build()

/** [this] を holder として `@CopyMapping([source], [target], …)` を付与する。 */
internal fun TypeSpec.withCopyMapping(
    source: ClassName,
    target: ClassName,
    canReverse: Boolean = false,
    properties: List<Pair<String, String>> = emptyList(),
    excludes: List<String> = emptyList(),
    visibility: CopyVisibility? = null,
    kdoc: CodeBlock? = null,
    funName: CodeBlock? = null,
): TypeSpec =
    toBuilder()
        .addAnnotation(
            AnnotationSpec
                .builder(CopyMapping::class)
                .addMember("%T::class", source)
                .addMember("%T::class", target)
                .apply {
                    if (canReverse) addMember("%L = %L", CopyMapping::canReverse.name, true)
                    if (properties.isNotEmpty()) addMember("%L = %L", CopyMapping::properties.name, propertiesBlock(properties))
                    if (excludes.isNotEmpty()) addMember("%L = %L", CopyMapping::excludes.name, excludesBlock(excludes))
                    if (visibility != null) addMember("${CopyMapping::visibility.name} = %T.%L", CopyVisibility::class, visibility.name)
                    if (kdoc != null) addMember("%L = %L", CopyMapping::kdoc.name, kdoc)
                    if (funName != null) addMember("%L = %L", CopyMapping::funName.name, funName)
                }.build(),
        ).build()

/**
 * holder（第 1 引数 = 注釈の付く宣言）と参照される source / target から scenario を作る。@CopyMapping の
 * source / target 参照は top-level 単純名（`source.name` / `target.name`）。holder を先頭に置くのでファイル先頭に出る。
 */
internal fun copyMapping(
    holder: TypeSpec,
    source: TypeSpec,
    target: TypeSpec,
    canReverse: Boolean = false,
    properties: List<Pair<String, String>> = emptyList(),
    excludes: List<String> = emptyList(),
    visibility: CopyVisibility? = null,
    kdoc: CodeBlock? = null,
    funName: CodeBlock? = null,
): SnapshotScenario =
    SnapshotScenario(
        holder.withCopyMapping(
            classNameOf(source.name!!),
            classNameOf(target.name!!),
            canReverse,
            properties,
            excludes,
            visibility,
            kdoc,
            funName,
        ),
        source,
        target,
    )

private fun propertiesBlock(properties: List<Pair<String, String>>): CodeBlock =
    CodeBlock
        .builder()
        .add("[")
        .apply {
            properties.forEachIndexed { index, (source, target) ->
                if (index > 0) add(", ")
                add("%T(source = %S, target = %S)", CopyMapping.Map::class, source, target)
            }
        }.add("]")
        .build()

private fun excludesBlock(excludes: List<String>): CodeBlock =
    CodeBlock
        .builder()
        .add("[")
        .apply {
            excludes.forEachIndexed { index, name ->
                if (index > 0) add(", ")
                add("%S", name)
            }
        }.add("]")
        .build()
