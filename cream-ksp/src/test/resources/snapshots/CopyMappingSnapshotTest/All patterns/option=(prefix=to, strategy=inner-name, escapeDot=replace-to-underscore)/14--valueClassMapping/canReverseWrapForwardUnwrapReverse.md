## Input:me.tbsten.cream.generated.Mapping

```kt
package me.tbsten.cream.generated

import kotlin.String
import kotlin.jvm.JvmInline
import me.tbsten.cream.CopyMapping

@CopyMapping(
  Source::class,
  Target::class,
  canReverse = true,
)
public object Mapping

public data class Source(
  public val id: String,
  public val name: String,
)

public data class Target(
  public val id: DomainId,
  public val name: String,
)

@JvmInline
public value class DomainId(
  public val `value`: String,
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
// file: CopyMapping__Mapping.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyMapping] annotation of [Mapping])
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
public fun  me.tbsten.cream.generated.Source.to_Target(
    id: DomainId = DomainId(this.id),
    name: String = this.name,
) : me.tbsten.cream.generated.Target = me.tbsten.cream.generated.Target(
    id = id,
    name = name,
)

/**
 * (Auto generate by @[CopyMapping] annotation of [Mapping])
 * 
 * Target -> Source copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = Target(...)
 * val target = source.to_Source()
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Target(...)
 * val target = source.to_Source(property = value)
 * ```
 * 
 * 
 * @see Target
 * @see Source
 */
public fun  me.tbsten.cream.generated.Target.to_Source(
    id: String = this.id.value,
    name: String = this.name,
) : me.tbsten.cream.generated.Source = me.tbsten.cream.generated.Source(
    id = id,
    name = name,
)
````
