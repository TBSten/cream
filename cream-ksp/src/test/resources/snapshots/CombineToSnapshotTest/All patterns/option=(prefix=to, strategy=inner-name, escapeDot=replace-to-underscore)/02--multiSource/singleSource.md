## Input:me.tbsten.cream.generated.SourceA

```kt
package me.tbsten.cream.generated

import kotlin.Int
import kotlin.String
import me.tbsten.cream.CombineTo

@CombineTo(Target::class)
public data class SourceA(
  public val name: String,
  public val extra: Int,
)

public data class Target(
  public val name: String,
  public val extra: Int,
)
```

## KSP options

```kt
ksp {
    arg("copyFunNamePrefix", "to")
    arg("copyFunNamingStrategy", "inner-name")
    arg("escapeDot", "replace-to-underscore")
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
// file: CombineTo__SourceA__Target.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CombineTo] annotation of [SourceA])
 * 
 * [SourceA] -> [Target] copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val sourceA = SourceA(...)
 * val target = sourceA.to_Target()
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val sourceA = SourceA(...)
 * val target = sourceA.to_Target(property = value)
 * ```
 * 
 * 
 * @see SourceA
 * @see Target
 */
public fun  me.tbsten.cream.generated.SourceA.to_Target(
    name: String = this.name,
    extra: Int = this.extra,
) : me.tbsten.cream.generated.Target = me.tbsten.cream.generated.Target(
    name = name,
    extra = extra,
)
````
