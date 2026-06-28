package me.tbsten.cream.ksp.feature.copyToChildren.scenario

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
import com.squareup.kotlinpoet.TypeSpec
import me.tbsten.cream.CopyToChildren
import me.tbsten.cream.CopyVisibility
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario

/** [this] を sealed parent として `@CopyToChildren`（任意で `notCopyToObject` / `visibility` / `kdoc` / `funName`）を付与する。 */
internal fun TypeSpec.withCopyToChildren(
    notCopyToObject: Boolean? = null,
    visibility: CopyVisibility? = null,
    kdoc: CodeBlock? = null,
    funName: CodeBlock? = null,
): TypeSpec =
    toBuilder()
        .addAnnotation(
            AnnotationSpec
                .builder(CopyToChildren::class)
                .apply {
                    if (notCopyToObject != null) addMember("%L = %L", CopyToChildren::notCopyToObject.name, notCopyToObject)
                    if (visibility != null) addMember("%L = %T.%L", CopyToChildren::visibility.name, CopyVisibility::class, visibility.name)
                    if (kdoc != null) addMember("%L = %L", CopyToChildren::kdoc.name, kdoc)
                    if (funName != null) addMember("%L = %L", CopyToChildren::funName.name, funName)
                }.build(),
        ).build()

/**
 * sealed parent（第 1 引数 = アノテーション付与先）に `@CopyToChildren` を付け、[additionalTopLevel]（外部の
 * sibling 宣言: enum leaf / customType など）を同ファイルに並べた scenario を作る。
 */
internal fun copyToChildren(
    sealedParent: TypeSpec,
    vararg additionalTopLevel: TypeSpec,
    notCopyToObject: Boolean? = null,
    visibility: CopyVisibility? = null,
    kdoc: CodeBlock? = null,
    funName: CodeBlock? = null,
): SnapshotScenario =
    SnapshotScenario(
        listOf(sealedParent.withCopyToChildren(notCopyToObject, visibility, kdoc, funName)) + additionalTopLevel,
    )

/** [abstractProps]（注釈/可視性も保持）を持ち [children] を入れ子にした sealed interface。 */
internal fun sealedInterfaceParent(
    name: String,
    abstractProps: List<Prop> = emptyList(),
    children: List<TypeSpec> = emptyList(),
): TypeSpec =
    TypeSpec
        .interfaceBuilder(name)
        .addModifiers(SEALED)
        .apply {
            abstractProps.forEach { p ->
                addProperty(
                    PropertySpec
                        .builder(p.name, p.type)
                        .apply {
                            p.visibility?.let { addModifiers(it) }
                            p.paramAnnotation?.let { addAnnotation(it) }
                        }.build(),
                )
            }
        }.apply { children.forEach { addType(it) } }
        .build()

/** [parent] の data class child。[overrides] を `override`、[extras] を追加プロパティにする。 */
internal fun childClass(
    name: String,
    parent: ClassName,
    overrides: List<Prop> = emptyList(),
    extras: List<Prop> = emptyList(),
    modifiers: List<KModifier> = listOf(DATA),
): TypeSpec {
    val constructor = FunSpec.constructorBuilder()
    (overrides + extras).forEach { constructor.addParameter(ParameterSpec.builder(it.name, it.type).build()) }
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
            extras.forEach { addProperty(PropertySpec.builder(it.name, it.type).initializer(it.name).build()) }
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
