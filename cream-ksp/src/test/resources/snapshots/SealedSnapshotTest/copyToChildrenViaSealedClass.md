## Generated

````kt
// file: CopyToChildren__S1.kt
package snap.sealed.viaSealedClass

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyToChildren] annotation of [S1])
 * 
 * S1 -> S1.S2.S3 copy function.
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = S1(...)
 * val target = source.copyToS1S2S3()
 * ```
 * 
 * 
 * @see S1
 * @see S1.S2.S3
 */
public fun snap.sealed.viaSealedClass.S1.copyToS1S2S3() = snap.sealed.viaSealedClass.S1.S2.S3
````

## Input

```kt
package snap.sealed.viaSealedClass

import me.tbsten.cream.CopyToChildren

@CopyToChildren
sealed interface S1 {
    val id: String

    sealed class S2 : S1 {
        data object S3 : S2() {
            override val id: String get() = "s3"
        }
    }
}
```
