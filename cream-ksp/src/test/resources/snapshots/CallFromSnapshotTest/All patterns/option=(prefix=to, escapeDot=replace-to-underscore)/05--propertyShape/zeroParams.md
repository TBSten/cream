## Input:me.tbsten.cream.generated.ping

```kt
package me.tbsten.cream.generated

import me.tbsten.cream.CallFrom

@CallFrom(Trigger::class)
public fun ping() {
}

public class Trigger
```

## KSP options

```kt
ksp {
    arg("copyFunNamePrefix", "to")
    arg("copyFunNamingStrategy", "under-package" /* default */)
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
// file: CallFrom__ping.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CallFrom] annotation of [ping])
 * 
 * Trigger -> ping() bridge function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val trigger = Trigger(...)
 * ping(trigger)
 * ```
 * 
 * # Example: Override parameter values
 * 
 * ```kt
 * val trigger = Trigger(...)
 * ping(trigger, parameter = value)
 * ```
 * 
 * 
 * @see Trigger
 */
public fun ping(
    trigger: Trigger,
): Unit = ping(
)
````
