## Input:me.tbsten.cream.generated.Source

```kt
package me.tbsten.cream.generated

import me.tbsten.cream.CopyToChildren

@CopyToChildren(notCopyToObject = true)
public sealed interface Source {
  public object First : Source

  public object Second : Source
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

```kt

```
