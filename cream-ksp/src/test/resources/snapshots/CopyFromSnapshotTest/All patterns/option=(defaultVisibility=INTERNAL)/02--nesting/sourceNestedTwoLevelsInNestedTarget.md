## Input:me.tbsten.cream.generated.Parent

```kt
package me.tbsten.cream.generated

import kotlin.Int
import kotlin.String
import me.tbsten.cream.CopyFrom

public class Parent {
  @CopyFrom(Target.Middle.Source::class)
  public data class Target(
    public val name: String,
    public val extra: Int,
  ) {
    public class Middle {
      public data class Source(
        public val name: String,
      )
    }
  }
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
OK
```

## Output:Console

```kt

```

## Output:Generated sources

````kt
// file: CopyFrom__Parent.Target.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyFrom] annotation of [Parent.Target])
 * 
 * Parent.Target.Middle.Source -> Parent.Target copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToParentTarget(extra = extra)
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.copyToParentTarget(extra = extra, property = value)
 * ```
 * 
 * 
 * @see Parent.Target.Middle.Source
 * @see Parent.Target
 */
internal fun  me.tbsten.cream.generated.Parent.Target.Middle.Source.copyToParentTarget(
    name: String = this.name,
    extra: Int,
) : me.tbsten.cream.generated.Parent.Target = me.tbsten.cream.generated.Parent.Target(
    name = name,
    extra = extra,
)
````
