# Refactor state management in a ViewModel by cream.kt

Covers state management in a ViewModel — or an equivalent StateHolder or Presenter — where the
state is modelled as a `sealed interface` / `sealed class`.

The problem this solves is **carry-over**: properties shared across states have to be hand-copied
from the previous state at every transition, so the one value that actually changed is buried in
noise, and adding a shared property later touches every transition in the codebase.

## Before / after

```kt
sealed interface ItemDetailUiState {
    val itemId: String
    val isBookmarked: Boolean

    data class Loading(override val itemId: String, override val isBookmarked: Boolean) : ItemDetailUiState
    data class Success(override val itemId: String, override val isBookmarked: Boolean, val item: Item) : ItemDetailUiState
    data class Error(override val itemId: String, override val isBookmarked: Boolean, val message: String) : ItemDetailUiState
}

class ItemDetailViewModel : ViewModel() {
    private val _state = MutableStateFlow<ItemDetailUiState>(/* ... */)
    val state = _state.asStateFlow()

    private suspend fun load() {
        runCatching { fetchItem() }.fold(
            onSuccess = { item ->
                _state.update { prev ->
                    ItemDetailUiState.Success(
                        itemId = prev.itemId,             // carry-over
                        isBookmarked = prev.isBookmarked, // carry-over
                        item = item,                      // the only real change
                    )
                }
            },
            onFailure = { e ->
                _state.update { prev ->
                    ItemDetailUiState.Error(
                        itemId = prev.itemId,             // carry-over
                        isBookmarked = prev.isBookmarked, // carry-over
                        message = e.message ?: "Unknown error",
                    )
                }
            },
        )
    }
}
```

One annotation on the sealed parent generates a copy function to every concrete leaf, with
`= this.xxx` defaults for the shared properties:

```kt
@CopyToChildren
sealed interface ItemDetailUiState { /* unchanged */ }
```

```kt
_state.update { prev -> prev.copyToItemDetailUiStateSuccess(item = item) }
_state.update { prev -> prev.copyToItemDetailUiStateError(message = e.message ?: "Unknown error") }
```

Adding a shared property later changes the sealed declaration only. No transition is touched.

## Ask first: which annotation style

The rewrite above is only one of three valid styles, and **which one a codebase wants is a design
preference you cannot infer from the code**. Settle it with the user before annotating anything —
it decides how much gets generated, and it changes the shape of the call sites.

| | Put it on | Receiver of the generated function | Transitions generated |
|---|---|---|---|
| **A. Blanket** | `@CopyToChildren` on the sealed parent | the sealed parent | every transitive leaf, automatically |
| **B. Restricted, parent receiver** | `@CopyTo(Success::class, Error::class)` on the sealed parent | the sealed parent | only the listed targets |
| **C. Per-edge** | `@CopyTo(Success::class, Error::class)` on each source leaf | that leaf | only the listed targets, and only from that source |

**A** is the least code and the least maintenance: one annotation, and leaves added later are
covered for free. The cost is that it generates a function for every source/target pair, including
transitions your state machine forbids. `Error -> Loading` becomes callable and shows up in
autocomplete even if it should never happen, and the legal edges live nowhere in the code.

**C** is the opposite trade. Each `@CopyTo` enumerates the states its source may become, so the
annotations *are* the transition table — an illegal transition simply has no function to call.
The cost is one annotation per source state, kept in sync by hand as states are added.

**B** sits in between: the parent receiver of A, the explicit target list of C. Useful when
transitions are driven from parent-typed state but only a few target states are legal.

### The receiver difference is not cosmetic

With **A** or **B** the receiver is the sealed parent, so the function can be called on a value
whose current subtype is unknown:

```kt
_state.update { prev -> prev.copyToItemDetailUiStateSuccess(item = item) } // prev: ItemDetailUiState
```

With **C** the receiver is the annotated leaf, so the source type must be narrowed first:

```kt
fun onItemLoaded(prev: ItemDetailUiState.Loading, item: Item): ItemDetailUiState =
    prev.copyToItemDetailUiStateSuccess(item = item)
```

So the existing code partly decides the answer. A codebase that transitions from parent-typed
state inside `_state.update { }` cannot adopt **C** without also narrowing the type at every call
site — which is a bigger change than the refactoring the user asked for. Say so when you ask.

### The ask

```
Q. 状態遷移のアノテーションはどのスタイルにしますか？

- [ ] A. @CopyToChildren を sealed 親に 1 つ
  - 記述量が最小。leaf を追加しても自動で拾われる。
  - 不正な遷移も生成され、呼べてしまう。
- [ ] B. @CopyTo を sealed 親に、遷移先を列挙
  - 親型のまま呼べて、遷移先は限定できる。
- [ ] C. @CopyTo を遷移元の leaf ごとに、遷移先を列挙
  - アノテーションが遷移表になる。不正な遷移は関数自体が存在しない。
  - 遷移元の型を先に絞る必要がある。状態を追加するたびに更新が要る。
```

Present the counts from step 1 so the choice is concrete — "this hierarchy has 5 leaves, so **A**
generates 25 transition functions where the code currently uses 4" is a far better question than
an abstract one.

## `@SealedCopy` is a separate axis

`@SealedCopy` is not an alternative to the three above; it solves the opposite problem — updating a
shared property while **keeping** the current subtype (refresh flags, banners, optimistic updates).
It generates the `when (this)` over all subtypes that you would otherwise hand-write for every
shared-property tweak, exposed as a parent-typed `copy()`.

Subtypes that cannot be copied — a `data object` — are handled by `nonCopyableStrategy`
(`ERROR` by default, or `RETURN_AS_IS` / `RETURN_NULL`); pick deliberately rather than leaving the
default if the hierarchy has such a subtype.

It composes with any of A / B / C. In an MVI reducer, the copy annotations handle transitions
*between* states and `@SealedCopy` handles updates *within* one.

Ask about it separately, and only if the code actually has same-subtype shared-property updates —
a `when (this)` whose every branch is `this.copy(someSharedProp = ...)` is the giveaway.

## Finding candidates

Look for constructor calls of a sealed subtype whose arguments are mostly reads of a previous
state. Useful signals:

```shell
# state holders
grep -rn --include='*.kt' --exclude-dir=build "MutableStateFlow\|mutableStateOf\|MutableLiveData" .
# transition sites
grep -rn --include='*.kt' --exclude-dir=build "_state.update\|_uiState.update\|updateAndGet\|_state.value =" .
# the carry-over itself: an argument that just reads the previous state
grep -rnE --include='*.kt' --exclude-dir=build "= *(prev|previous|current|it|state)\." .
# sealed state declarations
grep -rnE --include='*.kt' --exclude-dir=build "sealed (interface|class) [A-Za-z]*State" .
```

Two details these commands depend on: **quote the `--include` glob** (zsh expands `--include=*.kt`
and refuses to run the command at all), and **exclude `build/`** — once cream has generated
anything, the same patterns match the generated sources and the `build/kspCaches/` backups of them.

A file is a strong candidate when a single transition passes **two or more** arguments that are
plain reads of the previous state.

Skip a hierarchy where subtypes share nothing — there is no carry-over to generate, and adding an
annotation buys nothing.

## Workflow

### 1. Map the state hierarchy

For the chosen file, write down the sealed parent, its shared properties, every concrete leaf, and
every transition site. You need this list to check afterwards that nothing changed behaviour.

### 2. Ask which annotation style to use

Put the choice from [Ask first](#ask-first-which-annotation-style) to the user, with the leaf count
and transition count from step 1 filled in. Ask about `@SealedCopy` too, but only if step 1 turned
up same-subtype shared-property updates.

Do not skip this because one style looks obviously better to you. If the user has no preference,
recommend **A** for a hierarchy whose transitions are largely unconstrained and **C** for one that
models a real state machine with forbidden edges — then say which you picked and why.

### 3. Classify every transition

For each transition, note the source state, the target state, and flag the ones that are **not**
pure carry-over. A transition that deliberately resets a property (clearing an error, dropping a
snackbar message, resetting a scroll position) must keep that argument explicit. These are exactly
the places where a blind refactor introduces a bug.

For style **B** or **C**, this list is also what you enumerate in the `@CopyTo` targets — do not
add a target the code never actually transitions to.

### 4. Annotate

Apply the chosen style. For **A** and **B** the annotation goes on the sealed parent; for **C**,
one `@CopyTo` per source leaf. Add `@SealedCopy` to the parent if the user asked for it.

`@CopyToChildren` is transitive: it recurses through intermediate sealed types down to every
concrete leaf, so a nested state machine (`CheckoutUiState.Cart.Editing`,
`CheckoutUiState.Payment.Processing`, ...) is covered by a single annotation on the root, and
leaves added later are picked up automatically. `@CopyTo` has no such recursion — with **B** or
**C**, targets nested under an intermediate sealed type must be listed individually.

### 5. Generate, then read the generated names

Run the KSP task and open the generated file. Do not guess function names — they depend on
`cream.copyFunNamePrefix` and `cream.copyFunNamingStrategy`, so `copyToItemDetailUiStateSuccess` is
what the defaults produce, not a fixed name.

Use whatever name the project's configuration produces. The annotation's `funName` argument can
pin a different one per declaration, but that is configuration to maintain — raise it with the user
if the generated name is genuinely unusable, rather than setting it on your own. See
[Function name](https://github.com/TBSten/cream/blob/main/doc/customization/fun-name.md).

```shell
./gradlew :<module>:kspCommonMainKotlinMetadata   # KMP
./gradlew :<module>:compileKotlin                 # single-platform
find <module>/build/generated/ksp -name '*.kt'
```

### 6. Rewrite the call sites, one transition at a time

Replace the constructor call with the generated function, passing only the arguments that are not
carry-over — plus every argument you flagged in step 3. Compile after each transition, not after
all of them.

### 7. Verify

Run the verification method the user chose in step 1 of the parent skill. That answer governs —
including when it was "compile only" or "do not verify". Do not upgrade it on your own initiative.

If the chosen method looks too weak for what you changed — tests covering these transitions exist
and are the only real check that behaviour is unchanged, but the user picked compile-only — say so
in the report and let them decide. Do not quietly run them anyway.

## Watch out for

- **A transition that resets rather than carries over.** The generated default copies the previous
  value. If the old code passed a fresh value, keep passing it.
- **`data object` leaves.** `@CopyToChildren` generates a copy function to them too (it just
  returns the singleton). That is the default and it is harmless; only reach for
  `cream.notCopyToObject=true` if the user says the extra functions are in the way.
- **Style C narrows the receiver.** If a call site holds parent-typed state, a leaf-receiver
  function needs a smart cast or an `is` check first. If that shows up more than once or twice,
  the codebase probably wanted style A or B — go back and say so rather than sprinkling casts.
- **A property declared only on some leaves.** It has no parent-level counterpart, so it gets no
  default and stays a required argument. That is correct — do not add it to the parent just to
  silence the compiler.
- **Nullable vs non-null mismatches** between the parent property and a leaf's constructor
  parameter. No default is generated when the types differ; supply the conversion explicitly.
