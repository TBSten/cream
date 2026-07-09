## Input:me.tbsten.cream.generated.State

```kt
package me.tbsten.cream.generated

import kotlin.String
import kotlin.jvm.JvmInline
import me.tbsten.cream.CopyToChildren

@CopyToChildren
public sealed interface State {
  @CopyToChildren.Exclude
  public val id: String

  public class Loaded(
    id: DomainId,
  ) : State {
    override val id: String = id.value
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
    arg("copyFunNamePrefix", "copyTo" /* default */)
    arg("copyFunNamingStrategy", "under-package" /* default */)
    arg("escapeDot", "lower-camel-case" /* default */)
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
 * val target = source.copyToStateLoaded(id = id)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = State(...)
 * val target = source.copyToStateLoaded(id = id, property = value)
 * ```
 * 
 * 
 * @see State
 * @see State.Loaded
 */
public fun  me.tbsten.cream.generated.State.copyToStateLoaded(
    id: DomainId,
) : me.tbsten.cream.generated.State.Loaded = me.tbsten.cream.generated.State.Loaded(
    id = id,
)
````
