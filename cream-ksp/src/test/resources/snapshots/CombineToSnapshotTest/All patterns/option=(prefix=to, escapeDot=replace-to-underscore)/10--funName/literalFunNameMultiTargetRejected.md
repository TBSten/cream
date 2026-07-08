## Input:me.tbsten.cream.generated.SourceA

```kt
package me.tbsten.cream.generated

import kotlin.Int
import kotlin.String
import me.tbsten.cream.CombineTo

@CombineTo(
  TargetA::class,
  TargetB::class,
  funName = "toTarget",
)
public data class SourceA(
  public val name: String,
)

@CombineTo(
  TargetA::class,
  TargetB::class,
  funName = "toTarget",
)
public data class SourceB(
  public val extra: Int,
)

public data class TargetA(
  public val name: String,
  public val extra: Int,
)

public data class TargetB(
  public val name: String,
  public val extra: Int,
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
e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/me.tbsten.cream.generated.SourceA.kt:12: Invalid cream usage: @CombineTo on me.tbsten.cream.generated.SourceA sets a fixed funName "toTarget",
but it generates more than one function (multiple targets or sources, a sealed
target, or a reversible mapping). Those functions would all share that name and collide.

Solution: 
  Include a naming token so each generated function gets a distinct name, e.g.
    funName = "to" + CopyTargetSimpleName
  or split the declaration into separate annotations.

e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/me.tbsten.cream.generated.SourceA.kt:21: Invalid cream usage: @CombineTo on me.tbsten.cream.generated.SourceB sets a fixed funName "toTarget",
but it generates more than one function (multiple targets or sources, a sealed
target, or a reversible mapping). Those functions would all share that name and collide.

Solution: 
  Include a naming token so each generated function gets a distinct name, e.g.
    funName = "to" + CopyTargetSimpleName
  or split the declaration into separate annotations.
```

## Output:Generated sources

```kt

```
