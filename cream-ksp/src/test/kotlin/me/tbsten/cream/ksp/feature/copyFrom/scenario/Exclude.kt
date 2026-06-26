package me.tbsten.cream.ksp.feature.copyFrom.scenario

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.KModifier.DATA
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeSpec
import me.tbsten.cream.CopyFrom
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.classNameOf
import me.tbsten.cream.ksp.testing.poet.dataClass
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

private val EXCLUDE = AnnotationSpec.builder(CopyFrom.Exclude::class).build()

internal fun excludeScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        "excludedProperty" to
            copyFrom(
                dataClass("Target", Prop("name"), Prop("count", INT, paramAnnotation = EXCLUDE)),
                dataClass("Source", Prop("name"), Prop("count", INT)),
            ),
        "excludeNoEffect" to
            copyFrom(
                dataClass("Target", Prop("name"), Prop("targetOnly", INT, paramAnnotation = EXCLUDE)),
                dataClass("Source", Prop("name")),
            ),
        "mapAndExclude" to
            SnapshotScenario(
                TypeSpec
                    .classBuilder("Target")
                    .addModifiers(DATA)
                    .primaryConstructor(
                        FunSpec
                            .constructorBuilder()
                            .addParameter(
                                ParameterSpec
                                    .builder("targetName", STRING)
                                    .addAnnotation(
                                        AnnotationSpec.builder(CopyFrom.Map::class).addMember("%S", "sourceName").build(),
                                    ).addAnnotation(EXCLUDE)
                                    .build(),
                            ).addParameter("shared", STRING)
                            .build(),
                    ).addProperty(PropertySpec.builder("targetName", STRING).initializer("targetName").build())
                    .addProperty(PropertySpec.builder("shared", STRING).initializer("shared").build())
                    .build()
                    .withCopyFrom(classNameOf("Source")),
                dataClass("Source", Prop("sourceName"), Prop("shared")),
            ),
    )
