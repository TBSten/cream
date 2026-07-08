## Input:me.tbsten.cream.generated.Source

```kt
package me.tbsten.cream.generated

import kotlin.Int
import kotlin.String
import me.tbsten.cream.CopyToChildren

@CopyToChildren
public sealed interface Source {
  public sealed interface Branch : Source {
    public data class Done(
      public val x: String,
    ) : Branch

    public data class Pending(
      public val y: Int,
    ) : Branch
  }
}
```

## KSP options

```kt
ksp {
    arg("copyFunNamePrefix", "to")
    arg("copyFunNamingStrategy", "inner-name")
    arg("escapeDot", "replace-to-underscore")
    arg("notCopyToObject", "false" /* default */)
    arg("defaultVisibility", "INHERIT" /* default */)
    arg("autoValueClassMapping", "true" /* default */)
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
// file: CopyToChildren__Source.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyToChildren] annotation of [Source])
 * 
 * Source -> Source.Branch.Done copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.to_Branch_Done(x = x)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.to_Branch_Done(x = x, property = value)
 * ```
 * 
 * 
 * @see Source
 * @see Source.Branch.Done
 */
public fun  me.tbsten.cream.generated.Source.to_Branch_Done(
    x: String,
) : me.tbsten.cream.generated.Source.Branch.Done = me.tbsten.cream.generated.Source.Branch.Done(
    x = x,
)

/**
 * (Auto generate by @[CopyToChildren] annotation of [Source])
 * 
 * Source -> Source.Branch.Pending copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.to_Branch_Pending(y = y)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.to_Branch_Pending(y = y, property = value)
 * ```
 * 
 * 
 * @see Source
 * @see Source.Branch.Pending
 */
public fun  me.tbsten.cream.generated.Source.to_Branch_Pending(
    y: Int,
) : me.tbsten.cream.generated.Source.Branch.Pending = me.tbsten.cream.generated.Source.Branch.Pending(
    y = y,
)
````
