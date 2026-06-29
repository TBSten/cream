package me.tbsten.cream.test.copyMapping.lib

/**
 * Simulated external-library models that live in a different package than the `@CopyMapping` holder
 * (issue #145). The generated copy function must be emitted into the holder's package, not into this
 * one, so it stays discoverable where the mapping is declared.
 */
data class CrossSource(
    val shared: String,
    val sourceOnly: Int,
)

data class CrossTarget(
    val shared: String,
    val targetOnly: Int,
)
