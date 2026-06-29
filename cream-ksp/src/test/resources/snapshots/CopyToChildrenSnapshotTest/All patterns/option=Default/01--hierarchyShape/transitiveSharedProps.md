## Input:me.tbsten.cream.generated.Source

```kt
package me.tbsten.cream.generated

import kotlin.String
import me.tbsten.cream.CopyToChildren

@CopyToChildren
public sealed interface Source {
  public val id: String

  public sealed interface Branch : Source {
    public data class Done(
      override val id: String,
      public val note: String,
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
 * val target = source.copyToSourceBranchDone(note = note)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToSourceBranchDone(note = note, property = value)
 * ```
 * 
 * 
 * @see Source
 * @see Source.Branch.Done
 */
public fun  me.tbsten.cream.generated.Source.copyToSourceBranchDone(
    id: String = this.id,
    note: String,
) : me.tbsten.cream.generated.Source.Branch.Done = me.tbsten.cream.generated.Source.Branch.Done(
    id = id,
    note = note,
)
````
