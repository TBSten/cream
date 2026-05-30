## Compiler output

```text
Invalid cream usage: Cannot generate updated() for sealed type 'MyState' because the following subclass(es) have no compatible 'copy(...)' function: MyState.Frozen

Solution: 
  Choose one of the following strategies on @SealedCopy:
    • @SealedCopy(nonCopyableStrategy = RETURN_AS_IS)
      → emits 'is X -> this' for non-copyable branches
    • @SealedCopy(nonCopyableStrategy = RETURN_NULL)
      → widens the return type to 'MyState?' and emits 'is X -> null'
  
  For non-data class subtypes you can also:
    • Make the subtype a 'data class'
    • Add a 'copy(...)' member function that accepts the abstract properties
    • Or annotate that copy-shaped function with @SealedCopy.Map
  

me.tbsten.cream.ksp.InvalidCreamUsageException: Invalid cream usage: Cannot generate updated() for sealed type 'MyState' because the following subclass(es) have no compatible 'copy(...)' function: MyState.Frozen

Solution: 
  Choose one of the following strategies on @SealedCopy:
    • @SealedCopy(nonCopyableStrategy = RETURN_AS_IS)
      → emits 'is X -> this' for non-copyable branches
    • @SealedCopy(nonCopyableStrategy = RETURN_NULL)
      → widens the return type to 'MyState?' and emits 'is X -> null'
  
  For non-data class subtypes you can also:
    • Make the subtype a 'data class'
    • Add a 'copy(...)' member function that accepts the abstract properties
    • Or annotate that copy-shaped function with @SealedCopy.Map
  

	<stack trace omitted>
```

## Input

```kt
package diag.sealedCopy

import me.tbsten.cream.SealedCopy

@SealedCopy(funName = "updated")
sealed interface MyState {
    val name: String

    data class Loading(override val name: String) : MyState
    class Frozen(override val name: String) : MyState
}
```
