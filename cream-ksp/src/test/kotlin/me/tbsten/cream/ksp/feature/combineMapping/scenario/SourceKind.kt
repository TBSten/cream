package me.tbsten.cream.ksp.feature.combineMapping.scenario

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.KModifier.VALUE
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeSpec
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.clazz
import me.tbsten.cream.ksp.testing.poet.dataClass
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

private val JVM_INLINE = AnnotationSpec.builder(ClassName("kotlin.jvm", "JvmInline")).build()

private val sourceB = dataClass("SourceB", Prop("extra", INT))

private fun annotationClassSource(
    name: String,
    prop: String,
): TypeSpec =
    TypeSpec
        .annotationBuilder(name)
        .primaryConstructor(FunSpec.constructorBuilder().addParameter(prop, STRING).build())
        .addProperty(PropertySpec.builder(prop, STRING).initializer(prop).build())
        .build()

internal fun sourceKindScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        "valueClassSource" to
            combineMapping(
                mappingHolder(),
                listOf(clazz("SourceA", Prop("value"), modifiers = listOf(VALUE), annotations = listOf(JVM_INLINE)), sourceB),
                dataClass("Target", Prop("value"), Prop("extra", INT)),
            ),
        "plainClassSource" to
            combineMapping(
                mappingHolder(),
                listOf(clazz("SourceA", Prop("name")), sourceB),
                dataClass("Target", Prop("name"), Prop("extra", INT)),
            ),
        "annotationClassSource" to
            combineMapping(
                mappingHolder(),
                listOf(annotationClassSource("SourceA", "value"), sourceB),
                dataClass("Target", Prop("value"), Prop("extra", INT)),
            ),
    )
