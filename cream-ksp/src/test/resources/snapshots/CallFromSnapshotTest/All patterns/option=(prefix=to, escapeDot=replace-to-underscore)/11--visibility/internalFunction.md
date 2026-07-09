## Input:me.tbsten.cream.generated.audit

```kt
package me.tbsten.cream.generated

import kotlin.String
import me.tbsten.cream.CallFrom

@CallFrom(AuditArgs::class)
internal fun audit(name: String) {
}

public data class AuditArgs(
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
// file: CallFrom__audit.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CallFrom] annotation of [audit])
 * 
 * AuditArgs -> audit() bridge function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val auditArgs = AuditArgs(...)
 * audit(auditArgs)
 * ```
 * 
 * # Example: Override parameter values
 * 
 * ```kt
 * val auditArgs = AuditArgs(...)
 * audit(auditArgs, parameter = value)
 * ```
 * 
 * 
 * @see AuditArgs
 */
internal fun audit(
    auditArgs: AuditArgs,
    name: String = auditArgs.name,
): Unit = audit(
    name = name,
)
````
