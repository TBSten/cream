package me.tbsten.cream.ksp.feature.callFrom.scenario

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.KModifier.VALUE
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.generator.clazz.GENERATED_PACKAGE
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.clazz
import me.tbsten.cream.ksp.testing.poet.dataClass
import me.tbsten.cream.ksp.testing.poet.inputFileSpec
import me.tbsten.cream.ksp.testing.poet.sealedInterface
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

private val JVM_INLINE = AnnotationSpec.builder(ClassName("kotlin.jvm", "JvmInline")).build()

private const val OTHER_PACKAGE = "$GENERATED_PACKAGE.other"

internal fun sourceKindScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        "valueClassSource" to
            callFrom(
                fn("consume", param("value")),
                clazz("Args", Prop("value"), modifiers = listOf(VALUE), annotations = listOf(JVM_INLINE)),
            ),
        "plainClassSource" to
            callFrom(
                fn("consume", param("name"), param("extra", INT)),
                clazz("Args", Prop("name")),
            ),
        "sealedInterfaceSource" to
            callFrom(
                fn("consume", param("name"), param("extra", INT)),
                sealedInterface("Args", "name"),
            ),
        // The source class lives in a different package from the annotated function: the
        // generated bridge must reference it by its fully-qualified name.
        "sourceInDifferentPackage" to
            SnapshotScenario(
                files =
                    listOf(
                        FileSpec
                            .builder(GENERATED_PACKAGE, "$GENERATED_PACKAGE.consume")
                            .addFunction(
                                fn("consume", param("name"), param("extra", INT))
                                    .withCallFrom(ClassName(OTHER_PACKAGE, "OtherPackageArgs")),
                            ).build(),
                        inputFileSpec(OTHER_PACKAGE, dataClass("OtherPackageArgs", Prop("name"))),
                    ),
            ),
    )
