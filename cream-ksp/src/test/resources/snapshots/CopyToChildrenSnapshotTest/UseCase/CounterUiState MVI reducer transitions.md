## Input:me.tbsten.cream.generated.CounterUiState

```kt
package me.tbsten.cream.generated

import kotlin.Int
import kotlin.Long
import kotlin.String
import me.tbsten.cream.CopyToChildren

@CopyToChildren
public sealed interface CounterUiState {
  public val userId: String

  public val sessionStartedAt: Long

  public data class Idle(
    override val userId: String,
    override val sessionStartedAt: Long,
  ) : CounterUiState

  public data class Counting(
    override val userId: String,
    override val sessionStartedAt: Long,
    public val count: Int,
  ) : CounterUiState

  public data class Finished(
    override val userId: String,
    override val sessionStartedAt: Long,
    public val total: Int,
  ) : CounterUiState
}
```

## KSP options

```kt
ksp {
    arg("copyFunNamePrefix", "copyTo" /* default */)
    arg("copyFunNamingStrategy", "under-package" /* default */)
    arg("escapeDot", "lower-camel-case" /* default */)
    arg("notCopyToObject", "false" /* default */)
    arg("defaultVisibility", "INHERIT" /* default */)
}
```

## Output:ExitCode

```kt
OK
```

## Output:Console

```kt

```

## Output:Generated sources

````kt
// file: CopyToChildren__CounterUiState.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyToChildren] annotation of [CounterUiState])
 * 
 * CounterUiState -> CounterUiState.Counting copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = CounterUiState(...)
 * val target = source.copyToCounterUiStateCounting(count = count)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = CounterUiState(...)
 * val target = source.copyToCounterUiStateCounting(count = count, property = value)
 * ```
 * 
 * 
 * @see CounterUiState
 * @see CounterUiState.Counting
 */
public fun  me.tbsten.cream.generated.CounterUiState.copyToCounterUiStateCounting(
    userId: String = this.userId,
    sessionStartedAt: Long = this.sessionStartedAt,
    count: Int,
) : me.tbsten.cream.generated.CounterUiState.Counting = me.tbsten.cream.generated.CounterUiState.Counting(
    userId = userId,
    sessionStartedAt = sessionStartedAt,
    count = count,
)

/**
 * (Auto generate by @[CopyToChildren] annotation of [CounterUiState])
 * 
 * CounterUiState -> CounterUiState.Finished copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = CounterUiState(...)
 * val target = source.copyToCounterUiStateFinished(total = total)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = CounterUiState(...)
 * val target = source.copyToCounterUiStateFinished(total = total, property = value)
 * ```
 * 
 * 
 * @see CounterUiState
 * @see CounterUiState.Finished
 */
public fun  me.tbsten.cream.generated.CounterUiState.copyToCounterUiStateFinished(
    userId: String = this.userId,
    sessionStartedAt: Long = this.sessionStartedAt,
    total: Int,
) : me.tbsten.cream.generated.CounterUiState.Finished = me.tbsten.cream.generated.CounterUiState.Finished(
    userId = userId,
    sessionStartedAt = sessionStartedAt,
    total = total,
)

/**
 * (Auto generate by @[CopyToChildren] annotation of [CounterUiState])
 * 
 * CounterUiState -> CounterUiState.Idle copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = CounterUiState(...)
 * val target = source.copyToCounterUiStateIdle()
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = CounterUiState(...)
 * val target = source.copyToCounterUiStateIdle(property = value)
 * ```
 * 
 * 
 * @see CounterUiState
 * @see CounterUiState.Idle
 */
public fun  me.tbsten.cream.generated.CounterUiState.copyToCounterUiStateIdle(
    userId: String = this.userId,
    sessionStartedAt: Long = this.sessionStartedAt,
) : me.tbsten.cream.generated.CounterUiState.Idle = me.tbsten.cream.generated.CounterUiState.Idle(
    userId = userId,
    sessionStartedAt = sessionStartedAt,
)
````
