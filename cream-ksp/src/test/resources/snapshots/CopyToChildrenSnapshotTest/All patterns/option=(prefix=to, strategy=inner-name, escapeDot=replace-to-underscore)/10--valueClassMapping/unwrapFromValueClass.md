## Input:me.tbsten.cream.generated.State

```kt
package me.tbsten.cream.generated

import kotlin.String
import kotlin.jvm.JvmInline
import me.tbsten.cream.CopyToChildren

@CopyToChildren
public sealed interface State {
  public val id: DomainId

  public class Loaded(
    id: String,
  ) : State {
    override val id: DomainId = DomainId(id)
  }
}

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
// file: CopyToChildren__State.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyToChildren] annotation of [State])
 * 
 * State -> State.Loaded copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = State(...)
 * val target = source.to_Loaded()
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = State(...)
 * val target = source.to_Loaded(property = value)
 * ```
 * 
 * 
 * @see State
 * @see State.Loaded
 */
public fun  me.tbsten.cream.generated.State.to_Loaded(
    id: String = this.id.value,
) : me.tbsten.cream.generated.State.Loaded = me.tbsten.cream.generated.State.Loaded(
    id = id,
)
````
