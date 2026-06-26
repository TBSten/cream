## Input:Input

```kt
package me.tbsten.cream.generated

import kotlin.Int
import kotlin.String
import me.tbsten.cream.CombineFrom

@CombineFrom
public data class Target(
  public val name: String,
  public val extra: Int,
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
e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/Input.kt:8: Invalid cream usage: @CombineFrom requires at least one source class.

Solution: 
  Specify at least one source class in @CombineFrom.sources of me.tbsten.cream.generated.Target.
```

## Output:Generated sources

```kt

```
