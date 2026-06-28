## Input:Input

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
    arg("copyFunNamePrefix", "to")
    arg("copyFunNamingStrategy", "under-package" /* default */)
    arg("escapeDot", "replace-to-underscore")
    arg("notCopyToObject", "false" /* default */)
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
 * val target = source.to_Target_First_Second_Done()
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.to_Target_First_Second_Done(property = value)
 * ```
 * 
 * 
 * @see Source
 * @see Target.First.Second.Done
 */
public fun  me.tbsten.cream.generated.Source.to_Target_First_Second_Done(
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
 * val target = source.to_Target_First_Second_Refreshing(extra = extra)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.to_Target_First_Second_Refreshing(extra = extra, property = value)
 * ```
 * 
 * 
 * @see Source
 * @see Target.First.Second.Refreshing
 */
public fun  me.tbsten.cream.generated.Source.to_Target_First_Second_Refreshing(
    name: String = this.name,
    extra: Int,
) : me.tbsten.cream.generated.Target.First.Second.Refreshing = me.tbsten.cream.generated.Target.First.Second.Refreshing(
    name = name,
    extra = extra,
)
````
