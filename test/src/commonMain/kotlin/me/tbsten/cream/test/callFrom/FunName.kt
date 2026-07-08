package me.tbsten.cream.test.callFrom

import me.tbsten.cream.CallFrom

data class BuildConfigArgs(
    val name: String,
    val size: Int,
)

// A custom `funName` renames the bridge to `createBuildConfig` (not an overload of `buildConfig`).
// The generated bridge still delegates to the original `buildConfig`, so it is not self-recursive.
@CallFrom(BuildConfigArgs::class, funName = "createBuildConfig")
fun buildConfig(
    name: String,
    size: Int,
): String = "$name:$size"

data class ScaleArgs(
    val factor: Int,
)

class Renderer(
    private val base: Int,
) {
    // A custom `funName` on a member function: the bridge is the extension `Renderer.scaledBy`,
    // whose body delegates to the original member `scale` through the receiver.
    @CallFrom(ScaleArgs::class, funName = "scaledBy")
    fun scale(factor: Int): Int = base * factor
}
