package me.tbsten.cream.ksp.feature.childOptionals.scenario

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.INT
import me.tbsten.cream.CopyVisibility
import me.tbsten.cream.ParentOptional
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.classNameOf
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

private fun parentOptional(
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

/**
 * `@ChildOptionals(propertyName = ...)` — the sweep-wide accessor-name template — and how a
 * property's own `@ParentOptional` layers over it (most specific wins per argument).
 */
internal fun propertyNameScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        // The template renames every swept accessor at once; same-name contributions still merge
        // because they resolve to the same name.
        "sweepWideTemplate" to
            childOptionals(
                sealedInterfaceParent(
                    "Source",
                    children =
                        listOf(
                            childClass(
                                "Success",
                                classNameOf("Source"),
                                props = listOf(Prop("data"), Prop("count", INT)),
                            ),
                            childClass("Failure", classNameOf("Source"), props = listOf(Prop("data"))),
                            objectChild("Loading", classNameOf("Source")),
                        ),
                ),
                propertyNameCode = defaultAccessorPropertyNamePlus("OrNull"),
            ),
        // A property's own propertyName wins over the sweep-wide template.
        "propertyOverridesSweepTemplate" to
            childOptionals(
                sealedInterfaceParent(
                    "Source",
                    children =
                        listOf(
                            childClass(
                                "Success",
                                classNameOf("Source"),
                                props =
                                    listOf(
                                        Prop("data"),
                                        Prop("code", INT, paramAnnotation = parentOptional(propertyName = "resultCode")),
                                    ),
                            ),
                            objectChild("Loading", classNameOf("Source")),
                        ),
                ),
                propertyNameCode = defaultAccessorPropertyNamePlus("OrNull"),
            ),
        // A bare @ParentOptional inside a sweep leaves every argument unset, so the sweep's
        // template AND its visibility both still apply to that property's accessor.
        "bareParentOptionalInheritsSweepArguments" to
            childOptionals(
                sealedInterfaceParent(
                    "Source",
                    children =
                        listOf(
                            childClass(
                                "Success",
                                classNameOf("Source"),
                                props = listOf(Prop("data", paramAnnotation = parentOptional())),
                            ),
                            objectChild("Loading", classNameOf("Source")),
                        ),
                ),
                visibility = CopyVisibility.INTERNAL,
                propertyNameCode = defaultAccessorPropertyNamePlus("OrNull"),
            ),
        // The parent-visible filter compares the RESOLVED name: a swept override normally gets no
        // accessor (the member wins), but the template renames it away from the member, so it does.
        "templateRenamesAwayFromParentMember" to
            childOptionals(
                sealedInterfaceParent(
                    "Source",
                    abstractProps = listOf(Prop("data")),
                    children =
                        listOf(
                            childClass("Success", classNameOf("Source"), overrides = listOf(Prop("data"))),
                            childClass("Failure", classNameOf("Source"), overrides = listOf(Prop("data"))),
                        ),
                ),
                propertyNameCode = defaultAccessorPropertyNamePlus("OrNull"),
            ),
    )
