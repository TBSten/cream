package me.tbsten.cream.ksp.feature.childOptionals.scenario

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.KModifier.DATA
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import me.tbsten.cream.ChildOptionals
import me.tbsten.cream.ParentOptional
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.classNameOf
import me.tbsten.cream.ksp.testing.poet.dataClass
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

private val EXCLUDE = AnnotationSpec.builder(ChildOptionals.Exclude::class).build()
private val PARENT_OPTIONAL = AnnotationSpec.builder(ParentOptional::class).build()

/**
 * A leaf property carrying BOTH `@ParentOptional` and `@ChildOptionals.Exclude`: the explicit
 * opt-in wins over the sweep opt-out, so the `amount` accessor is still generated (and the exclude
 * emits no warning, because the property is deliberately generated — not an unmatched mistake).
 */
private fun parentOptionalOverridesExcludeScenario(): SnapshotScenario {
    val paid =
        TypeSpec
            .classBuilder("Paid")
            .addModifiers(DATA)
            .addSuperinterface(classNameOf("Source"))
            .primaryConstructor(
                FunSpec
                    .constructorBuilder()
                    .addParameter(
                        ParameterSpec
                            .builder("amount", INT)
                            .addAnnotation(PARENT_OPTIONAL)
                            .addAnnotation(EXCLUDE)
                            .build(),
                    ).build(),
            ).addProperty(PropertySpec.builder("amount", INT).initializer("amount").build())
            .build()
    return childOptionals(
        sealedInterfaceParent("Source", children = listOf(paid, objectChild("Unpaid", classNameOf("Source")))),
    )
}

internal fun excludeScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        // One excluded property is dropped entirely (no accessor) while its non-excluded sibling
        // still generates one.
        "singleExcludedPropertySkipped" to
            childOptionals(
                sealedInterfaceParent(
                    "Source",
                    children =
                        listOf(
                            childClass(
                                "Success",
                                classNameOf("Source"),
                                props = listOf(Prop("data"), Prop("trace", paramAnnotation = EXCLUDE)),
                            ),
                            objectChild("Loading", classNameOf("Source")),
                        ),
                ),
            ),
        // Two children share the `message` name; excluding one contributor drops just its `is`
        // branch from the merged accessor while the other child still contributes.
        "excludedContributorDroppedFromMerge" to
            childOptionals(
                sealedInterfaceParent(
                    "Source",
                    children =
                        listOf(
                            childClass(
                                "Success",
                                classNameOf("Source"),
                                props = listOf(Prop("message", paramAnnotation = EXCLUDE), Prop("data")),
                            ),
                            childClass("Failure", classNameOf("Source"), props = listOf(Prop("message"))),
                        ),
                ),
            ),
        // Every contributor to the `token` name is excluded, so no `token` accessor is generated at
        // all; the leaves' other properties still generate.
        "allContributorsExcludedNoAccessor" to
            childOptionals(
                sealedInterfaceParent(
                    "Source",
                    children =
                        listOf(
                            childClass(
                                "Success",
                                classNameOf("Source"),
                                props = listOf(Prop("token", paramAnnotation = EXCLUDE), Prop("data")),
                            ),
                            childClass(
                                "Failure",
                                classNameOf("Source"),
                                props = listOf(Prop("token", paramAnnotation = EXCLUDE), Prop("error")),
                            ),
                        ),
                ),
            ),
        // @ChildOptionals.Exclude on a property whose enclosing class is NOT part of any
        // @ChildOptionals hierarchy removes nothing -> KSP warning (pinned in Output:Console). The
        // sealed parent's own sweep still generates `data`.
        "excludeNoEffectWarns" to
            childOptionals(
                sealedInterfaceParent(
                    "Source",
                    children =
                        listOf(
                            childClass("Success", classNameOf("Source"), props = listOf(Prop("data"))),
                            objectChild("Loading", classNameOf("Source")),
                        ),
                ),
                dataClass("Unrelated", Prop("ignored", paramAnnotation = EXCLUDE)),
            ),
        "parentOptionalOverridesExclude" to parentOptionalOverridesExcludeScenario(),
    )
