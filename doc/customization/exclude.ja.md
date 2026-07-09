[← README](../../README.ja.md) | [English](./exclude.md)

# 自動コピーのデフォルト値を外す (.Exclude)

プロパティに付与することで、生成コピー関数から**自動コピーのデフォルト値を除去**し、その引数を必須にします。
引数自体は関数シグネチャに残りますが、`= this.<プロパティ>` というデフォルトが消え、呼び出し側が明示的に値を指定する必要があります。
`@CopyMapping` / `@CombineMapping`（マッピング対象クラスにアノテーションを付けられない場合）では、
同じ効果を [`excludes` アノテーション引数](#copymapping--combinemapping--excludes) で指定できます。

## 付与する場所

| アノテーション | 付与する場所 |
|---|---|
| `@CopyTo.Exclude` | ソースクラスのコンストラクタパラメータ |
| `@CopyFrom.Exclude` | ターゲットクラスのコンストラクタパラメータ |
| `@CombineTo.Exclude` | ソースクラスのプロパティ |
| `@CombineFrom.Exclude` | ターゲットクラスのコンストラクタパラメータ |
| `@SealedCopy.Exclude` | sealed 親の abstract プロパティ |
| `@CopyToChildren.Exclude` | sealed 親のプロパティ（全ての per-child コピー関数に適用） |
| `@CallFrom.Exclude` | [`@CallFrom`](../call-from.ja.md) を付与した関数のパラメータ |
| `@CopyMapping(excludes = [...])` | アノテーションの `excludes` 引数（ターゲット側のプロパティ名） |
| `@CombineMapping(excludes = [...])` | アノテーションの `excludes` 引数（ターゲット側のプロパティ名） |

## CopyTo.Exclude

```kt
import me.tbsten.cream.CopyTo

@CopyTo(Target::class)
data class Source(
    val name: String,
    @CopyTo.Exclude val count: Int, // 自動コピーのデフォルトなし — 呼び出し側が指定
)

data class Target(
    val name: String,
    val count: Int,
)

// 生成されるコード:
fun Source.copyToTarget(
    name: String = this.name,
    count: Int,              // 必須 — デフォルトなし
): Target = Target(name = name, count = count)

// usage
val source: Source = /* ... */
val target: Target = source.copyToTarget(
    count = 1, // 必須 — 明示的に渡す必要がある
)
```

## CopyFrom.Exclude

```kt
import me.tbsten.cream.CopyFrom

sealed interface State {
    val name: String
    val count: Int

    @CopyFrom(State::class)
    data class Success(
        val name: String,
        @CopyFrom.Exclude val count: Int, // 自動コピーのデフォルトなし — 呼び出し側が指定
    )
}

// 生成されるコード:
fun State.copyToStateSuccess(
    name: String = this.name,
    count: Int,              // 必須 — デフォルトなし
): State.Success = State.Success(name = name, count = count)

// usage
val state: State = /* ... */
val success: State.Success = state.copyToStateSuccess(
    count = 1, // 必須 — 明示的に渡す必要がある
)
```

## CombineTo.Exclude

片方のソースクラスのプロパティに付けるだけで、**生成されるすべての combine 関数**でそのプロパティが
必須になります（他のソースに同名プロパティがあっても、そちらのデフォルトにはフォールバックしません）。

```kt
import me.tbsten.cream.CombineTo

@CombineTo(Target::class)
data class SourceA(
    @CombineTo.Exclude val shared: String, // 自動コピーのデフォルトなし — 呼び出し側が指定
    val uniqueA: Int,
)

@CombineTo(Target::class)
data class SourceB(
    val shared: String,
    val uniqueB: Boolean,
)

data class Target(
    val shared: String,
    val uniqueA: Int,
    val uniqueB: Boolean,
)

// 生成されるコード (SourceB を receiver とする関数でも shared は必須になります):
fun SourceA.copyToTarget(
    sourceB: SourceB,
    shared: String,                     // 必須 — デフォルトなし
    uniqueA: Int = this.uniqueA,
    uniqueB: Boolean = sourceB.uniqueB,
): Target = Target(shared = shared, uniqueA = uniqueA, uniqueB = uniqueB)

// usage
val target: Target = sourceA.copyToTarget(
    sourceB = sourceB,
    shared = "value", // 必須 — 明示的に渡す必要がある
)
```

## CombineFrom.Exclude

```kt
import me.tbsten.cream.CombineFrom

data class SourceA(
    val name: String,
    val count: Int,
)

data class SourceB(
    val extra: Int,
)

@CombineFrom(SourceA::class, SourceB::class)
data class Target(
    val name: String,
    @CombineFrom.Exclude val count: Int, // 自動コピーのデフォルトなし — 呼び出し側が指定
    val extra: Int,
)

// 生成されるコード:
fun SourceA.copyToTarget(
    sourceB: SourceB,
    name: String = this.name,
    count: Int,              // 必須 — デフォルトなし
    extra: Int = sourceB.extra,
): Target = Target(name = name, count = count, extra = extra)

// usage
val target: Target = sourceA.copyToTarget(
    sourceB = sourceB,
    count = 1, // 必須 — 明示的に渡す必要がある
)
```

## SealedCopy.Exclude

sealed 親の abstract プロパティに付与すると、`@SealedCopy` が生成する `copy()` でそのパラメータが必須になります。

```kt
import me.tbsten.cream.SealedCopy

@SealedCopy
sealed interface MyState {
    val name: String
    @SealedCopy.Exclude val count: Int  // 呼び出し側が count を指定

    data class Loading(override val name: String, override val count: Int) : MyState
}

// 生成されるコード:
fun MyState.copy(
    name: String = this.name,
    count: Int,               // 必須
): MyState = when (this) {
    is MyState.Loading -> this.copy(name = name, count = count)
}

// usage
val state: MyState = MyState.Loading("a", 1)
val updated: MyState = state.copy(count = 2) // 必須 — 明示的に渡す必要がある
```

## CopyToChildren.Exclude

sealed 親のプロパティに付与すると、`@CopyToChildren` が生成する**すべての** per-child コピー関数で
そのパラメータが必須になります。

```kt
import me.tbsten.cream.CopyToChildren

@CopyToChildren
sealed interface UiState {
    val sessionId: String
    @CopyToChildren.Exclude val count: Int  // 全ての per-child コピー関数で必須

    data class Loading(override val sessionId: String, override val count: Int) : UiState
    data class Success(override val sessionId: String, override val count: Int, val data: String) : UiState
}

// 生成されるコード:
fun UiState.copyToUiStateLoading(
    sessionId: String = this.sessionId,
    count: Int,   // 必須
): UiState.Loading = /* ... */

fun UiState.copyToUiStateSuccess(
    sessionId: String = this.sessionId,
    count: Int,   // 必須
    data: String,
): UiState.Success = /* ... */

// usage
val state: UiState = /* ... */
val loading: UiState.Loading = state.copyToUiStateLoading(
    count = 0, // 必須 — 明示的に渡す必要がある
)
```

## CopyMapping / CombineMapping — `excludes`

`@CopyMapping` / `@CombineMapping` は自分のコードでないクラス同士をマッピングするため、
プロパティにアノテーションを付けられません。代わりに、必須にしたい引数を `excludes`
アノテーション引数に列挙します。各エントリは生成される引数名 — つまり生成シグネチャと同じ
**ターゲット側**のプロパティ名（`properties = [Map(...)]` によるリネーム後の名前）— を指定します。

```kt
import me.tbsten.cream.CopyMapping

// ライブラリ X / Y 側 — 変更できない
data class LibXModel(val shareProp: String, val xProp: Int)
data class LibYModel(val shareProp: String, val yProp: Int)

@CopyMapping(
    source = LibXModel::class,
    target = LibYModel::class,
    excludes = ["shareProp"],
)
private object Mapping

// 生成されるコード:
fun LibXModel.copyToLibYModel(
    shareProp: String,       // 必須 — デフォルトなし
    yProp: Int,
): LibYModel = ...
```

`@CombineMapping(excludes = [...])` も同様で、エントリは生成される combine 関数の
ターゲット側の引数名を指定します。

### `canReverse = true` との組み合わせ

`excludes` は**両方向**に適用されます。エントリは反転した `properties` マッピングを通して
変換されます: `Map(source = ..., target = ...)` の `target` を指すエントリは、逆方向関数では
**ソース側**の引数を必須にします。マッピングのないエントリ（同名の共有プロパティ）はそのまま
適用されます。

```kt
@CopyMapping(
    source = LibXModel::class,
    target = LibYModel::class,
    canReverse = true,
    properties = [CopyMapping.Map(source = "xProp", target = "yProp")],
    excludes = ["yProp"],
)
private object Mapping

// 順方向 — yProp が必須:
fun LibXModel.copyToLibYModel(
    shareProp: String = this.shareProp,
    yProp: Int,              // 必須 — デフォルトなし
): LibYModel = ...

// 逆方向 — exclude が反転した Map を通して変換され、xProp が必須:
fun LibYModel.copyToLibXModel(
    shareProp: String = this.shareProp,
    xProp: Int,              // 必須 — デフォルトなし
): LibXModel = ...
```

## 詳細・エッジケース

matched でない引数に `@Exclude` を付けても**効果がなく**、KSP の warning が出ます。
同様に、自動コピーのデフォルトを持つ引数に一致しない `excludes` エントリも効果がなく、KSP の warning が出ます。

`@SealedCopy.Exclude` は `@SealedCopy` が生成する `copy()` 関数のみに効き、`@CopyToChildren.Exclude` は
`@CopyToChildren` が生成する per-child コピー関数のみに効きます。
同じ sealed 型に両方のアノテーションが共存しても互いに干渉しません。

`@CopyMapping` および `@CombineMapping` には `.Exclude` アノテーションがありません
（コピー元/コピー先クラスが自分のコードでないため、プロパティへのアノテーション付与が不可能です）。
代わりに `excludes` アノテーション引数を使ってください。

## 関連ドキュメント

- [Property mapping (.Map)](./property-mapping.ja.md) — デフォルトを除去する代わりに、別名プロパティへマッピングしたい場合
- [Copy — @CopyTo / @CopyFrom / @CopyMapping](../copy.ja.md)
- [Combine — @CombineTo / @CombineFrom / @CombineMapping](../combine.ja.md)
- [Sealed copy — @SealedCopy](../sealed-copy.ja.md)
- [Copy to children — @CopyToChildren](../copy-to-children.ja.md)
