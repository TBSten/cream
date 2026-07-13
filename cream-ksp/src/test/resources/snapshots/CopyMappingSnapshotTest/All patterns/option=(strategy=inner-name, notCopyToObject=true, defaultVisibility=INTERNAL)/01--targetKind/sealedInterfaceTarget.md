## Input:me.tbsten.cream.generated.Mapping

```kt
package me.tbsten.cream.generated

import kotlin.Int
import kotlin.String
import me.tbsten.cream.CopyMapping

@CopyMapping(
  Source::class,
  Target::class,
)
public object Mapping

public data class Source(
  public val name: String,
)

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
```

## KSP options

```kt
ksp {
    arg("copyFunNamePrefix", "copyTo" /* default */)
    arg("copyFunNamingStrategy", "inner-name")
    arg("escapeDot", "lower-camel-case" /* default */)
    arg("notCopyToObject", "true")
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
// file: CopyMapping__Mapping.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyMapping] annotation of [Mapping])
 * 
 * Source -> Target.First.Second.Done copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToFirstSecondDone()
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToFirstSecondDone(property = value)
 * ```
 * 
 * 
 * @see Source
 * @see Target.First.Second.Done
 */
internal fun  me.tbsten.cream.generated.Source.copyToFirstSecondDone(
    name: String = this.name,
) : me.tbsten.cream.generated.Target.First.Second.Done = me.tbsten.cream.generated.Target.First.Second.Done(
    name = name,
)

/**
 * (Auto generate by @[CopyMapping] annotation of [Mapping])
 * 
 * Source -> Target.First.Second.Refreshing copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToFirstSecondRefreshing(extra = extra)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToFirstSecondRefreshing(extra = extra, property = value)
 * ```
 * 
 * 
 * @see Source
 * @see Target.First.Second.Refreshing
 */
internal fun  me.tbsten.cream.generated.Source.copyToFirstSecondRefreshing(
    name: String = this.name,
    extra: Int,
) : me.tbsten.cream.generated.Target.First.Second.Refreshing = me.tbsten.cream.generated.Target.First.Second.Refreshing(
    name = name,
    extra = extra,
)
````
