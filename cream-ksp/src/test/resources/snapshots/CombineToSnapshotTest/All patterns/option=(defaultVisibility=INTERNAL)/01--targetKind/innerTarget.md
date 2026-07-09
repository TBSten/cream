## Input:me.tbsten.cream.generated.Source

```kt
package me.tbsten.cream.generated

import kotlin.String
import me.tbsten.cream.CombineTo

@CombineTo(Outer.Target::class)
public data class Source(
  public val name: String,
)

public class Outer {
  public inner class Target(
    public val name: String,
  )
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
COMPILATION_ERROR
```

## Output:Console

```kt
e: file://<TMPDIR>/Kotlin-Compilation<N>/ksp/sources/kotlin/me/tbsten/cream/generated/CombineTo__Source__Outer.Target.kt:30:78 Constructor of the inner class 'inner class Target : Any' can only be called with a receiver of the containing class.
```

## Output:Generated sources

````kt
// file: CombineTo__Source__Outer.Target.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CombineTo] annotation of [Source])
 * 
 * [Source] -> [Outer.Target] copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToOuterTarget()
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToOuterTarget(property = value)
 * ```
 * 
 * 
 * @see Source
 * @see Outer.Target
 */
internal fun  me.tbsten.cream.generated.Source.copyToOuterTarget(
    name: String = this.name,
) : me.tbsten.cream.generated.Outer.Target = me.tbsten.cream.generated.Outer.Target(
    name = name,
)
````
