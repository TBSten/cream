## Input:me.tbsten.cream.generated.tag

```kt
package me.tbsten.cream.generated

import kotlin.String
import me.tbsten.cream.CallFrom

@CallFrom(TagArgs::class)
public fun <T> tag(item: T, label: String) {
}

public data class TagArgs(
  public val label: String,
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
// file: CallFrom__tag.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CallFrom] annotation of [tag])
 * 
 * TagArgs -> tag() bridge function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val tagArgs = TagArgs(...)
 * tag(tagArgs, item = ...)
 * ```
 * 
 * # Example: Override parameter values
 * 
 * ```kt
 * val tagArgs = TagArgs(...)
 * tag(tagArgs, item = ..., parameter = value)
 * ```
 * 
 * 
 * @see TagArgs
 */
internal fun <T : Any?> tag(
    tagArgs: TagArgs,
    item: T,
    label: String = tagArgs.label,
): Unit = tag(
    item = item,
    label = label,
)
````
