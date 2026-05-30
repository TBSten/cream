package me.tbsten.cream.test.sealedCopy

import me.tbsten.cream.NonCopyableStrategy
import me.tbsten.cream.SealedCopy

/**
 * Exercises `@SealedCopy` being `@Repeatable`: two annotations on the same sealed type
 * should produce two independent extensions whose `funName` and `nonCopyableStrategy`
 * are taken from each annotation site.
 *
 * `withUpdated` uses `RETURN_AS_IS` — `Empty` collapses to itself, function returns
 * non-nullable.
 *
 * `withUpdatedOrNull` uses `RETURN_NULL` — `Empty` collapses to `null`, function
 * widens its return type to nullable.
 */
@SealedCopy(funName = "withUpdated", nonCopyableStrategy = NonCopyableStrategy.RETURN_AS_IS)
@SealedCopy(funName = "withUpdatedOrNull", nonCopyableStrategy = NonCopyableStrategy.RETURN_NULL)
sealed interface MultiSealedCopyState {
    val sessionId: String

    data class Loading(
        override val sessionId: String,
    ) : MultiSealedCopyState

    data object Empty : MultiSealedCopyState {
        override val sessionId: String = "empty"
    }
}
