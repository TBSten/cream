# cream.kt

![Maven Central Version](https://img.shields.io/maven-central/v/me.tbsten.cream/cream-runtime)
![GitHub License](https://img.shields.io/github/license/TBSten/cream)
[![Ask DeepWiki](https://deepwiki.com/badge.svg)](https://deepwiki.com/TBSten/cream)

<a href="https://github.com/TBSten/cream/blob/main/README.md">English</a> |
日本語 |
<a href="https://deepwiki.com/TBSten/cream">DeepWiki</a>

cream.kt は **宣言的データコピー（Declarative data copy）** を可能にし、**クラスを跨いだ copy** をしやすくする KSP Plugin です。

あるオブジェクトをほぼ同じクラスの別インスタンスへコピーする Mapper を自動生成します。

## ⭐️ 0. 要点

**クラスを跨いだ copy を自動生成する KSP Plugin**

- **Before**: 手動でプロパティを一つずつコピー
- **After**: `prevState.toNextState(data = newData)` で変換。自明なデータの引き継ぎを省略できるため、可読性が向上します。

```kt
// 従来の書き方
// ❌ 具体的のどのデータが追加・変更されたかがパッと分かりずらい
MyUiState.Success(
    userName = prevState.userName,    // 手動コピー
    password = prevState.password,    // 手動コピー
    data = newData
)

// cream.kt を使った書き方
// (toSuccess が自動生成される)
// ✅ data が追加されたことがパッとわかる
prevState.toSuccess(data = newData)  // 自動コピー
```

## 🤔 1. モチベーション

あなたのプロジェクトに以下のような UiState があったとします。

```kt
sealed interface MyUiState {
    val userName: String
    val password: String

    data class Loading(
        override val userName: String,
        override val password: String,
    ) : MyUiState

    data class Stable(
        override val userName: String,
        override val password: String,
        val loadedData: List<String>,
    ) : MyUiState
}
```

MyUiState が Loading から Stable に遷移するとします。
その場合

```kt
val prevState: MyUiState.Loading = TODO()
val loadedData: List<String> = TODO()

MyUiState.Stable(
    // ⚠️ See here !
    userName = prevState.userName,
    password = prevState.password,
    loadedData = loadedData,
)
```

「⚠️ See here !」の下の 2 行に注目してください。
prevState からデータを引き継いで Stable state をインスタンス化していますが、これでは MyUiState
の変更 (ex. プロパティの追加, 削除) に 子クラスである MyUiState.Stable が影響を受けてしまいます。
MyUiState のプロパティが増えるたびにこのコピーのコードも増やす必要が出てきてしまいます。

cream.kt を使用することで先ほどのコードは以下のように簡略化できます。

```kt
val prevState: MyUiState.Loading = TODO()
val loadedData: List<String> = TODO()

prevState.toStable(
    loadedData = loadedData,
)
```

`userName = prevState.userName, password = prevState.password,` の部分がなくなりスッキリしました。

特に理由がなければ以前の値（上の例では prevState: MyUiState.Loading ）を引き継ぐという動作は **data
class の copy メソッド** に似ています。
copy と違い、**cream.kt では クラスを跨いだ状態遷移** も可能にします(上記の例では .Loading -> .Stable
へクラスを跨いで状態をコピーしています)。

## ⚙️ 2. セットアップ

|                   |                                                                         |
|-------------------|-------------------------------------------------------------------------|
| `<cream-version>` | ![GitHub Release](https://img.shields.io/github/v/release/TBSten/cream) |
| `<ksp-version>`   | ![GitHub Release](https://img.shields.io/github/v/release/google/ksp)   |

```kts
// module/build.gradle.kts
plugins {
    id("com.google.devtools.ksp") version "<ksp-version>"
}

dependencies {
    implementation("me.tbsten.cream:cream-runtime:<cream-version>")
    ksp("me.tbsten.cream:cream-ksp:<cream-version>")
}
```

<details>

<summary> Kotlin Multiplatform プロジェクト </summary>

現在 KSP は Kotlin Multiplatform の commonMain
のような中間ソースセットにコードを生成することをサポートしていません。 ([参照](https://github.com/google/ksp/issues/567))
この制限により現在 cream.kt では commonMain などのクラスからコピー関数を生成することはできません。

ただし 以下のようにセットアップすることで commonMain コードのみ コード生成させることが可能になります。
(この場合 各プラットフォームのアノテーションは処理されない点に注意してください。)

```kt
fun Project.setupKspForMultiplatformWorkaround() {
    kotlin.sourceSets.commonMain {
        kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
    }

    tasks.configureEach {
        if (name.startsWith("ksp") && name != "kspCommonMainKotlinMetadata") {
            dependsOn(tasks.named("kspCommonMainKotlinMetadata"))
            enabled = false
        }
    }
}
setupKspForMultiplatformWorkaround()
```

参考: https://github.com/TBSten/cream/blob/main/test/build.gradle.kts#L54-L66

</details>

## ❇️ 3. 利用方法

### CopyTo

`@CopyTo` を付与したクラスから指定した遷移先のクラスへ遷移する copy 関数を生成します。

```kt
@CopyTo(UiState.Success::class)
class UiState {
    data class Success(
        val data: Data,
    )
}

// auto generate
fun UiState.copyToUiStateSuccess(
    data: Data,
): UiState.Success = /* ... */

// usage
val uiState: UiState = /* ... */
val nextUiState: UiState.Success = uiState.copyToUiStateSuccess(
    data = /* ... */,
)
```

copy 関数は遷移先クラスのコンストラクタごとに生成されます。
遷移元クラスのプロパティ名と一致するコンストラクタの引数はデフォルト値が設定されます。

```kt
@CopyTo(UiState.Success::class)
class ItemDetailUiState(
    val itemId: String
) {
    data class Success(
        override val itemId: String,
        val data: Data,
    )
}

// auto generate
fun UiState.copyToUiStateSuccess(
    itemId: String = this.itemId,
    data: Data,
): UiState.Success = /* ... */

// usage
val uiState: UiState = /* ... */
val nextUiState: UiState.Success = uiState.copyToUiStateSuccess(
    data = /* ... */,
)
```

### CopyFrom

`@CopyTo` と似ていますが、引数に **遷移元** のクラスを指定する点が違います。

```kt
data class DataLayerModel(
    val data: Data,
)

@CopyFrom(DataLayerModel::class)
data class DomainLayerModel(
    val data: Data,
)

// auto generate
fun DataLayerModel.toDomainLayerModel(
    data: Data,
): DomainLayerModel = /* ... */
```

### CopyToChildren

sealed class/interface に付与することで、その sealed class/interface -> 継承するすべてのクラス
へコピーするコピー関数を自動生成します。

```kt
@CopyToChildren
sealed interface UiState {
    data object Loading : UiState

    sealed interface Success : UiState {
        val data: Data

        data class Done(
            override val data: Data,
        ) : Success

        data class Refreshing(
            override val data: Data,
        ) : Success
    }
}

// auto generate
fun UiState.copyToUiStateSuccessDone(
    data: Data,
): UiState.Success.Done = /* ... */

fun UiState.copyToUiStateSuccessRefreshing(
    data: Data,
): UiState.Success.Refreshing = /* ... */
```

これは各 sealed class/interface に @CopyTo を都度指定するよりも圧倒的に楽です。

### CombineTo

`@CombineTo` を使用すると、**複数のソースクラスから1つのターゲットクラスへ**コピー関数を生成できます。
複数のデータソースを組み合わせて1つの状態を作成する際に便利です。

```kt
@CombineTo(SuccessState::class)
data class LoadingState(
    val itemId: String,
)

@CombineTo(SuccessState::class)
data class SuccessAction(
    val data: Data,
)

data class SuccessState(
    val itemId: String,  // from LoadingState.itemId
    val data: Data,      // from SuccessAction.data
    val lastUpdateAt: Date,
)

// auto generate
fun LoadingState.copyToSuccessState(
    successAction: SuccessAction,
    itemId: String = this.itemId,
    data: Data = successAction.data,
    lastUpdateAt: Date,
): SuccessState = /* ... */

// usage
val loadingState: LoadingState = /* ... */
val action: SuccessAction = /* ... */
val successState: SuccessState = loadingState.copyToSuccessState(
    successAction = action,
    lastUpdateAt = Date(),
)
```

複数のソースクラスに同じプロパティ名がある場合、**後に宣言されたソースクラス** の値が優先されます。

### CombineFrom

`@CombineFrom` は `@CombineTo` の逆で、**ターゲット側**に複数のソースクラスを指定します。

```kt
data class LoadingState(
    val itemId: String,
)

data class SuccessAction(
    val data: Data,
)

@CombineFrom(LoadingState::class, SuccessAction::class)
data class SuccessState(
    val itemId: String,  // from LoadingState.itemId
    val data: Data,      // from SuccessAction.data
    val lastUpdateAt: Date,
)

// auto generate
fun LoadingState.copyToSuccessState(
    successAction: SuccessAction,
    itemId: String = this.itemId,
    data: Data = successAction.data,
    lastUpdateAt: Date,
): SuccessState = /* ... */
```

`@CombineTo` と `@CombineFrom` は生成される関数は同じですが、アノテーションを付ける場所が異なります。

- ソース側を編集できる場合は `@CombineTo`
- ターゲット側を編集できる場合は `@CombineFrom`

を選択してください。

### CopyTo.Map, CopyFrom.Map, CombineTo.Map, CombineFrom.Map

`@CopyTo.Map`、`@CopyFrom.Map`、`@CombineTo.Map`、`@CombineFrom.Map` を使用してプロパティごとに対応するプロパティを指定できます。
これはコピー元とコピー先でプロパティ名が違う時にマッピングするのに便利です。

#### CopyTo.Map / CopyFrom.Map

```kt
@CopyTo(DataModel::class)
data class DomainModel(
    @CopyTo.Map("dataId")
    val domainId: String,
)

data class DataModel(
    val dataId: String,
)

// auto genarate
fun DomainModel.copyToDataModel(
    dataId: String = this.domainId, // domainId と dataId がマッピングされます
): DataModel = ...
```

```kt
@CopyFrom(DataModel::class)
data class DomainModel(
    @CopyFrom.Map("dataId")
    val domainId: String,
)

data class DataModel(
    val dataId: String,
)

// auto generate
fun DataModel.copyToDomainModel(
    domainId: String = this.dataId, // dataId と domainId がマッピングされます
)
```

#### CombineTo.Map / CombineFrom.Map

複数のソースクラスから1つのターゲットクラスへコピーする際も同様にプロパティマッピングが可能です。

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

// auto generate
fun SourceA.copyToTargetState(
    sourceB: SourceB,
    targetProperty: String = this.sourceProperty, // sourceProperty と targetProperty がマッピングされます
    otherProperty: Int = sourceB.otherProperty,
): TargetState = ...
```

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

// auto generate
fun SourceA.copyToTargetState(
    sourceB: SourceB,
    targetProperty: String = this.sourceProperty, // sourceProperty と targetProperty がマッピングされます
    otherProperty: Int = sourceB.otherSourceProperty, // otherSourceProperty と otherProperty がマッピングされます
): TargetState = ...
```

### CopyMapping

コピー元/コピー先クラスが両方とも自分のソースコードではないが、コピー関数を生成したい場合は CopyMapping を使用できます。
これにより コピー元クラス, コピー先クラスを両方とも一切編集することなく それらの関数のコピー関数を生成することが可能です。

```kt
// in library X
data class LibXModel(
    val shareProp: String,
    val xProp: Int,
)

// in library Y
data class LibYModel(
    val shareProp: String,
    val yProp: Int,
)

// in your module
@CopyMapping(LibXModel::class, LibYModel::class)
private object Mapping

// auto generate
fun LibXModel.copyToLibYModel(
    shareProp: String = this.shareProp,
    yProp: Int,
): LibYModel = ...
```

## 💻 4. 利用例

主に想定されている cream.kt のユースケースを以下に示します。
それぞれのユースケース向けの [Context7](https://context7.com/) を利用すると あなたの生成 AI に cream.kt
の情報を即座に適用できるため便利でしょう。

- ViewModel などでの sealed interface/class を使った状態管理における、状態遷移のコードを改善する。
    - [Context7 ドキュメント](https://context7.com/tbsten/cream?topic=Improve+ViewModel+state+management&tokens=2000)
- Data <-> Domain などのレイヤーを跨ぐ際にデータモデルを変換する必要がある際に、データモデルのコピーを改善する。
    - [Context7 ドキュメント](https://context7.com/tbsten/cream?topic=Cross-Layer+Data+Model+Copy&tokens=2000)

(利用例は一例であり、cream.kt の利用範囲を制限するものではありません。他のユースケースで不都合がある場合は issue
で作成してください。)

## 🔨 5. オプション

生成される copy 関数の名前をカスタマイズするためのいくつかのオプションが用意されています。
すべてのオプションの設定は任意です。必要に応じて設定してください。

各オプションの動作を確認するためには [Option Builder](http://tbsten.github.io/cream/option-builder) が便利です。

各オプション設定時の生成されるコピー関数名の詳細な例は、
[@CopyFunctionNameTest.kt](./cream-ksp/src/test/kotlin/me/tbsten/cream/ksp/transform/CopyFunctionNameTest.kt)
のテストケースも参考にしてください。

```kts
// module/build.gradle.kts

ksp {
    arg("cream.copyFunNamePrefix", "copyTo")
    arg("cream.copyFunNamingStrategy", "under-package")
    arg("cream.escapeDot", "replace-to-underscore")
    arg("cream.notCopyToObject", "false")
}
```

### オプションの一覧

| オプション名                            | 説明                                                          | 設定例                                                                      | デフォルト              |
|-----------------------------------|-------------------------------------------------------------|--------------------------------------------------------------------------|--------------------|
| **`cream.copyFunNamePrefix`**     | 生成されるコピー関数の先頭につく文字列                                         | `copyTo`, `transitionTo`, `to`, `mapTo`                                  | `copyTo`           |
| **`cream.copyFunNamingStrategy`** | コピー関数の命名方法。                                                 | `under-package`, `diff-parent`, `simple-name`, `full-name`, `inner-name` | `under-package`    |
| **`cream.escapeDot`**             | `cream.copyFunNamingStrategy` で命名された名前に含まれる `.` をエスケープする方法。 | `replace-to-underscore`, `pascal-case`, `backquote`                      | `lower-camel-case` |
| **`cream.notCopyToObject`**       | `true` の場合 @CopyToChildren で object へのコピー関数を生成しないようにします。    | `true` , `false`                                                         | `false`            |

### オプション 1. `cream.copyFunNamePrefix`

| デフォルト    | 設定可能な値 |
|----------|--------|
| `copyTo` | 任意の文字列 |

生成されるコピー関数名の先頭につく クラス名を設定します。
`copyTo` や `to` などのコピーや状態の遷移を表す端的な文字列を設定してください。

### オプション 2. `cream.copyFunNamingStrategy`

| デフォルト           | 設定可能な値                                                                          |
|-----------------|---------------------------------------------------------------------------------|
| `under-package` | `under-package`, `diff-parent`, `simple-name`, `full-name`, `inner-name`　のいずれか。 |

コピー関数の prefix 以降のクラス名文字列の設定方法です。以下の表に示す設定方法をサポートします。
これら以外の命名方法が欲しい場合は [issue](https://github.com/TBSten/cream/issues?q=sort%3Aupdated-desc+is%3Aissue+is%3Aopen)
にリクエストしてください。

| 設定値             | 説明                                                                | `com.example.Aaa.Bbb` -> `com.example.Aaa.Bbb.Ccc.Ddd` に遷移するコピー関数を生成する例 |
|-----------------|-------------------------------------------------------------------|-------------------------------------------------------------------------|
| `under-package` | パッケージ階層を反映した名前を使用します。                                             | Hoge.Fuga.copyTo **`Aaa.Bbb.Ccc.Ddd`** (...)                            |
| `diff-parent`   | 遷移元クラスとの差分のみを含めた名前を使用する。                                          | Hoge.Fuga.copyTo **`CccDdd`** (...)                                     |
| `simple-name`   | 遷移先クラス::class.simpleName を使用する。                                   | Hoge.Fuga.copyTo **`Ddd`** (...)                                        |
| `full-name`     | 対象クラス::class.qualifiedName を使用する。                                 | Hoge.Fuga.copyTo **`ComExampleAaaBbbCccDdd`** (...)                     |
| `inner-name`    | ネストされたクラスの 2 階層目以降のクラス名を使用する。（ネストされていないクラスの場合は `simple-name` と同じ） | Hoge.Fuga.copyTo **`BbbCccDdd`** (...)                                  |

<img src="./doc/cream.copyFunNamingStrategy.png" width="800" />

### オプション 3. `cream.escapeDot`

| デフォルト              | 設定可能な値                                                     |
|--------------------|------------------------------------------------------------|
| `lower-camel-case` | `replace-to-underscore`, `pascal-case`, `backquote`　のいずれか。 |

`cream.copyFunNamingStrategy` で取得したクラス名をエスケープする方法を設定します。

Kotlin の関数名には通常 `.` を含めることはできないため 設定例に示すいずれかの方法で命名可能な文字列に変更する必要があります。

| 設定値                     | 説明                                 | `com.example.Hoge.Fuga` -> `com.example.Hoge.Piyo` に遷移するコピー関数を生成する例 |
|-------------------------|------------------------------------|---------------------------------------------------------------------|
| `lower-camel-case`      | ドットで区切られた各要素をキャメルケースで連結し、先頭を小文字にする | Hoge.Fuga.copyTohogePiyo(...)                                       |
| `replace-to-underscore` | ドットをアンダースコアに置換する                   | Hoge.Fuga.copyTo_hoge_piyo(...)                                     |
| `backquote`             | ドットを含む完全な名前をバッククォート（\``...`\`）で囲む  | Hoge.Fuga.\`copyTocom.example.Hoge.Piyo`\(...)                      |

### Option 4. `cream.notCopyToObject`

| デフォルト   | 設定可能な値                |
|---------|-----------------------|
| `false` | `true`,`false` のいずれか。 |

true を設定すると、あるクラスから object へのコピー関数を生成しなくなります。

object へのコピー関数は、実際にはコピーではなく object のインスタンスをそのまま返します。
これがあなたの好みに合わない場合、このオプションに `true` を設定して data object へのコピーを抑止できます。

またこのオプションはモジュール全体に影響しますが、 `@CopyToChildren` の notCopyToObject プロパティを
true にすることで アノテーションをつけたクラスのみに絞ることも可能です。

## 🆚 6. 他のライブラリとの比較

Kotlin のデータマッピングライブラリを選択する際、いくつかの選択肢があります。ここでは cream.kt と他の人気ライブラリとの比較を示します。

### vs. MapStruct

**MapStruct** は、異なるオブジェクトタイプ間のマッピングに特化した成熟した Java ベースのコード生成ライブラリです。

| 機能 | cream.kt | MapStruct |
|---------|----------|-----------|
| **言語** | Kotlin ファーストで KSP を使用 | Java ファーストでアノテーション処理 |
| **状態遷移** | ✅ sealed class の状態遷移に最適化 | ❌ Entity-DTO マッピングに特化 |
| **デフォルト値のオーバーライド** | ✅ 生成された関数でデフォルト値をオーバーライド可能 | ⚠️ デフォルト値の扱いが限定的 |
| **マルチプラットフォーム** | ✅ Kotlin Multiplatform サポート | ❌ JVM のみ |
| **IDE サポート** | ✅ ネイティブな Kotlin IDE 統合 | ⚠️ Java プロジェクトに最適 |
| **ユースケース** | フロントエンド状態管理（UI 状態など） | バックエンド Entity-DTO 変換 |

**MapStruct より cream.kt を選ぶべきケース:**
- Kotlin で開発している（特に Kotlin Multiplatform）
- sealed class での UI 状態遷移を管理する必要がある
- 状態遷移時にデフォルト値をオーバーライドしたい
- 軽量で Kotlin ネイティブなソリューションを好む

### vs. KOMM (Kotlin Object Multiplatform Mapper)

**KOMM** は軽量な Kotlin Multiplatform マッピングライブラリで、同じく KSP を使用してコード生成を行います。

| 機能 | cream.kt | KOMM |
|---------|----------|------|
| **構造の不一致処理** | ✅ ソースとターゲットの構造が異なる場合の処理が優れている | ⚠️ より多くの手動設定が必要 |
| **デフォルト値のオーバーライド** | ✅ マッチしたプロパティはすべてデフォルト値が設定され、オーバーライド可能 | ⚠️ より限定的なデフォルト値の扱い |
| **高度な機能** | ✅ `@CopyToChildren`、`@CombineTo`、`@CopyMapping` | ⚠️ よりシンプルな機能セット |
| **Object シングルトンへのコピー** | ✅ `object` 型へのコピー（オプトアウト可能） | ❌ サポートなし |
| **複雑さ** | ⚠️ より多くの機能 = 学習曲線がやや急 | ✅ よりシンプルで軽量 |
| **柔軟性** | ⚠️ 状態管理パターンに特化している | ✅ より汎用的な柔軟性 |

**KOMM より cream.kt を選ぶべきケース:**
- 複雑な状態管理を持つアプリケーションを構築している（例: [Tart](https://github.com/TBSten/tart) のようなライブラリを使用）
- sealed interface からすべての子クラスへコピーする必要がある（`@CopyToChildren`）
- UI 状態のための sealed class 階層を頻繁に扱う
- 複数のソースクラスを 1 つのターゲットに結合する必要がある（`@CombineTo`）
- ソースコードを変更せずにライブラリ間マッピングが必要（`@CopyMapping`）

**KOMM の方が適しているケース:**
- よりシンプルで汎用的なマッピングライブラリが欲しい
- 意見の強いパターンよりも最大限の柔軟性を好む
- 複雑な状態階層なしでよりシンプルなマッピングニーズがある
