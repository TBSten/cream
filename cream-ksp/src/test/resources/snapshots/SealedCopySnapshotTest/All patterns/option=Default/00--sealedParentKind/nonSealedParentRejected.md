## Input:me.tbsten.cream.generated.Source

```kt
package me.tbsten.cream.generated

import kotlin.String
import me.tbsten.cream.SealedCopy

@SealedCopy
public data class Source(
  public val name: String,
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
}
```

## Output:ExitCode

```kt
COMPILATION_ERROR
```

## Output:Console

```kt
e: Error occurred in KSP, check log for detail
e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/me.tbsten.cream.generated.Source.kt:7: Invalid cream usage: @SealedCopy must be applied to a sealed class/interface, but me.tbsten.cream.generated.Source is not sealed.

Solution: 
  Make me.tbsten.cream.generated.Source a `sealed class` or `sealed interface`.
```

## Output:Generated sources

```kt

```
