package me.tbsten.cream.ksp.feature.copyMapping.scenario

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.KModifier.PRIVATE
import com.squareup.kotlinpoet.KModifier.VALUE
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeSpec
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.classNameOf
import me.tbsten.cream.ksp.testing.poet.clazz
import me.tbsten.cream.ksp.testing.poet.containing
import me.tbsten.cream.ksp.testing.poet.dataClass

private val NULLABLE_STRING = STRING.copy(nullable = true)
private val NULLABLE_INT = INT.copy(nullable = true)
private val JVM_INLINE = AnnotationSpec.builder(ClassName("kotlin.jvm", "JvmInline")).build()

internal fun layerModelMappingUseCase(): SnapshotScenario {
    val holder =
        TypeSpec
            .objectBuilder("ItemMapping")
            .addModifiers(PRIVATE)
            .build()
            .withCopyMapping(
                source = classNameOf("GetItemApiResponse", "Item"),
                target = classNameOf("Item"),
                properties = listOf("imageUrl" to "thumbnailUrl"),
            )
    val apiResponse =
        dataClass("GetItemApiResponse", Prop("item", classNameOf("GetItemApiResponse", "Item")))
            .containing(
                dataClass(
                    "Item",
                    Prop("itemId"),
                    Prop("name"),
                    Prop("price", NULLABLE_INT),
                    Prop("description"),
                    Prop("imageUrl", NULLABLE_STRING),
                    Prop("stock", INT),
                    Prop("updatedAt"),
                ),
            )
    val domainItem =
        dataClass(
            "Item",
            Prop("itemId", classNameOf("ItemId")),
            Prop("name"),
            Prop("price", NULLABLE_INT),
            Prop("description"),
            Prop("thumbnailUrl", NULLABLE_STRING),
            Prop("stock", INT),
            Prop("updatedAt", ClassName("java.time", "Instant")),
        )
    val itemId = clazz("ItemId", Prop("value"), modifiers = listOf(VALUE), annotations = listOf(JVM_INLINE))
    return SnapshotScenario(holder, apiResponse, domainItem, itemId)
}
