## Input:Input

```kt
package me.tbsten.cream.generated

import kotlin.Int
import kotlin.String
import me.tbsten.cream.CombineFrom

@CombineFrom(
  SourceA::class,
  funName = "toFoo",
)
@CombineFrom(
  SourceB::class,
  funName = "toBar",
)
public data class Target(
  public val propertyA: String,
  public val propertyB: Int,
)

public data class SourceA(
  public val propertyA: String,
)

public data class SourceB(
  public val propertyB: Int,
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
e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/Input.kt:15: Invalid cream usage: @CombineFrom on me.tbsten.cream.generated.Target is repeated with conflicting funName values:
"toFoo", "toBar"
Stacked @CombineFrom annotations are merged into a single generated function, so funName must be unambiguous.

Solution: 
  Set the same funName on every @CombineFrom of me.tbsten.cream.generated.Target, or set it on only one.
```

## Output:Generated sources

```kt

```
