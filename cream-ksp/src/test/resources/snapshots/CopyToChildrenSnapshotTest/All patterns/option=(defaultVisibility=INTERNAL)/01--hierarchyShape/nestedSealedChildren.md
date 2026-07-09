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
    arg("copyFunNamePrefix", "copyTo" /* default */)
    arg("copyFunNamingStrategy", "under-package" /* default */)
    arg("escapeDot", "lower-camel-case" /* default */)
    arg("notCopyToObject", "false" /* default */)
    arg("defaultVisibility", "INTERNAL")
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
 * val target = source.copyToSourceBranchDone(x = x)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToSourceBranchDone(x = x, property = value)
 * ```
 * 
 * 
 * @see Source
 * @see Source.Branch.Done
 */
internal fun  me.tbsten.cream.generated.Source.copyToSourceBranchDone(
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
 * val target = source.copyToSourceBranchPending(y = y)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToSourceBranchPending(y = y, property = value)
 * ```
 * 
 * 
 * @see Source
 * @see Source.Branch.Pending
 */
internal fun  me.tbsten.cream.generated.Source.copyToSourceBranchPending(
    y: Int,
) : me.tbsten.cream.generated.Source.Branch.Pending = me.tbsten.cream.generated.Source.Branch.Pending(
    y = y,
)
````
