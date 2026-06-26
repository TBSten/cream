## Input:Input

```kt
package me.tbsten.cream.generated

import kotlin.String
import me.tbsten.cream.CopyFrom

public class Outer {
  @CopyFrom(Source::class)
  public inner class Target(
    public val name: String,
  )
}

public data class Source(
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
}
```

## Output:ExitCode

```kt
COMPILATION_ERROR
```

## Output:Console

```kt
e: Error occurred in KSP, check log for detail
e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/Input.kt:8: Invalid cream usage: Unsupported target inner class (me.tbsten.cream.generated.Outer.Target). An inner class requires an enclosing instance and cannot be a target.

Solution: 
  Make me.tbsten.cream.generated.Outer.Target a top-level or nested (non-inner) class.
```

## Output:Generated sources

```kt

```
