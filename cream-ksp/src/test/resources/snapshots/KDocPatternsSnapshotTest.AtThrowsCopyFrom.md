## Generated

````kt
// file: CopyFrom__User.kt
package snap.kdoc.atthrowscopyfrom

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyFrom] annotation of [User])
 * 
 * RemoteUser -> User copy function.
 * 
 * @throws IllegalArgumentException email が空文字の場合。
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = RemoteUser(...)
 * val target = source.copyToUser()
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = RemoteUser(...)
 * val target = source.copyToUser(property = value)
 * ```
 * 
 * 
 * @see RemoteUser
 * @see User
 */
public fun  snap.kdoc.atthrowscopyfrom.RemoteUser.copyToUser(
    email: String = this.email,
) : snap.kdoc.atthrowscopyfrom.User = snap.kdoc.atthrowscopyfrom.User(
    email = email,
)
````

## Input

```kt
package snap.kdoc.atthrowscopyfrom

import me.tbsten.cream.CopyFrom
import me.tbsten.cream.KDoc

data class RemoteUser(val email: String)

@CopyFrom(
    RemoteUser::class,
    kdoc = KDoc(description = "@throws IllegalArgumentException email が空文字の場合。"),
)
data class User(val email: String)
```
