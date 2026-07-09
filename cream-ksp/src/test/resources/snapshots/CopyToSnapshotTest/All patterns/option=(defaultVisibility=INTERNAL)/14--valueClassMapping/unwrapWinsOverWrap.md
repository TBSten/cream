## Input:me.tbsten.cream.generated.Source

```kt
package me.tbsten.cream.generated

import kotlin.Any
import kotlin.String
import kotlin.jvm.JvmInline
import me.tbsten.cream.CopyTo

@CopyTo(Target::class)
public data class Source(
  public val item: Holder,
  public val name: String,
)

public data class Target(
  public val item: Wrapper,
  public val name: String,
)

@JvmInline
public value class Wrapper(
  public val raw: Any,
)

@JvmInline
public value class Holder(
  public val wrapper: Wrapper,
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
 * val target = source.copyToTarget()
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToTarget(property = value)
 * ```
 * 
 * 
 * @see Source
 * @see Target
 */
internal fun  me.tbsten.cream.generated.Source.copyToTarget(
    item: Wrapper = this.item.wrapper,
    name: String = this.name,
) : me.tbsten.cream.generated.Target = me.tbsten.cream.generated.Target(
    item = item,
    name = name,
)
````
