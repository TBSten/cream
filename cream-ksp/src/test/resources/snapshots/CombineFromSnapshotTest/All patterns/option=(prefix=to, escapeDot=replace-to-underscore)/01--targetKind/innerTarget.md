## Input:Input

```kt
package me.tbsten.cream.generated

import kotlin.String
import me.tbsten.cream.CombineFrom

public class Outer {
  @CombineFrom(Source::class)
  public inner class Target(
    public val name: String,
  )
}

public data class Source(
  public val name: String,
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
COMPILATION_ERROR
```

## Output:Console

```kt
e: file://<TMPDIR>/Kotlin-Compilation<N>/ksp/sources/kotlin/me/tbsten/cream/generated/CombineFrom__Source__Outer.Target.kt:30:78 Constructor of the inner class 'inner class Target : Any' can only be called with a receiver of the containing class.
```

## Output:Generated sources

````kt
// file: CombineFrom__Source__Outer.Target.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CombineFrom] annotation of [Outer.Target])
 * 
 * [Source] -> [Outer.Target] copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.to_Outer_Target()
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.to_Outer_Target(property = value)
 * ```
 * 
 * 
 * @see Source
 * @see Outer.Target
 */
public fun  me.tbsten.cream.generated.Source.to_Outer_Target(
    name: String = this.name,
) : me.tbsten.cream.generated.Outer.Target = me.tbsten.cream.generated.Outer.Target(
    name = name,
)
````
