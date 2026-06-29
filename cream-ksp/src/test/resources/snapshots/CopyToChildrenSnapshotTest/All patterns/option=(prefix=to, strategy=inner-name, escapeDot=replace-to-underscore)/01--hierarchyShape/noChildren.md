## Input:me.tbsten.cream.generated.Source

```kt
package me.tbsten.cream.generated

import me.tbsten.cream.CopyToChildren

@CopyToChildren
public sealed interface Source
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

```kt

```
