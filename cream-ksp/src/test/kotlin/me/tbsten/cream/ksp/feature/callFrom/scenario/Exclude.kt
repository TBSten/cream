package me.tbsten.cream.ksp.feature.callFrom.scenario

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.STRING
import me.tbsten.cream.CallFrom
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.dataClass
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

private val EXCLUDE = AnnotationSpec.builder(CallFrom.Exclude::class).build()

private fun mapAnno(sourcePropertyName: String): AnnotationSpec = AnnotationSpec.builder(CallFrom.Map::class).addMember("%S", sourcePropertyName).build()

internal fun excludeScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        "excludedParameter" to
            callFrom(
                fn("track", param("name"), param("count", INT, EXCLUDE)),
                dataClass("TrackArgs", Prop("name"), Prop("count", INT)),
            ),
        "excludeNoEffect" to
            callFrom(
                fn("track", param("name"), param("paramOnly", INT, EXCLUDE)),
                dataClass("TrackArgs", Prop("name")),
            ),
        "mapAndExclude" to
            callFrom(
                fn("track", param("paramName", STRING, mapAnno("sourceName"), EXCLUDE), param("shared")),
                dataClass("TrackArgs", Prop("sourceName"), Prop("shared")),
            ),
        // @Exclude on a MATCHED parameter wins over the original function's own default: the
        // user explicitly asked for a required parameter, so it stays in the bridge without any
        // default (auto-copied or original).
        "excludeOnParamWithOriginalDefault" to
            callFrom(
                fn(
                    "track",
                    param("name"),
                    ParameterSpec
                        .builder("count", INT)
                        .defaultValue("%L", 0)
                        .addAnnotation(CallFrom.Exclude::class)
                        .build(),
                ),
                dataClass("TrackArgs", Prop("name"), Prop("count", INT)),
            ),
    )
