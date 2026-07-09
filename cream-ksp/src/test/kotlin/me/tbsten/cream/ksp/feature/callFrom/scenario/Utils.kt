package me.tbsten.cream.ksp.feature.callFrom.scenario

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeAliasSpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import me.tbsten.cream.CallFrom
import me.tbsten.cream.CopyVisibility
import me.tbsten.cream.ksp.testing.generator.clazz.GENERATED_PACKAGE
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.classNameOf

/** [this] を bridge 先関数として `@CallFrom([sources])`（任意で `visibility = ...` / `kdoc = ...` / `funName = ...`）を付与する。 */
internal fun FunSpec.withCallFrom(
    vararg sources: ClassName,
    visibility: CopyVisibility? = null,
    kdoc: CodeBlock? = null,
    funName: String? = null,
): FunSpec =
    toBuilder()
        .addAnnotation(
            AnnotationSpec
                .builder(CallFrom::class)
                .apply { sources.forEach { addMember("%T::class", it) } }
                .apply {
                    if (visibility != null) addMember("${CallFrom::visibility.name} = %T.%L", CopyVisibility::class, visibility.name)
                    if (kdoc != null) addMember("%L = %L", CallFrom::kdoc.name, kdoc)
                    if (funName != null) addMember("${CallFrom::funName.name} = %S", funName)
                }.build(),
        ).build()

/**
 * top-level の [function] に `@CallFrom([sources])` を付けた scenario を作る。第 1 引数が常に注釈の付く宣言
 * （= ファイル先頭に出る）になるよう function を先に取る。[sources] は同ファイル top-level の単純名参照。
 * source でない補助宣言（custom 型など）は [extraDeclarations] に渡す。
 */
internal fun callFrom(
    function: FunSpec,
    vararg sources: TypeSpec,
    visibility: CopyVisibility? = null,
    kdoc: CodeBlock? = null,
    funName: String? = null,
    extraDeclarations: List<TypeSpec> = emptyList(),
    typeAliases: List<TypeAliasSpec> = emptyList(),
): SnapshotScenario =
    functionScenario(
        function.withCallFrom(
            *sources.map { classNameOf(it.name!!) }.toTypedArray(),
            visibility = visibility,
            kdoc = kdoc,
            funName = funName,
        ),
        *(sources.toList() + extraDeclarations).toTypedArray(),
        typeAliases = typeAliases,
    )

/**
 * `@CallFrom` 付与済みの top-level [annotatedFunction] と補助宣言から scenario を作る。ネストした source を
 * 参照するなど [callFrom] の単純名参照で表せないケース用。[typeAliases] は typealias 経由の型解決ケース用。
 */
internal fun functionScenario(
    annotatedFunction: FunSpec,
    vararg declarations: TypeSpec,
    typeAliases: List<TypeAliasSpec> = emptyList(),
): SnapshotScenario =
    SnapshotScenario(
        files =
            listOf(
                FileSpec
                    .builder(GENERATED_PACKAGE, "$GENERATED_PACKAGE.${annotatedFunction.name}")
                    .addFunction(annotatedFunction)
                    .apply { declarations.forEach { addType(it) } }
                    .apply { typeAliases.forEach { addTypeAlias(it) } }
                    .build(),
            ),
    )

/** bridge 先関数の value parameter（任意で @CallFrom.Map / @CallFrom.Exclude 等の [annotations]）。 */
internal fun param(
    name: String,
    type: TypeName = STRING,
    vararg annotations: AnnotationSpec,
): ParameterSpec = ParameterSpec.builder(name, type).apply { annotations.forEach { addAnnotation(it) } }.build()

/** 本体が空の `fun [name]([parameters])`。suspend / 戻り値 / 型パラメータが要る場合は [FunSpec.builder] を直接使う。 */
internal fun fn(
    name: String,
    vararg parameters: ParameterSpec,
): FunSpec = FunSpec.builder(name).addParameters(parameters.toList()).build()
