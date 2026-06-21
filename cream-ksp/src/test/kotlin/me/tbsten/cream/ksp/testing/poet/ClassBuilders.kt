package me.tbsten.cream.ksp.testing.poet

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.KModifier.DATA
import com.squareup.kotlinpoet.KModifier.SEALED
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.TypeVariableName
import me.tbsten.cream.ksp.testing.generator.clazz.GENERATED_PACKAGE

/** [GENERATED_PACKAGE] 配下の [ClassName] を作る（`classNameOf("Foo", "Bar")` -> `…generated.Foo.Bar`）。 */
internal fun classNameOf(vararg simpleNames: String): ClassName = ClassName(GENERATED_PACKAGE, *simpleNames)

/** primary-constructor の `val` プロパティ。型（型変数も可）・可視性・param 注釈（@Map / @Exclude 等）を持てる。 */
internal class Prop(
    val name: String,
    val type: TypeName = STRING,
    val visibility: KModifier? = null,
    val paramAnnotation: AnnotationSpec? = null,
)

/**
 * [modifiers] / [annotations] / [typeVariables] / primary-ctor val [props] / [secondaryConstructors]
 * からクラスを組む。[typeVariables] を渡すと generic クラス（`class Foo<T>(...)`）になる。
 */
internal fun clazz(
    name: String,
    vararg props: Prop,
    modifiers: List<KModifier> = emptyList(),
    annotations: List<AnnotationSpec> = emptyList(),
    typeVariables: List<TypeVariableName> = emptyList(),
    secondaryConstructors: List<FunSpec> = emptyList(),
    constructorVisibility: KModifier? = null,
): TypeSpec {
    val builder = TypeSpec.classBuilder(name).addModifiers(modifiers).addTypeVariables(typeVariables)
    annotations.forEach { builder.addAnnotation(it) }
    if (props.isNotEmpty()) {
        val constructor = FunSpec.constructorBuilder()
        constructorVisibility?.let { constructor.addModifiers(it) }
        props.forEach { p ->
            constructor.addParameter(
                ParameterSpec.builder(p.name, p.type).apply { p.paramAnnotation?.let { addAnnotation(it) } }.build(),
            )
        }
        builder.primaryConstructor(constructor.build())
        props.forEach { p ->
            builder.addProperty(
                PropertySpec
                    .builder(p.name, p.type)
                    .apply { p.visibility?.let { addModifiers(it) } }
                    .initializer(p.name)
                    .build(),
            )
        }
    }
    secondaryConstructors.forEach { builder.addFunction(it) }
    return builder.build()
}

/** [clazz] の data class ショートハンド。 */
internal fun dataClass(
    name: String,
    vararg props: Prop,
    typeVariables: List<TypeVariableName> = emptyList(),
    secondaryConstructors: List<FunSpec> = emptyList(),
): TypeSpec = clazz(name, *props, modifiers = listOf(DATA), typeVariables = typeVariables, secondaryConstructors = secondaryConstructors)

/** sealed interface（abstract な val のみ）。 */
internal fun sealedInterface(
    name: String,
    vararg abstractProperties: String,
): TypeSpec =
    TypeSpec
        .interfaceBuilder(name)
        .addModifiers(SEALED)
        .apply { abstractProperties.forEach { addProperty(PropertySpec.builder(it, STRING).build()) } }
        .build()

/** [this] を `inner` 修飾子付きにコピーする。 */
internal fun TypeSpec.asInner(): TypeSpec = toBuilder().addModifiers(KModifier.INNER).build()

/** [this] に [nested] を入れ子として加えたコピーを返す。 */
internal fun TypeSpec.containing(vararg nested: TypeSpec): TypeSpec = toBuilder().apply { nested.forEach { addType(it) } }.build()

/** [name] という空クラスに [nested] を入れた TypeSpec（Outer / Middle / Parent のような容器用）。 */
internal fun classWithNested(
    name: String,
    vararg nested: TypeSpec,
): TypeSpec = TypeSpec.classBuilder(name).apply { nested.forEach { addType(it) } }.build()
