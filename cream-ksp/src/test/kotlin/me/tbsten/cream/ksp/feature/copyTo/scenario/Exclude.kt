package me.tbsten.cream.ksp.feature.copyTo.scenario

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.KModifier.DATA
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.TypeVariableName
import me.tbsten.cream.CopyTo
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.classNameOf
import me.tbsten.cream.ksp.testing.poet.dataClass
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

private val EXCLUDE = AnnotationSpec.builder(CopyTo.Exclude::class).build()

internal fun excludeScenarios(): Generator<SnapshotScenario> {
    val t = TypeVariableName("T")
    return Generator.snapshotScenarios(
        "excludedProperty" to
            copyTo(
                dataClass("Source", Prop("name"), Prop("count", INT, paramAnnotation = EXCLUDE)),
                dataClass("Target", Prop("name"), Prop("count", INT)),
            ),
        "excludeNoEffect" to
            copyTo(
                dataClass("Source", Prop("name"), Prop("sourceOnly", INT, paramAnnotation = EXCLUDE)),
                dataClass("Target", Prop("name")),
            ),
        "genericExclude" to
            copyTo(
                dataClass("Source", Prop("name"), Prop("item", t, paramAnnotation = EXCLUDE), typeVariables = listOf(t)),
                dataClass("Target", Prop("name"), Prop("item", t), typeVariables = listOf(t)),
            ),
        "nullableExclude" to
            copyTo(
                dataClass("Source", Prop("note", STRING.copy(nullable = true), paramAnnotation = EXCLUDE)),
                dataClass("Target", Prop("note", STRING.copy(nullable = true))),
            ),
        "allParamsExcluded" to
            copyTo(
                dataClass(
                    "Source",
                    Prop("name", paramAnnotation = EXCLUDE),
                    Prop("count", INT, paramAnnotation = EXCLUDE),
                ),
                dataClass("Target", Prop("name"), Prop("count", INT)),
            ),
        "mapAndExclude" to
            SnapshotScenario(
                TypeSpec
                    .classBuilder("Source")
                    .addModifiers(DATA)
                    .primaryConstructor(
                        FunSpec
                            .constructorBuilder()
                            .addParameter(
                                ParameterSpec
                                    .builder("sourceName", STRING)
                                    .addAnnotation(
                                        AnnotationSpec.builder(CopyTo.Map::class).addMember("%S", "targetName").build(),
                                    ).addAnnotation(EXCLUDE)
                                    .build(),
                            ).addParameter("shared", STRING)
                            .build(),
                    ).addProperty(PropertySpec.builder("sourceName", STRING).initializer("sourceName").build())
                    .addProperty(PropertySpec.builder("shared", STRING).initializer("shared").build())
                    .build()
                    .withCopyTo(classNameOf("Target")),
                dataClass("Target", Prop("targetName"), Prop("shared")),
            ),
    )
}
