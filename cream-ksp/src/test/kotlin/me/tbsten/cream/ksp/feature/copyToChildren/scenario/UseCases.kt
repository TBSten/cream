package me.tbsten.cream.ksp.feature.copyToChildren.scenario

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.KModifier.DATA
import com.squareup.kotlinpoet.KModifier.OVERRIDE
import com.squareup.kotlinpoet.KModifier.SEALED
import com.squareup.kotlinpoet.LIST
import com.squareup.kotlinpoet.LONG
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeSpec
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.classNameOf
import me.tbsten.cream.ksp.testing.poet.clazz
import me.tbsten.cream.ksp.testing.poet.dataClass

private fun phaseInterface(
    name: String,
    parent: ClassName,
    abstractProps: List<Prop>,
    vararg leaves: TypeSpec,
): TypeSpec =
    TypeSpec
        .interfaceBuilder(name)
        .addModifiers(SEALED)
        .addSuperinterface(parent)
        .apply { abstractProps.forEach { addProperty(PropertySpec.builder(it.name, it.type).build()) } }
        .apply { leaves.forEach { addType(it) } }
        .build()

private fun idleObject(parent: ClassName): TypeSpec =
    TypeSpec
        .objectBuilder("Idle")
        .addModifiers(DATA)
        .addSuperinterface(parent)
        .addProperty(
            PropertySpec
                .builder("sessionId", STRING)
                .addModifiers(OVERRIDE)
                .getter(FunSpec.getterBuilder().addStatement("return %S", "").build())
                .build(),
        ).build()

internal fun checkoutStateMachineUseCase(): SnapshotScenario {
    val root = classNameOf("CheckoutUiState")
    val cart = root.nestedClass("Cart")
    val payment = root.nestedClass("Payment")
    val sessionId = Prop("sessionId")
    val items = Prop("items", LIST.parameterizedBy(classNameOf("CartItem")))
    val address = Prop("address", classNameOf("Address"))
    return copyToChildren(
        sealedInterfaceParent(
            "CheckoutUiState",
            abstractProps = listOf(sessionId),
            children =
                listOf(
                    idleObject(root),
                    phaseInterface(
                        "Cart",
                        root,
                        abstractProps = listOf(items),
                        childClass("Editing", cart, overrides = listOf(sessionId, items)),
                        childClass("Validating", cart, overrides = listOf(sessionId, items)),
                    ),
                    phaseInterface(
                        "Payment",
                        root,
                        abstractProps = listOf(items, address),
                        childClass("SelectingMethod", payment, overrides = listOf(sessionId, items, address)),
                        childClass(
                            "Processing",
                            payment,
                            overrides = listOf(sessionId, items, address),
                            extras = listOf(Prop("method", classNameOf("PaymentMethod"))),
                        ),
                        childClass(
                            "Completed",
                            payment,
                            overrides = listOf(sessionId, items, address),
                            extras = listOf(Prop("orderId")),
                        ),
                    ),
                ),
        ),
        dataClass("CartItem", Prop("name")),
        dataClass("Address", Prop("line")),
        dataClass("PaymentMethod", Prop("name")),
    )
}

internal fun counterReducerUseCase(): SnapshotScenario {
    val parent = classNameOf("CounterUiState")
    val shared = listOf(Prop("userId"), Prop("sessionStartedAt", LONG))
    return copyToChildren(
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

internal fun komaSearchStateUseCase(): SnapshotScenario {
    val parent = classNameOf("SearchState")
    val shared =
        listOf(
            Prop("query"),
            Prop("sortOrder", classNameOf("SortOrder")),
            Prop("searchedAt", ClassName("java.time", "Instant").copy(nullable = true)),
        )
    return copyToChildren(
        sealedInterfaceParent(
            "SearchState",
            abstractProps = shared,
            children =
                listOf(
                    childClass("Loading", parent, overrides = shared),
                    childClass(
                        "Content",
                        parent,
                        overrides = shared,
                        extras = listOf(Prop("results", LIST.parameterizedBy(classNameOf("Item")))),
                    ),
                    childClass("Error", parent, overrides = shared, extras = listOf(Prop("message"))),
                ),
        ).toBuilder().addSuperinterface(classNameOf("State")).build(),
        TypeSpec.interfaceBuilder("State").build(),
        dataClass("Item", Prop("id")),
        clazz("SortOrder", Prop("key")),
    )
}
