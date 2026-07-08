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
 * val target = source.copyToStateLoaded()
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = State(...)
 * val target = source.copyToStateLoaded(property = value)
 * ```
 * 
 * 
 * @see State
 * @see State.Loaded
 */
internal fun  me.tbsten.cream.generated.State.copyToStateLoaded(
    id: String = this.id.value,
) : me.tbsten.cream.generated.State.Loaded = me.tbsten.cream.generated.State.Loaded(
    id = id,
)
````
