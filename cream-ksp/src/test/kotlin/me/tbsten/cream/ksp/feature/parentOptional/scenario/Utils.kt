package me.tbsten.cream.ksp.feature.parentOptional.scenario

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
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
import me.tbsten.cream.CopyVisibility
import me.tbsten.cream.ParentOptional
import me.tbsten.cream.ksp.testing.poet.Prop

/** `@ParentOptional`（任意で `propertyName` / `visibility` / `kdoc`）の [AnnotationSpec]。 */
internal fun parentOptional(
    propertyName: String? = null,
    visibility: CopyVisibility? = null,
    kdoc: CodeBlock? = null,
): AnnotationSpec =
    AnnotationSpec
        .builder(ParentOptional::class)
        .apply {
            if (propertyName != null) addMember("%L = %S", ParentOptional::propertyName.name, propertyName)
            if (visibility != null) addMember("%L = %T.%L", ParentOptional::visibility.name, CopyVisibility::class, visibility.name)
            if (kdoc != null) addMember("%L = %L", ParentOptional::kdoc.name, kdoc)
        }.build()

/** `@ParentOptional` 付き primary-constructor `val` の [Prop]。 */
internal fun parentOptionalProp(
    name: String,
    type: TypeName = STRING,
    propertyName: String? = null,
    visibility: CopyVisibility? = null,
    kdoc: CodeBlock? = null,
): Prop = Prop(name, type, paramAnnotation = parentOptional(propertyName, visibility, kdoc))

/** [children] を入れ子にした sealed interface。 */
internal fun sealedInterfaceParent(
    name: String,
    children: List<TypeSpec> = emptyList(),
    typeVariables: List<TypeVariableName> = emptyList(),
): TypeSpec =
    TypeSpec
        .interfaceBuilder(name)
        .addModifiers(SEALED)
        .addTypeVariables(typeVariables)
        .apply { children.forEach { addType(it) } }
        .build()

/** [parent] を実装する data class child。[props] の `paramAnnotation` / `visibility` を宣言に反映する。 */
internal fun childClass(
    name: String,
    parent: TypeName,
    props: List<Prop> = emptyList(),
    modifiers: List<KModifier> = listOf(DATA),
    typeVariables: List<TypeVariableName> = emptyList(),
): TypeSpec {
    val constructor = FunSpec.constructorBuilder()
    props.forEach { p ->
        constructor.addParameter(
            ParameterSpec.builder(p.name, p.type).apply { p.paramAnnotation?.let { addAnnotation(it) } }.build(),
        )
    }
    return TypeSpec
        .classBuilder(name)
        .addModifiers(modifiers)
        .addTypeVariables(typeVariables)
        .addSuperinterface(parent)
        .primaryConstructor(constructor.build())
        .apply {
            props.forEach { p ->
                addProperty(
                    PropertySpec
                        .builder(p.name, p.type)
                        .apply { p.visibility?.let { addModifiers(it) } }
                        .initializer(p.name)
                        .build(),
                )
            }
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
