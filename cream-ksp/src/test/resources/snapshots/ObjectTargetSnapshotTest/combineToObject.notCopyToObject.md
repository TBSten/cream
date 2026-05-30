## Generated

```kt
// file: CombineTo__Source__Singleton.kt
package snap.objtarget

import me.tbsten.cream.*
```

## Input

```kt
package snap.objtarget

import me.tbsten.cream.CombineTo

@CombineTo(Singleton::class)
data class Source(val prop: String)

data object Singleton
```
