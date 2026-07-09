## Input:me.tbsten.cream.generated.Holder

```kt
package me.tbsten.cream.generated

import kotlin.String
import me.tbsten.cream.CallFrom

internal class Holder {
  @CallFrom(MemberArgs::class)
  public fun member(`value`: String) {
  }
}

public data class MemberArgs(
  public val `value`: String,
)
```

## KSP options

```kt
ksp {
    arg("copyFunNamePrefix", "to")
    arg("copyFunNamingStrategy", "inner-name")
    arg("escapeDot", "replace-to-underscore")
    arg("notCopyToObject", "false" /* default */)
    arg("defaultVisibility", "INHERIT" /* default */)
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
// file: CallFrom__Holder_member.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CallFrom] annotation of [Holder.member])
 * 
 * MemberArgs -> Holder.member() bridge function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val memberArgs = MemberArgs(...)
 * holder.member(memberArgs)
 * ```
 * 
 * # Example: Override parameter values
 * 
 * ```kt
 * val memberArgs = MemberArgs(...)
 * holder.member(memberArgs, parameter = value)
 * ```
 * 
 * 
 * @see MemberArgs
 */
internal fun me.tbsten.cream.generated.Holder.member(
    memberArgs: MemberArgs,
    value: String = memberArgs.value,
): Unit = member(
    value = value,
)
````
