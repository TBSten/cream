## Generated

````kt
// file: CopyMapping__StripeUserMapping.kt
package snap.kdoc.copymappinglibraryboundary

import me.tbsten.cream.*

/**
 * (Auto generate by @[CopyMapping] annotation of [StripeUserMapping])
 * 
 * StripeCustomer -> AppUser copy function.
 * 
 * 外部 SDK の StripeCustomer を自前ドメインへ隔離変換する腐敗防止層。
 * 
 * # Example: Basic
 * 
 * ```kt
 * val source = StripeCustomer(...)
 * val target = source.copyToAppUser()
 * ```
 * 
 * # Example: Override property values
 * 
 * ```kt
 * val source = StripeCustomer(...)
 * val target = source.copyToAppUser(property = value)
 * ```
 * 
 * 
 * @see StripeCustomer
 * @see AppUser
 */
public fun  snap.kdoc.copymappinglibraryboundary.StripeCustomer.copyToAppUser(
    id: String = this.id,
    email: String = this.email,
) : snap.kdoc.copymappinglibraryboundary.AppUser = snap.kdoc.copymappinglibraryboundary.AppUser(
    id = id,
    email = email,
)
````

## Input

```kt
package snap.kdoc.copymappinglibraryboundary

import me.tbsten.cream.CopyMapping
import me.tbsten.cream.KDoc

data class StripeCustomer(val id: String, val email: String)

data class AppUser(val id: String, val email: String)

@CopyMapping(
    StripeCustomer::class,
    AppUser::class,
    kdoc = KDoc(description = "外部 SDK の StripeCustomer を自前ドメインへ隔離変換する腐敗防止層。"),
)
private object StripeUserMapping
```
