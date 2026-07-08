package me.tbsten.cream.ksp.feature.callFrom.scenario

import com.squareup.kotlinpoet.INT
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.dataClass
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

/**
 * `funName` customization. By default a bridge keeps the annotated function's own name (an
 * overload); a custom `funName` is a plain literal that renames the bridge — `@CallFrom` supports
 * no `CopyTarget*` naming tokens (there is no target class to render). The bridge body still
 * delegates to the *original* function, so a renamed bridge is not self-recursive.
 *
 * The "custom name causes a collision" direction is a diagnostic, covered in
 * `CallFromInvalidUsageTest` (`customFunNameBridgeClash`).
 */
internal fun funNameScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        // funName omitted -> the bridge overloads the annotated function's own name (the default).
        "defaultNameOverload" to
            callFrom(
                fn("build", param("name"), param("size", INT)),
                dataClass("BuildArgs", Prop("name"), Prop("size", INT)),
            ),
        // A custom literal renames the bridge; it still delegates to `build`, not to itself.
        "customLiteralName" to
            callFrom(
                fn("build", param("name"), param("size", INT)),
                dataClass("BuildArgs", Prop("name"), Prop("size", INT)),
                funName = "createFromArgs",
            ),
        // A custom name shared by every source's bridge stays collision-free: each bridge has a
        // distinct first-parameter (source) type, so the `of(...)` overloads coexist.
        "customNameMultipleSources" to
            callFrom(
                fn("consume", param("value")),
                dataClass("ArgsA", Prop("value")),
                dataClass("ArgsB", Prop("value")),
                funName = "of",
            ),
    )
