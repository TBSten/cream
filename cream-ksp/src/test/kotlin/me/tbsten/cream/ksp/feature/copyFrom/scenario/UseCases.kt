package me.tbsten.cream.ksp.feature.copyFrom.scenario

import com.squareup.kotlinpoet.BOOLEAN
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier.DATA
import com.squareup.kotlinpoet.KModifier.OVERRIDE
import com.squareup.kotlinpoet.KModifier.SEALED
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeSpec
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.classNameOf
import me.tbsten.cream.ksp.testing.poet.dataClass

private val NULLABLE_STRING = STRING.copy(nullable = true)

private fun uiStateChild(
    name: String,
    parent: ClassName,
    props: List<Prop>,
    overrides: Set<String>,
): TypeSpec {
    val constructor = FunSpec.constructorBuilder()
    props.forEach { constructor.addParameter(it.name, it.type) }
    return TypeSpec
        .classBuilder(name)
        .addModifiers(DATA)
        .addSuperinterface(parent)
        .primaryConstructor(constructor.build())
        .apply {
            props.forEach { p ->
                addProperty(
                    PropertySpec
                        .builder(p.name, p.type)
                        .apply { if (p.name in overrides) addModifiers(OVERRIDE) }
                        .initializer(p.name)
                        .build(),
                )
            }
        }.build()
}

internal fun itemDetailTransitionsFromTargetsUseCase(): SnapshotScenario {
    val parent = classNameOf("ItemDetailUiState")
    val loading = parent.nestedClass("Loading")
    val shared = listOf(Prop("itemId"), Prop("isBookmarked", BOOLEAN), Prop("snackbarMessage", NULLABLE_STRING))
    val sharedNames = shared.map { it.name }.toSet()
    return SnapshotScenario(
        TypeSpec
            .interfaceBuilder("ItemDetailUiState")
            .addModifiers(SEALED)
            .apply { shared.forEach { addProperty(PropertySpec.builder(it.name, it.type).build()) } }
            .addType(uiStateChild("Loading", parent, props = shared, overrides = sharedNames))
            .addType(
                uiStateChild(
                    "Success",
                    parent,
                    props = listOf(shared[0], Prop("item", classNameOf("Item")), shared[1], shared[2]),
                    overrides = sharedNames,
                ).withCopyFrom(loading),
            ).addType(
                uiStateChild(
                    "Error",
                    parent,
                    props = listOf(shared[0], Prop("message"), shared[1], shared[2]),
                    overrides = sharedNames,
                ).withCopyFrom(loading),
            ).build(),
        dataClass("Item", Prop("id")),
    )
}
