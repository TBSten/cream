package me.tbsten.cream.ksp.feature.childOptionals.scenario

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.KModifier.DATA
import com.squareup.kotlinpoet.KModifier.OVERRIDE
import com.squareup.kotlinpoet.KModifier.SEALED
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import me.tbsten.cream.ChildOptionals
import me.tbsten.cream.CopyVisibility
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario

/** [this] を sealed parent として `@ChildOptionals`（任意で `visibility` / `kdoc`）を付与する。 */
internal fun TypeSpec.withChildOptionals(
    visibility: CopyVisibility? = null,
    kdoc: CodeBlock? = null,
): TypeSpec =
    toBuilder()
        .addAnnotation(
            AnnotationSpec
                .builder(ChildOptionals::class)
                .apply {
                    if (visibility != null) addMember("%L = %T.%L", ChildOptionals::visibility.name, CopyVisibility::class, visibility.name)
                    if (kdoc != null) addMember("%L = %L", ChildOptionals::kdoc.name, kdoc)
                }.build(),
        ).build()

/** sealed parent（第 1 引数 = アノテーション付与先）に `@ChildOptionals` を付けた scenario を作る。 */
internal fun childOptionals(
    sealedParent: TypeSpec,
    vararg additionalTopLevel: TypeSpec,
    visibility: CopyVisibility? = null,
    kdoc: CodeBlock? = null,
): SnapshotScenario =
    SnapshotScenario(
        listOf(sealedParent.withChildOptionals(visibility, kdoc)) + additionalTopLevel,
    )

/** [abstractProps] を持ち [children] を入れ子にした sealed interface。 */
internal fun sealedInterfaceParent(
    name: String,
    abstractProps: List<Prop> = emptyList(),
    children: List<TypeSpec> = emptyList(),
): TypeSpec =
    TypeSpec
        .interfaceBuilder(name)
        .addModifiers(SEALED)
        .apply { abstractProps.forEach { addProperty(PropertySpec.builder(it.name, it.type).build()) } }
        .apply { children.forEach { addType(it) } }
        .build()

/**
 * [parent] を実装する data class child。[overrides] を `override`、[props] を子固有プロパティ
 * （`paramAnnotation` / `visibility` を反映）、[bodyProps] を body 宣言プロパティにする。
 */
internal fun childClass(
    name: String,
    parent: TypeName,
    props: List<Prop> = emptyList(),
    overrides: List<Prop> = emptyList(),
    modifiers: List<KModifier> = listOf(DATA),
    bodyProps: List<PropertySpec> = emptyList(),
): TypeSpec {
    val constructor = FunSpec.constructorBuilder()
    (overrides + props).forEach { p ->
        constructor.addParameter(
            ParameterSpec.builder(p.name, p.type).apply { p.paramAnnotation?.let { addAnnotation(it) } }.build(),
        )
    }
    return TypeSpec
        .classBuilder(name)
        .addModifiers(modifiers)
        .addSuperinterface(parent)
        .primaryConstructor(constructor.build())
        .apply {
            overrides.forEach {
                addProperty(
                    PropertySpec
                        .builder(it.name, it.type)
                        .addModifiers(OVERRIDE)
                        .initializer(it.name)
                        .build(),
                )
            }
            props.forEach { p ->
                addProperty(
                    PropertySpec
                        .builder(p.name, p.type)
                        .apply { p.visibility?.let { addModifiers(it) } }
                        .initializer(p.name)
                        .build(),
                )
            }
            bodyProps.forEach { addProperty(it) }
        }.build()
}

/** [parent] を実装する（プロパティを持たない）object child。 */
internal fun objectChild(
    name: String,
    parent: ClassName,
): TypeSpec =
    TypeSpec
        .objectBuilder(name)
        .addSuperinterface(parent)
        .build()

/** [parent] を実装し [leaves] を入れ子にする中間 sealed interface。 */
internal fun nestedSealed(
    name: String,
    parent: ClassName,
    vararg leaves: TypeSpec,
): TypeSpec =
    TypeSpec
        .interfaceBuilder(name)
        .addModifiers(SEALED)
        .addSuperinterface(parent)
        .apply { leaves.forEach { addType(it) } }
        .build()
