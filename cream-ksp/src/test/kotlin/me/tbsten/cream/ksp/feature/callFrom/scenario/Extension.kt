package me.tbsten.cream.ksp.feature.callFrom.scenario

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.KModifier.SUSPEND
import com.squareup.kotlinpoet.LIST
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeVariableName
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.dataClass
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

/**
 * Top-level extension functions bridge as extension functions on the same receiver; the bridge
 * body reaches the original through the implicit receiver. (A member extension function —
 * double receiver — is a diagnostic, covered in `CallFromInvalidUsageTest`.)
 */
internal fun extensionScenarios(): Generator<SnapshotScenario> {
    val t = TypeVariableName("T")
    return Generator.snapshotScenarios(
        "topLevelExtension" to
            callFrom(
                FunSpec
                    .builder("greet")
                    .receiver(STRING)
                    .addParameter("name", STRING)
                    .returns(STRING)
                    .addStatement("return this + name")
                    .build(),
                dataClass("GreetArgs", Prop("name")),
            ),
        // The receiver's type parameter must be transcribed onto the bridge.
        "genericReceiverExtension" to
            callFrom(
                FunSpec
                    .builder("pick")
                    .addTypeVariable(t)
                    .receiver(LIST.parameterizedBy(t))
                    .addParameter("index", INT)
                    .returns(t)
                    .addStatement("return this[index]")
                    .build(),
                dataClass("PickArgs", Prop("index", INT)),
            ),
        // Nullability of the receiver and the suspend modifier must both survive.
        "nullableReceiverSuspendExtension" to
            callFrom(
                FunSpec
                    .builder("orValue")
                    .addModifiers(SUSPEND)
                    .receiver(STRING.copy(nullable = true))
                    .addParameter("fallback", STRING)
                    .returns(STRING)
                    .addStatement("return this ?: fallback")
                    .build(),
                dataClass("OrValueArgs", Prop("fallback")),
            ),
    )
}
