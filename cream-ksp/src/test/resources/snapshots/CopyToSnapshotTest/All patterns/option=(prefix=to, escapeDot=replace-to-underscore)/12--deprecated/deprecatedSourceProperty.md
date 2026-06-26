## Input:Input

```kt
package me.tbsten.cream.generated

import kotlin.Deprecated
import kotlin.Int
import kotlin.String
import me.tbsten.cream.CopyTo

@CopyTo(Target::class)
public data class Source(
  public val name: String,
  @Deprecated("gone")
  public val legacy: Int,
)

public data class Target(
  public val name: String,
  public val legacy: Int,
)
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
w: file://<TMPDIR>/Kotlin-Compilation<N>/ksp/sources/kotlin/me/tbsten/cream/generated/CopyTo__Source.kt:31:24 'val legacy: Int' is deprecated. gone.
```

## Output:Generated sources

````kt
// file: CopyTo__Source.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyTo] annotation of [Source])
 * 
 * Source -> Target copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.to_Target()
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.to_Target(property = value)
 * ```
 * 
 * 
 * @see Source
 * @see Target
 */
@Deprecated("gone")
public fun  me.tbsten.cream.generated.Source.to_Target(
    name: String = this.name,
    legacy: Int = this.legacy,
) : me.tbsten.cream.generated.Target = me.tbsten.cream.generated.Target(
    name = name,
    legacy = legacy,
)
````
