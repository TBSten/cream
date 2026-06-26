## Input:Input

```kt
package me.tbsten.cream.generated

import kotlin.Int
import kotlin.String
import me.tbsten.cream.CopyFrom

@CopyFrom(Source::class)
public class Target private constructor(
  public val name: String,
  public val extra: Int,
)

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
}
```

## Output:ExitCode

```kt
COMPILATION_ERROR
```

## Output:Console

```kt
e: Error occurred in KSP, check log for detail
e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/Input.kt:8: Invalid cream usage: Unsupported target me.tbsten.cream.generated.Target: its primary constructor is private and cannot be called from generated code.

Solution: 
  Make the primary constructor of me.tbsten.cream.generated.Target public or internal.
```

## Output:Generated sources

```kt

```
