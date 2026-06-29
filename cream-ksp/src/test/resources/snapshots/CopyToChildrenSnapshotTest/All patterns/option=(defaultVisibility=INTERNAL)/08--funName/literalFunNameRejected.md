## Input:me.tbsten.cream.generated.Source

```kt
package me.tbsten.cream.generated

import kotlin.String
import me.tbsten.cream.CopyToChildren

@CopyToChildren(funName = "toState")
public sealed interface Source {
  public val id: String

  public data class Loading(
    override val id: String,
  ) : Source

  public data class Done(
    override val id: String,
    public val `data`: String,
  ) : Source
}
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
e: [ksp] <TMPDIR>/Kotlin-Compilation<N>/sources/me.tbsten.cream.generated.Source.kt:7: Invalid cream usage: @CopyToChildren on me.tbsten.cream.generated.Source sets a fixed funName "toState",
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
