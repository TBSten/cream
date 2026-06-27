package me.tbsten.cream.test.copyFrom

import me.tbsten.cream.CopyFrom

/**
 * A sealed `@CopyFrom` target. cream fans out to every transitive concrete leaf
 * (`First.Second.Done` / `First.Second.Refreshing`) and generates one copy function per leaf
 * whose receiver is [SealedTargetSource]. Mirrors the reproduction of issue #144 (the generated
 * KDoc must attribute generation to the `@CopyFrom`-annotated [SealedTargetState], not the source).
 */
@CopyFrom(SealedTargetSource::class)
sealed interface SealedTargetState {
    val name: String

    sealed interface First : SealedTargetState {
        sealed interface Second : First {
            data class Done(
                override val name: String,
            ) : Second

            data class Refreshing(
                override val name: String,
                val extra: Int,
            ) : Second
        }
    }
}

/**
 * Source for [SealedTargetState] copy functions.
 *
 * @property name
 */
data class SealedTargetSource(
    val name: String,
)
