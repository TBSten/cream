## Input:Input

```kt
package me.tbsten.cream.generated

import kotlin.String
import me.tbsten.cream.CombineMapping

@CombineMapping(
  sources = [SourceA::class, SourceB::class],
  target = Target::class,
)
public object Mapping

public data class SourceA(
  public val name: String,
)

public sealed interface SourceB {
  public val extra: String
}

public data class Target(
  public val name: String,
  public val extra: String,
)
```

## KSP options

```kt
ksp {
    arg("copyFunNamePrefix", "to")
    arg("copyFunNamingStrategy", "inner-name")
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
e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/Input.kt:10: Invalid cream usage: me.tbsten.cream.generated.SourceB (Specified in @CombineMapping.sources) must be a class.

Solution: 
  Specify a class in @CombineMapping.sources
```

## Output:Generated sources

```kt

```
