package me.tbsten.cream.ksp.feature.sealedCopy.scenario

import com.squareup.kotlinpoet.BOOLEAN
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.KModifier.DATA
import com.squareup.kotlinpoet.KModifier.OVERRIDE
import com.squareup.kotlinpoet.LIST
import com.squareup.kotlinpoet.LONG
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.THROWABLE
import com.squareup.kotlinpoet.TypeSpec
import me.tbsten.cream.NonCopyableStrategy
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.classNameOf
import me.tbsten.cream.ksp.testing.poet.dataClass

private val NULLABLE_STRING = STRING.copy(nullable = true)

private fun feedLoadingObject(parent: ClassName): TypeSpec =
    TypeSpec
        .objectBuilder("Loading")
        .addModifiers(DATA)
        .addSuperinterface(parent)
        .addProperty(
            PropertySpec
                .builder("isRefreshing", BOOLEAN)
                .addModifiers(OVERRIDE)
                .getter(FunSpec.getterBuilder().addStatement("return false").build())
                .build(),
        ).addProperty(
            PropertySpec
                .builder("bannerMessage", NULLABLE_STRING)
                .addModifiers(OVERRIDE)
                .getter(FunSpec.getterBuilder().addStatement("return null").build())
                .build(),
        ).build()

internal fun feedRefreshUseCase(): SnapshotScenario {
    val parent = classNameOf("FeedUiState")
    val shared = listOf(Prop("isRefreshing", BOOLEAN), Prop("bannerMessage", NULLABLE_STRING))
    return sealedCopy(
        sealedInterfaceParent(
            "FeedUiState",
            abstractProps = shared,
            children =
                listOf(
                    feedLoadingObject(parent),
                    childClass(
                        "Content",
                        parent,
                        overrides = shared,
                        extras = listOf(Prop("posts", LIST.parameterizedBy(classNameOf("Post")))),
                    ),
                    childClass("Error", parent, overrides = shared, extras = listOf(Prop("throwable", THROWABLE))),
                ),
        ),
        dataClass("Post", Prop("id")),
        nonCopyableStrategy = NonCopyableStrategy.RETURN_AS_IS,
    )
}

internal fun counterSharedContextUseCase(): SnapshotScenario {
    val parent = classNameOf("CounterUiState")
    val shared = listOf(Prop("userId"), Prop("sessionStartedAt", LONG))
    return sealedCopy(
        sealedInterfaceParent(
            "CounterUiState",
            abstractProps = shared,
            children =
                listOf(
                    childClass("Idle", parent, overrides = shared),
                    childClass("Counting", parent, overrides = shared, extras = listOf(Prop("count", INT))),
                    childClass("Finished", parent, overrides = shared, extras = listOf(Prop("total", INT))),
                ),
        ),
    )
}
