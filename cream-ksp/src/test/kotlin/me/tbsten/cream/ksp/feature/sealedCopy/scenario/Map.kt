package me.tbsten.cream.ksp.feature.sealedCopy.scenario

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier.OVERRIDE
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import me.tbsten.cream.SealedCopy
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.classNameOf
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

/** A non-`data class` child that delegates via `@SealedCopy.Via`, accepting every abstract property by name. */
private fun mappedChild(
    parent: ClassName,
    self: ClassName,
    prop: Prop,
): TypeSpec {
    val cloneWith =
        FunSpec
            .builder("cloneWith")
            .addAnnotation(AnnotationSpec.builder(SealedCopy.Via::class).build())
            .addParameter(ParameterSpec.builder(prop.name, prop.type).defaultValue("this.%N", prop.name).build())
            .returns(self)
            .addStatement("return %T(%N)", self, prop.name)
            .build()
    return TypeSpec
        .classBuilder(self.simpleName)
        .addSuperinterface(parent)
        .primaryConstructor(FunSpec.constructorBuilder().addParameter(prop.name, prop.type).build())
        .addProperty(
            PropertySpec
                .builder(prop.name, prop.type)
                .addModifiers(OVERRIDE)
                .initializer(prop.name)
                .build(),
        ).addFunction(cloneWith)
        .build()
}

/**
 * A non-`data class` child whose `@SealedCopy.Via` delegate takes [renamedProp] under a different parameter name
 * ([renamedParamName]), bound back to the abstract property via `@SealedCopy.Map`. This is the case that used to
 * mis-generate an infinitely-recursing call: the delegate does not accept every abstract property under its own
 * name, so the call must use the delegate's parameter names (`... = <abstract property>`).
 */
private fun mappedRenamedChild(
    parent: ClassName,
    self: ClassName,
    matchedProp: Prop,
    renamedProp: Prop,
    renamedParamName: String,
): TypeSpec {
    val renamedParam =
        ParameterSpec
            .builder(renamedParamName, renamedProp.type)
            .addAnnotation(AnnotationSpec.builder(SealedCopy.Map::class).addMember("%S", renamedProp.name).build())
            .build()
    val cloneWith =
        FunSpec
            .builder("cloneWith")
            .addAnnotation(AnnotationSpec.builder(SealedCopy.Via::class).build())
            .addParameter(ParameterSpec.builder(matchedProp.name, matchedProp.type).build())
            .addParameter(renamedParam)
            .returns(self)
            .addStatement("return %T(%N = %N, %N = %N)", self, matchedProp.name, matchedProp.name, renamedProp.name, renamedParamName)
            .build()
    return TypeSpec
        .classBuilder(self.simpleName)
        .addSuperinterface(parent)
        .primaryConstructor(
            FunSpec
                .constructorBuilder()
                .addParameter(matchedProp.name, matchedProp.type)
                .addParameter(renamedProp.name, renamedProp.type)
                .build(),
        ).addProperty(
            PropertySpec
                .builder(matchedProp.name, matchedProp.type)
                .addModifiers(OVERRIDE)
                .initializer(matchedProp.name)
                .build(),
        ).addProperty(
            PropertySpec
                .builder(renamedProp.name, renamedProp.type)
                .addModifiers(OVERRIDE)
                .initializer(renamedProp.name)
                .build(),
        ).addFunction(cloneWith)
        .build()
}

internal fun mapScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        "mappedNonDataChild" to
            sealedCopy(
                sealedInterfaceParent(
                    "Source",
                    abstractProps = listOf(Prop("name")),
                    children = listOf(mappedChild(classNameOf("Source"), classNameOf("Source", "Custom"), Prop("name"))),
                ),
            ),
        "mappedAmongDataChildren" to
            sealedCopy(
                sealedInterfaceParent(
                    "Source",
                    abstractProps = listOf(Prop("name")),
                    children =
                        listOf(
                            childClass("Loading", classNameOf("Source"), overrides = listOf(Prop("name"))),
                            mappedChild(classNameOf("Source"), classNameOf("Source", "Custom"), Prop("name")),
                        ),
                ),
            ),
        // The delegate accepts a differently-named parameter for one abstract property (`label` -> `newLabel`),
        // exercising the fix for the infinitely-recursing subset-signature delegate (issue #162).
        "mappedRenamedSubsetChild" to
            sealedCopy(
                sealedInterfaceParent(
                    "Source",
                    abstractProps = listOf(Prop("name"), Prop("label")),
                    children =
                        listOf(
                            childClass("Loading", classNameOf("Source"), overrides = listOf(Prop("name"), Prop("label"))),
                            mappedRenamedChild(
                                classNameOf("Source"),
                                classNameOf("Source", "Custom"),
                                matchedProp = Prop("name"),
                                renamedProp = Prop("label"),
                                renamedParamName = "newLabel",
                            ),
                        ),
                ),
            ),
    )
