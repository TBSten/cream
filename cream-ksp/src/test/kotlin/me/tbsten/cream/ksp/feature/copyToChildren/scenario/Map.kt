package me.tbsten.cream.ksp.feature.copyToChildren.scenario

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier.DATA
import com.squareup.kotlinpoet.KModifier.OVERRIDE
import com.squareup.kotlinpoet.KModifier.SEALED
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeSpec
import me.tbsten.cream.CopyToChildren
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.classNameOf
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

private fun mapAnno(vararg targetPropertyNames: String): AnnotationSpec =
    AnnotationSpec.builder(CopyToChildren.Map::class).apply { targetPropertyNames.forEach { addMember("%S", it) } }.build()

private val EXCLUDE = AnnotationSpec.builder(CopyToChildren.Exclude::class).build()

/**
 * `@CopyToChildren.Map` 用の data class child。ctor は子固有の命名 [ownProps] + override の [overrideProps] を持ち、
 * [getterOverrides]（親 abstract prop 名 -> ctor prop 名）で残りの親プロパティを getter override する。
 */
private fun mappedChild(
    name: String,
    parent: ClassName,
    ownProps: List<Prop> = emptyList(),
    overrideProps: List<Prop> = emptyList(),
    getterOverrides: List<Pair<String, String>> = emptyList(),
): TypeSpec {
    val constructor = FunSpec.constructorBuilder()
    (ownProps + overrideProps).forEach { constructor.addParameter(it.name, it.type) }
    return TypeSpec
        .classBuilder(name)
        .addModifiers(DATA)
        .addSuperinterface(parent)
        .primaryConstructor(constructor.build())
        .apply {
            ownProps.forEach { addProperty(PropertySpec.builder(it.name, it.type).initializer(it.name).build()) }
            overrideProps.forEach {
                addProperty(
                    PropertySpec
                        .builder(it.name, it.type)
                        .addModifiers(OVERRIDE)
                        .initializer(it.name)
                        .build(),
                )
            }
            getterOverrides.forEach { (parentProp, ctorProp) ->
                addProperty(
                    PropertySpec
                        .builder(parentProp, STRING)
                        .addModifiers(OVERRIDE)
                        .getter(FunSpec.getterBuilder().addStatement("return %N", ctorProp).build())
                        .build(),
                )
            }
        }.build()
}

private fun mapAndExcludeParent(): TypeSpec =
    TypeSpec
        .interfaceBuilder("Source")
        .addModifiers(SEALED)
        .addProperty(
            PropertySpec
                .builder("sourceName", STRING)
                .addAnnotation(mapAnno("targetName"))
                .addAnnotation(EXCLUDE)
                .build(),
        ).addProperty(PropertySpec.builder("shared", STRING).build())
        .addType(
            mappedChild(
                "Child",
                classNameOf("Source"),
                ownProps = listOf(Prop("targetName")),
                overrideProps = listOf(Prop("shared")),
                getterOverrides = listOf("sourceName" to "targetName"),
            ),
        ).build()

internal fun mapScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        "propertyMapping" to
            copyToChildren(
                sealedInterfaceParent(
                    "Source",
                    abstractProps = listOf(Prop("sourceName", paramAnnotation = mapAnno("targetName")), Prop("shared")),
                    children =
                        listOf(
                            mappedChild(
                                "Child",
                                classNameOf("Source"),
                                ownProps = listOf(Prop("targetName")),
                                overrideProps = listOf(Prop("shared")),
                                getterOverrides = listOf("sourceName" to "targetName"),
                            ),
                        ),
                ),
            ),
        "mapToNonexistentProperty" to
            copyToChildren(
                sealedInterfaceParent(
                    "Source",
                    abstractProps = listOf(Prop("sourceName", paramAnnotation = mapAnno("missingTarget")), Prop("shared")),
                    children =
                        listOf(
                            mappedChild(
                                "Child",
                                classNameOf("Source"),
                                ownProps = listOf(Prop("targetName")),
                                overrideProps = listOf(Prop("shared")),
                                getterOverrides = listOf("sourceName" to "targetName"),
                            ),
                        ),
                ),
            ),
        "mapOverridesNameMatch" to
            copyToChildren(
                sealedInterfaceParent(
                    "Source",
                    abstractProps = listOf(Prop("alpha"), Prop("beta", paramAnnotation = mapAnno("alpha"))),
                    children =
                        listOf(
                            mappedChild(
                                "Child",
                                classNameOf("Source"),
                                overrideProps = listOf(Prop("alpha")),
                                getterOverrides = listOf("beta" to "alpha"),
                            ),
                        ),
                ),
            ),
        "mapToDifferentNamePerChild" to
            copyToChildren(
                sealedInterfaceParent(
                    "Source",
                    abstractProps = listOf(Prop("sourceId", paramAnnotation = mapAnno("idA", "idB"))),
                    children =
                        listOf(
                            mappedChild(
                                "ChildA",
                                classNameOf("Source"),
                                ownProps = listOf(Prop("idA")),
                                getterOverrides = listOf("sourceId" to "idA"),
                            ),
                            mappedChild(
                                "ChildB",
                                classNameOf("Source"),
                                ownProps = listOf(Prop("idB")),
                                getterOverrides = listOf("sourceId" to "idB"),
                            ),
                        ),
                ),
            ),
        "mapAndExclude" to copyToChildren(mapAndExcludeParent()),
    )
