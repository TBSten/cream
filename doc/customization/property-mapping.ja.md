[← README](../../README.ja.md) | [English](./property-mapping.md)

# プロパティマッピング (.Map)

`@CopyTo.Map`、`@CopyFrom.Map`、`@CopyToChildren.Map`、`@SealedCopy.Map`、`@CombineTo.Map`、`@CombineFrom.Map` を使用して、名前の異なるプロパティ同士を紐づけられます。
生成されるコピー関数の両側 — コピー元とコピー先のクラス、あるいは sealed 親とその子 — で同じ値が別名になっている時に便利です。

> [!NOTE]
> `.Map` は、変更できない *2 つのクラスの間* にコピー関数そのものを生成する
> [`@CopyMapping`](../copy.ja.md#copymapping) とは別物です。
> 
> このページの `.Map` アノテーションは、生成された関数の 1 つの引数を別名のプロパティに紐づけるための仕組みです。

## CopyTo.Map / CopyFrom.Map

```kt
import me.tbsten.cream.CopyTo

@CopyTo(DataModel::class)
data class DomainModel(
    @CopyTo.Map("dataId")
    val domainId: String,
)

data class DataModel(
    val dataId: String,
)

// usage
val domainModel = DomainModel(domainId = "id-1")
val dataModel: DataModel = domainModel.copyToDataModel()
// dataModel.dataId == "id-1"
```

<details>
<summary>生成されるコード</summary>

```kt
// auto generate
fun DomainModel.copyToDataModel(
    dataId: String = this.domainId, // domainId と dataId がマッピングされます
): DataModel = ...
```

</details>

```kt
@CopyFrom(DataModel::class)
data class DomainModel(
    @CopyFrom.Map("dataId")
    val domainId: String,
)

data class DataModel(
    val dataId: String,
)

// usage
val dataModel = DataModel(dataId = "id-1")
val domainModel: DomainModel = dataModel.copyToDomainModel()
// domainModel.domainId == "id-1"
```


<details>
<summary>生成されるコード</summary>

```kt
// auto generate
fun DataModel.copyToDomainModel(
    domainId: String = this.dataId, // dataId と domainId がマッピングされます
): DomainModel = ...
```

</details>

## CombineTo.Map / CombineFrom.Map

複数のソースクラスから 1 つのターゲットクラスへコピーする際も同様にプロパティマッピングが可能です。

**ソース側でマッピングを指定する場合:**

```kt
@CombineTo(TargetState::class)
data class SourceA(
    @CombineTo.Map("targetProperty")
    val sourceProperty: String,
)

@CombineTo(TargetState::class)
data class SourceB(
    val otherProperty: Int,
)

data class TargetState(
    val targetProperty: String,
    val otherProperty: Int,
)

// usage
val sourceA = SourceA(sourceProperty = "value")
val sourceB = SourceB(otherProperty = 42)
val target: TargetState = sourceA.copyToTargetState(sourceB)
// target.targetProperty == "value", target.otherProperty == 42
```

<details>
<summary>生成されるコード</summary>

```kt
// auto generate
fun SourceA.copyToTargetState(
    sourceB: SourceB,
    targetProperty: String = this.sourceProperty, // sourceProperty と targetProperty がマッピングされます
    otherProperty: Int = sourceB.otherProperty,
): TargetState = ...
```

</details>

**ターゲット側でマッピングを指定する場合:**

```kt
data class SourceA(
    val sourceProperty: String,
)

data class SourceB(
    val otherSourceProperty: Int,
)

@CombineFrom(SourceA::class, SourceB::class)
data class TargetState(
    @CombineFrom.Map("sourceProperty")
    val targetProperty: String,
    @CombineFrom.Map("otherSourceProperty")
    val otherProperty: Int,
)

// usage
val sourceA = SourceA(sourceProperty = "value")
val sourceB = SourceB(otherSourceProperty = 42)
val target: TargetState = sourceA.copyToTargetState(sourceB)
// target.targetProperty == "value", target.otherProperty == 42
```

<details>
<summary>生成されるコード</summary>

```kt
// auto generate
fun SourceA.copyToTargetState(
    sourceB: SourceB,
    targetProperty: String = this.sourceProperty, // sourceProperty と targetProperty がマッピングされます
    otherProperty: Int = sourceB.otherSourceProperty, // otherSourceProperty と otherProperty がマッピングされます
): TargetState = ...
```

</details>

## CopyToChildren.Map

`@CopyToChildren.Map` は sealed 親に宣言したプロパティに付与し、子クラスの別名の
コンストラクタパラメータに紐づけます。

```kt
import me.tbsten.cream.CopyToChildren

@CopyToChildren
sealed interface UiState {
    @CopyToChildren.Map("id")
    val sessionId: String

    data class Loading(val id: String) : UiState {
        override val sessionId: String get() = id
    }

    data class Success(val id: String, val data: String) : UiState {
        override val sessionId: String get() = id
    }
}

// usage
val state: UiState = UiState.Loading(id = "id-1")
val success: UiState.Success = state.copyToUiStateSuccess(data = "data")
// success.id == "id-1"
```

<details>
<summary>生成されるコード</summary>

```kt
// auto generate
fun UiState.copyToUiStateLoading(
    id: String = this.sessionId, // sessionId と id がマッピングされます
): UiState.Loading = ...

fun UiState.copyToUiStateSuccess(
    id: String = this.sessionId, // sessionId と id がマッピングされます
    data: String,
): UiState.Success = ...
```

</details>

## SealedCopy.Map

`@SealedCopy.Map` も「別名の対応先にマッピングする」という同じ意味を持ちますが、付与する場所は
`@SealedCopy.Via` で指定した委譲関数のパラメータで、そのパラメータを sealed 親の別名の
abstract プロパティに紐づけます。

詳細と例は [Sealed copy — @SealedCopy](../sealed-copy.ja.md) を参照してください。

## 関連ドキュメント

- [Excluding auto-copy defaults (.Exclude)](./exclude.ja.md) — マッピングではなく、引数の自動コピーデフォルトを除去したい場合
- [Copy — @CopyTo / @CopyFrom / @CopyMapping](../copy.ja.md)
- [Copy to children — @CopyToChildren](../copy-to-children.ja.md)
- [Combine — @CombineTo / @CombineFrom / @CombineMapping](../combine.ja.md)
- [Sealed copy — @SealedCopy](../sealed-copy.ja.md)
