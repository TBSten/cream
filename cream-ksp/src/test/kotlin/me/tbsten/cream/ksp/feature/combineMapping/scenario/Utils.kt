package me.tbsten.cream.ksp.feature.combineMapping.scenario

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.TypeSpec
import me.tbsten.cream.CombineMapping
import me.tbsten.cream.CopyVisibility
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.classNameOf

/** `@CombineMapping` を載せる holder。生成関数の可視性は visibility 引数（無ければ target）に従う。 */
internal fun mappingHolder(): TypeSpec = TypeSpec.objectBuilder("Mapping").build()

/** [this] を holder として `@CombineMapping(sources = […], target = …, …)` を付与する。sources は最低 2 件。 */
internal fun TypeSpec.withCombineMapping(
    sources: List<ClassName>,
    target: ClassName,
    properties: List<Pair<String, String>> = emptyList(),
    excludes: List<String> = emptyList(),
    visibility: CopyVisibility? = null,
    kdoc: CodeBlock? = null,
    funName: CodeBlock? = null,
): TypeSpec =
    toBuilder()
        .addAnnotation(
            AnnotationSpec
                .builder(CombineMapping::class)
                .addMember("%L = %L", CombineMapping::sources.name, sourcesBlock(sources))
                .addMember("%L = %T::class", CombineMapping::target.name, target)
                .apply {
                    if (properties.isNotEmpty()) addMember("%L = %L", CombineMapping::properties.name, propertiesBlock(properties))
                    if (excludes.isNotEmpty()) addMember("%L = %L", CombineMapping::excludes.name, excludesBlock(excludes))
                    if (visibility != null) addMember("${CombineMapping::visibility.name} = %T.%L", CopyVisibility::class, visibility.name)
                    if (kdoc != null) addMember("%L = %L", CombineMapping::kdoc.name, kdoc)
                    if (funName != null) addMember("%L = %L", CombineMapping::funName.name, funName)
                }.build(),
        ).build()

/**
 * holder（第 1 引数 = 注釈の付く宣言）と参照される sources / target から scenario を作る。holder を先頭に置くので
 * ファイル先頭に出る。`@CombineMapping` の sources / target 参照は top-level の単純名。
 */
internal fun combineMapping(
    holder: TypeSpec,
    sources: List<TypeSpec>,
    target: TypeSpec,
    properties: List<Pair<String, String>> = emptyList(),
    excludes: List<String> = emptyList(),
    visibility: CopyVisibility? = null,
    kdoc: CodeBlock? = null,
    funName: CodeBlock? = null,
): SnapshotScenario =
    SnapshotScenario(
        listOf(
            holder.withCombineMapping(
                sources.map { classNameOf(it.name!!) },
                classNameOf(target.name!!),
                properties,
                excludes,
                visibility,
                kdoc,
                funName,
            ),
        ) + sources + target,
    )

private fun sourcesBlock(sources: List<ClassName>): CodeBlock =
    CodeBlock
        .builder()
        .add("[")
        .apply {
            sources.forEachIndexed { index, source ->
                if (index > 0) add(", ")
                add("%T::class", source)
            }
        }.add("]")
        .build()

private fun propertiesBlock(properties: List<Pair<String, String>>): CodeBlock =
    CodeBlock
        .builder()
        .add("[")
        .apply {
            properties.forEachIndexed { index, (source, target) ->
                if (index > 0) add(", ")
                add("%T(source = %S, target = %S)", CombineMapping.Map::class, source, target)
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
