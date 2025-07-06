# cream.kt

![Maven Central Version](https://img.shields.io/maven-central/v/me.tbsten.cream/cream-runtime)
![GitHub License](https://img.shields.io/github/license/TBSten/cream)

<a href="https://github.com/TBSten/cream/blob/main/README.md">English</a> |
日本語 | <a href="https://deepwiki.com/TBSten/cream">DeepWiki</a>

cream.kt はクラスを跨いだ copy をしやすくする KSP Plugin です。

## 0. 一言要点

- `@CopyTo(<target-class>::class)`, `@CopyFrom(<source-class>::class)` を付与したクラスに copy
  関数を生成します。
    - 生成される copy 関数の例: `fun UiState.toLoading(): Loading`,
      `fun UiState.toSuccess(data: Data): Success`
- `@CopyToChildren` を付与したクラスからそのすべての子クラスへのコピー関数を生成します。

## 1. モチベーション

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

## 2. セットアップ

![Maven Central Version](https://img.shields.io/maven-central/v/me.tbsten.cream/cream-runtime)

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

## 3. 利用方法

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
data class DataModelLayer(
    val data: Data,
)

@CopyFrom(DataModelLayer::class)
data class DomainModelLayer(
    val data: Data,
)

// auto generate
fun DataModelLayer.toDomainModelLayer(
    data: Data,
): UiState.Success = /* ... */
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

## 4. オプション

挙動をカスタマイズするためのいくつかのオプションが用意されています。
すべてのオプションの設定は任意です。必要に応じて設定してください。

```kts
// module/build.gradle.kts

ksp {
    arg("cream.copyFunNamePrefix", "copyTo")
    arg("cream.copyFunNamingStrategy", "under-package")
    arg("cream.escapeDot", "replace-to-underscore")
}
```

| オプション                         | 説明                                                    | デフォルト                     | 設定例                                                        |                                                                                                                     |
|-------------------------------|-------------------------------------------------------|---------------------------|------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------|
| `cream.copyFunNamePrefix`     | 生成される copy 関数 の名前のプレフィックス。任意の文字列を設定できます。              | `copyTo`                  | `copyTo`, `transitionTo`, `mapTo`                          |                                                                                                                     |
|                               |                                                       |                           | `copyTo`                                                   | `copyToHoge`, `copyToFuga` のような関数が生成されるようになります。                                                                     |
| `cream.copyFunNamingStrategy` | 生成される copy 関数 の `cream.copyFunNamePrefix` 以降の名前の設定方法。 | `under-package`           | `under-package`, `diff-parent`, `simple-name`, `full-name` |                                                                                                                     |
|                               |                                                       |                           | `under-package`                                            | `com.example.ParentClass.ChildClass` -> プレフィックス + `ParentClassChildClass`(...) のような関数が生成されます                        |
|                               |                                                       |                           | `diff-parent`                                              | `com.example.ParentClass` から `com.example.ParentClass.ChildClass` にコピー -> プレフィックス + `ChildClass`(...) のような関数が生成されます |
|                               |                                                       |                           | `simple-name`                                              | `com.example.ParentClass.ChildClass` -> プレフィックス + `ChildClass`(...) のような関数が生成されます                                   |
|                               |                                                       |                           | `full-name`                                                | `com.example.ParentClass.ChildClass` -> プレフィックス + `com.example.ParentClass.ChildClass`(...) のような関数が生成されます           |
| `cream.escapeDot`             | 生成される copy 関数名の `.` をエスケープする方法。                       | `"replace-to-underscore"` | `replace-to-underscore`, `pascal-case`                     |                                                                                                                     |
|                               |                                                       |                           | `replace-to-underscore`                                    | `.` が `_` に置き換えられます。                                                                                                |
|                               |                                                       |                           | `pascal-case`                                              | `.` を単語区切りとみなし、各単語の先頭を大文字にして連結した文字列になります。                                                                           |
