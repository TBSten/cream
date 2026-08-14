package me.tbsten.cream.ksp.feature.childOptionals.scenario

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier.DATA
import com.squareup.kotlinpoet.KModifier.PRIVATE
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.TypeVariableName
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.classNameOf
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

internal fun propertyFilteringScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        "parentVisiblePropertiesSkipped" to
            childOptionals(
                sealedInterfaceParent(
                    "Source",
                    abstractProps = listOf(Prop("shared")),
                    children =
                        listOf(
                            childClass("Success", classNameOf("Source"), overrides = listOf(Prop("shared")), props = listOf(Prop("data"))),
                            childClass("Failure", classNameOf("Source"), overrides = listOf(Prop("shared"))),
                        ),
                ),
            ),
        "privatePropertiesSkippedSilently" to
            childOptionals(
                sealedInterfaceParent(
                    "Source",
                    children =
                        listOf(
                            childClass(
                                "Success",
                                classNameOf("Source"),
                                props = listOf(Prop("data"), Prop("secret", visibility = PRIVATE)),
                            ),
                            objectChild("Loading", classNameOf("Source")),
                        ),
                ),
            ),
        "privateChildClassSkippedSilently" to
            childOptionals(
                sealedInterfaceParent(
                    "Source",
                    children =
                        listOf(
                            childClass("Success", classNameOf("Source"), props = listOf(Prop("data"))),
                        ),
                ),
                // A file-private leaf: the generated `is` branch could not reference it, so the
                // blanket sweep skips the whole leaf silently (its `secret` gets no accessor).
                childClass(
                    "Hidden",
                    classNameOf("Source"),
                    props = listOf(Prop("secret")),
                    modifiers = listOf(PRIVATE, DATA),
                ),
            ),
        "bodyPropertiesIncluded" to
            childOptionals(
                sealedInterfaceParent(
                    "Source",
                    children =
                        listOf(
                            childClass(
                                "Success",
                                classNameOf("Source"),
                                props = listOf(Prop("data")),
                                bodyProps = listOf(PropertySpec.builder("note", STRING).initializer("%S", "memo").build()),
                            ),
                            objectChild("Loading", classNameOf("Source")),
                        ),
                ),
            ),
        // `Tagged<M>`'s `meta: M` references a type parameter the parent does not pin: the sweep
        // skips it WITH a warning (pinned in the Console facet) while `label` still generates —
        // an explicit @ParentOptional on such a property errors instead (see
        // ParentOptionalInvalidUsageTest.typeParameterNotPinnedByParent).
        "unpinnedGenericPropertySkippedWithWarning" to
            childOptionals(
                sealedInterfaceParent(
                    "Source",
                    children =
                        listOf(
                            TypeSpec
                                .classBuilder("Tagged")
                                .addModifiers(DATA)
                                .addTypeVariable(TypeVariableName("M"))
                                .addSuperinterface(classNameOf("Source"))
                                .primaryConstructor(
                                    FunSpec
                                        .constructorBuilder()
                                        .addParameter("meta", TypeVariableName("M"))
                                        .addParameter("label", STRING)
                                        .build(),
                                ).addProperty(PropertySpec.builder("meta", TypeVariableName("M")).initializer("meta").build())
                                .addProperty(PropertySpec.builder("label", STRING).initializer("label").build())
                                .build(),
                            objectChild("Loading", classNameOf("Source")),
                        ),
                ),
            ),
        // A member *extension* property (`val String.suffix`) cannot be read by an accessor
        // (no extension receiver to supply) — the sweep skips it silently; `data` still generates.
        "extensionPropertiesSkippedSilently" to
            childOptionals(
                sealedInterfaceParent(
                    "Source",
                    children =
                        listOf(
                            childClass(
                                "Success",
                                classNameOf("Source"),
                                props = listOf(Prop("data")),
                                bodyProps =
                                    listOf(
                                        PropertySpec
                                            .builder("suffix", STRING)
                                            .receiver(STRING)
                                            .getter(FunSpec.getterBuilder().addStatement("return takeLast(1)").build())
                                            .build(),
                                    ),
                            ),
                            objectChild("Loading", classNameOf("Source")),
                        ),
                ),
            ),
    )
