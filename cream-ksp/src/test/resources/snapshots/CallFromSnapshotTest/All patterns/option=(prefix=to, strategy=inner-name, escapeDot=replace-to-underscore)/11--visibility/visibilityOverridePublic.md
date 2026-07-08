## Input:me.tbsten.cream.generated.audit

```kt
package me.tbsten.cream.generated

import kotlin.String
import me.tbsten.cream.CallFrom
import me.tbsten.cream.CopyVisibility

@CallFrom(
  AuditArgs::class,
  visibility = CopyVisibility.PUBLIC,
)
public fun audit(name: String) {
}

public data class AuditArgs(
  public val name: String,
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
public fun audit(
    auditArgs: AuditArgs,
    name: String = auditArgs.name,
): Unit = audit(
    name = name,
)
````
