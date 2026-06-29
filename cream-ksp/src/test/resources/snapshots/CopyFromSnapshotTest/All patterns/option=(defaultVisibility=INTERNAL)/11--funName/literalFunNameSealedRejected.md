## Input:me.tbsten.cream.generated.Target

```kt
package me.tbsten.cream.generated

import kotlin.String
import me.tbsten.cream.CopyFrom

@CopyFrom(
  Source::class,
  funName = "toState",
)
public sealed interface Target {
  public val name: String

  public data class First(
    override val name: String,
  ) : Target

  public data class Second(
    override val name: String,
  ) : Target
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
e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/me.tbsten.cream.generated.Target.kt:10: Invalid cream usage: @CopyFrom on me.tbsten.cream.generated.Target sets a fixed funName "toState",
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
