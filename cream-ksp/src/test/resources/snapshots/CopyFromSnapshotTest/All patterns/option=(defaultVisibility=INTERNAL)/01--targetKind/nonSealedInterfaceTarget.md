## Input:me.tbsten.cream.generated.Target

```kt
package me.tbsten.cream.generated

import kotlin.String
import me.tbsten.cream.CopyFrom

@CopyFrom(Source::class)
public interface Target {
  public val name: String
}

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
    arg("defaultVisibility", "INTERNAL")
}
```

## Output:ExitCode

```kt
COMPILATION_ERROR
```

## Output:Console

```kt
e: Error occurred in KSP, check log for detail
e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/me.tbsten.cream.generated.Target.kt:7: Invalid cream usage: Unsupported target interface (me.tbsten.cream.generated.Target). It must be a sealed interface.

Solution: 
  Please make me.tbsten.cream.generated.Target a sealed interface.
```

## Output:Generated sources

```kt

```
