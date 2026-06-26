## Input:Input

```kt
package me.tbsten.cream.generated

import me.tbsten.cream.CopyToChildren

@CopyToChildren
public sealed interface Source {
  public data class Child() : Source
}
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
e: file://<TMPDIR>/Kotlin-Compilation<N>/sources/Input.kt:7:26 Data class must have at least one primary constructor parameter.
```

## Output:Generated sources

````kt
// file: CopyToChildren__Source.kt
package me.tbsten.cream.generated

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyToChildren] annotation of [Source])
 * 
 * Source -> Source.Child copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.to_Source_Child()
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = Source(...)
 * val target = source.to_Source_Child(property = value)
 * ```
 * 
 * 
 * @see Source
 * @see Source.Child
 */
public fun  me.tbsten.cream.generated.Source.to_Source_Child(
) : me.tbsten.cream.generated.Source.Child = me.tbsten.cream.generated.Source.Child(
)
````
