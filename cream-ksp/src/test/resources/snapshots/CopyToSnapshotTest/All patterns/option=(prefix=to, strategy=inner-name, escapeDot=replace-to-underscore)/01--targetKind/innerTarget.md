## Input:me.tbsten.cream.generated.Source

```kt
package me.tbsten.cream.generated

import kotlin.String
import me.tbsten.cream.CopyTo

@CopyTo(Outer.Target::class)
public data class Source(
  public val name: String,
)

public class Outer {
  public inner class Target(
    public val name: String,
  )
}
```

## KSP options

```kt
ksp {
    arg("copyFunNamePrefix", "to")
    arg("copyFunNamingStrategy", "inner-name")
    arg("escapeDot", "replace-to-underscore")
    arg("notCopyToObject", "false" /* default */)
    arg("defaultVisibility", "INHERIT" /* default */)
    arg("autoValueClassMapping", "true" /* default */)
}
```

## Output:ExitCode

```kt
COMPILATION_ERROR
```

## Output:Console

```kt
e: Error occurred in KSP, check log for detail
e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/me.tbsten.cream.generated.Source.kt:12: Invalid cream usage: Unsupported target inner class (me.tbsten.cream.generated.Outer.Target). An inner class requires an enclosing instance and cannot be a target.

Solution: 
  Make me.tbsten.cream.generated.Outer.Target a top-level or nested (non-inner) class.
```

## Output:Generated sources

```kt

```
