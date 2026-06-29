## Input:me.tbsten.cream.generated.Target

```kt
package me.tbsten.cream.generated

import kotlin.Int
import kotlin.String
import me.tbsten.cream.CopyFrom

@CopyFrom(Source::class)
public sealed interface Target {
  public val name: String

  public sealed interface First : Target {
    public sealed interface Second : First {
      public data class Done(
        override val name: String,
      ) : Second

      public data class Refreshing(
        override val name: String,
        public val extra: Int,
      ) : Second
    }
  }
}

public data class Source(
  public val name: String,
)
```

## KSP options

```kt
ksp {
    arg("copyFunNamePrefix", "copyTo" /* default */)
    arg("copyFunNamingStrategy", "under-package" /* default */)
    arg("escapeDot", "lower-camel-case" /* default */)
    arg("notCopyToObject", "false" /* default */)
    arg("defaultVisibility", "INTERNAL")
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
// file: CopyFrom__Target.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyFrom] annotation of [Target])
 * 
 * Source -> Target.First.Second.Done copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToTargetFirstSecondDone()
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToTargetFirstSecondDone(property = value)
 * ```
 * 
 * 
 * @see Source
 * @see Target.First.Second.Done
 */
internal fun  me.tbsten.cream.generated.Source.copyToTargetFirstSecondDone(
    name: String = this.name,
) : me.tbsten.cream.generated.Target.First.Second.Done = me.tbsten.cream.generated.Target.First.Second.Done(
    name = name,
)

/**
 * (Auto generate by @[CopyFrom] annotation of [Target])
 * 
 * Source -> Target.First.Second.Refreshing copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToTargetFirstSecondRefreshing(extra = extra)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToTargetFirstSecondRefreshing(extra = extra, property = value)
 * ```
 * 
 * 
 * @see Source
 * @see Target.First.Second.Refreshing
 */
internal fun  me.tbsten.cream.generated.Source.copyToTargetFirstSecondRefreshing(
    name: String = this.name,
    extra: Int,
) : me.tbsten.cream.generated.Target.First.Second.Refreshing = me.tbsten.cream.generated.Target.First.Second.Refreshing(
    name = name,
    extra = extra,
)
````
