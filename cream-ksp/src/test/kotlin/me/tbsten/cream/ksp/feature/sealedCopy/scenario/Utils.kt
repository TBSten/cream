package me.tbsten.cream.ksp.feature.sealedCopy.scenario

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
import me.tbsten.cream.CopyVisibility
import me.tbsten.cream.NonCopyableStrategy
import me.tbsten.cream.SealedCopy
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario

internal fun TypeSpec.withSealedCopy(
    nonCopyableStrategy: NonCopyableStrategy? = null,
    funName: CodeBlock? = null,
    visibility: CopyVisibility? = null,
    kdoc: CodeBlock? = null,
): TypeSpec =
    toBuilder()
        .addAnnotation(
            AnnotationSpec
                .builder(SealedCopy::class)
                .apply {
                    if (funName != null) addMember("%L = %L", SealedCopy::funName.name, funName)
                    if (nonCopyableStrategy != null) {
                        addMember("%L = %T.%L", SealedCopy::nonCopyableStrategy.name, NonCopyableStrategy::class, nonCopyableStrategy.name)
                    }
                    if (kdoc != null) addMember("%L = %L", SealedCopy::kdoc.name, kdoc)
                    if (visibility != null) addMember("%L = %T.%L", SealedCopy::visibility.name, CopyVisibility::class, visibility.name)
                }.build(),
        ).build()

internal fun sealedCopy(
    sealedParent: TypeSpec,
    vararg additionalTopLevel: TypeSpec,
    nonCopyableStrategy: NonCopyableStrategy? = null,
    funName: CodeBlock? = null,
    visibility: CopyVisibility? = null,
    kdoc: CodeBlock? = null,
): SnapshotScenario =
    SnapshotScenario(
        listOf(sealedParent.withSealedCopy(nonCopyableStrategy, funName, visibility, kdoc)) + additionalTopLevel,
    )

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

internal fun objectChild(
    name: String,
    parent: ClassName,
): TypeSpec =
    TypeSpec
        .objectBuilder(name)
        .addSuperinterface(parent)
        .build()
