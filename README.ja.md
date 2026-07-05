# cream.kt

![Maven Central Version](https://img.shields.io/maven-central/v/me.tbsten.cream/cream-runtime)
![GitHub License](https://img.shields.io/github/license/TBSten/cream)
[![Ask DeepWiki](https://deepwiki.com/badge.svg)](https://deepwiki.com/TBSten/cream)

[English](./README.md) |
日本語 |
[DeepWiki](https://deepwiki.com/TBSten/cream)

**目次:**
[cream.kt を使う理由](#creamkt-を使う理由) ·
[セットアップ](#セットアップ) ·
[クイックスタート](#クイックスタート) ·
[アノテーション](#アノテーション) ·
[カスタマイズ](#カスタマイズ) ·
[ユースケース](#ユースケース)

---

cream.kt は **宣言的データコピー（Declarative data copy）** を可能にし、**クラスを跨いだ copy** をしやすくする KSP Plugin です。
クラスにアノテーションを付けるだけで、ほぼ同じ形の別クラスへのコピー関数を自動生成します —
名前が一致するプロパティは自動で引き継がれます。

```kt
// 従来の書き方
// ❌ 具体的にどのデータが追加・変更されたかがパッと分かりづらい
MyUiState.Success(
    userName = prevState.userName,    // 手動コピー
    password = prevState.password,    // 手動コピー
    data = newData,
)

// cream.kt を使った書き方 — copyToMyUiStateSuccess が自動生成される
// ✅ data が追加されたことがパッとわかる
prevState.copyToMyUiStateSuccess(data = newData)
```

関数名はカスタマイズできます（例: `toSuccess` に短縮）→ [Function name](doc/customization/fun-name.ja.md)

## cream.kt を使う理由

- **宣言的データコピー** — アノテーション 1 つでコピー関数を生成。名前が一致するプロパティは
  デフォルト引数になるため、変更したい値だけを渡せばよくなります。
- **クラスを跨いだ状態遷移** — data class の `copy()` に似ていますが、クラスを跨げます
  （例: `Loading` → `Success`）。sealed class/interface による状態管理のために設計されています。
- **Kotlin Multiplatform 対応** — ランタイムアノテーションは全 Kotlin プラットフォーム向けに公開されています。

他のマッピングライブラリとの比較は [comparison](doc/comparison.ja.md)（MapStruct, KOMM）も参照してください。

## セットアップ

|                   |                                                                          |
|-------------------|--------------------------------------------------------------------------|
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

Kotlin Multiplatform（commonMain）では KSP の制約により追加セットアップが必要です
→ [Kotlin Multiplatform サポート](doc/customization/multiplatform.ja.md)

## クイックスタート

コピー元クラスに `@CopyTo` を付けると、ターゲットクラスへのコピー関数が生成されます:

```kt
import me.tbsten.cream.CopyTo

@CopyTo(UiState.Success::class)
class UiState {
    data class Success(
        val data: String,
    )
}

// 自動生成
fun UiState.copyToUiStateSuccess(
    data: String,
): UiState.Success = /* ... */

// 使い方
val uiState: UiState = /* ... */
val nextUiState: UiState.Success = uiState.copyToUiStateSuccess(
    data = /* ... */,
)
```

コピー元クラスのプロパティと名前が一致するコンストラクタ引数にはデフォルト値が設定されるため、
変更したい値だけを渡せば OK です。詳細: [Copy](doc/copy.ja.md)

## ユースケース

### UI の状態遷移

Android アプリ開発などの GUI の状態を管理する必要がある場面では sealed interface での状態の管理が便利ですが、状態が移動する際の コンストラクタ呼び出しはわかりにくいものになりがちです。

cream.kt を使うことで sealed interface を使いつつ 状態遷移をシンプルに保てます。

```kt
sealed interface HomeState {
    @CopyTo(Success::class, Error::class)
    data object Loading : HomeState

    data class Success(
        val data: HomeScreenData,
    ) : HomeState

    data class Error(
        val message: String,
    ) : HomeState
}

class HomeViewModel : ViewModel() {
    private val _state = MutableStateFlow<HomeState>(HomeState.Loading)

    fun initialLoad() = viewModelScope.launch {
        val loadingState = HomeState.Loading
        _state.update { loadingState }

        runCatching {
            fetchHomeScreenDataFromServer()
        }.fold(
            onSuccess = { _state.update { loadingState.copyToHomeStateSuccess(data = it) } },
            onFailure = { _state.update { loadingState.copyToHomeStateError(message = it.message ?: "Unknown error") } },
        )
    }
}
```

詳細は [sealed class を使った UI 状態管理](doc/use-case/ui-state-management-by-sealed-class/README.ja.md) を参照してください。

### レイヤーを跨ぐデータ遷移

データレイヤーとドメインレイヤーでモデルを別々に定義するとデータレイヤーの変更をアプリの他の部分（UI レイヤーなど）へ影響しないようにすることができ便利です。

しかし、小中規模のアプリではマッピングはしばしば面倒なボイラープレートを生み出します。cream.kt を利用することで、この詰め替えコードを自動生成に置き換えられます:

```kt
// domain layer
data class Item(
    val itemId: String,
    val name: String,
    val price: Int,
)

// data layer
@CopyTo(Item::class)
data class GetItemApiResponse(
    val itemId: String,
    val name: String,
    val price: Int,
)

class ItemRepositoryImpl : ItemRepository {
    override suspend fun getItem(itemId: String): Item {
        val apiResponse = itemApi.getItem(itemId)
        return apiResponse.copyToItem()
    }
}
```

詳細は [レイヤーを跨ぐモデルマッピング](doc/use-case/model-mapping.ja.md) を参照してください。

## アノテーション

各機能の詳細な情報は以下のドキュメントを参照してください。

| アノテーション | 付ける場所 | 生成されるもの | Docs |
|---|---|---|---|
| `@CopyTo(Target::class)` | コピー元クラス | コピー元 → ターゲットへのコピー関数 | [docs](doc/copy.ja.md#copyto) |
| `@CopyFrom(Source::class)` | ターゲットクラス | `@CopyTo` と同じ（アノテーションをターゲット側に置く） | [docs](doc/copy.ja.md#copyfrom) |
| `@CopyMapping(Source::class, Target::class)` | 自モジュール内の宣言 | 変更できないクラス同士（ライブラリのクラスなど）のコピー関数 | [docs](doc/copy.ja.md#copymapping) |
| `@CopyToChildren` | sealed class/interface | sealed 親から**全ての**子クラスへのコピー関数 | [docs](doc/copy-to-children.ja.md) |
| `@SealedCopy` | sealed class/interface | 子 type を維持する、sealed 親の `copy()` | [docs](doc/sealed-copy.ja.md) |
| `@CombineTo(Target::class)` | 各コピー元クラス | **複数**のコピー元 → 1 つのターゲットへの combine 関数 | [docs](doc/combine.ja.md#combineto) |
| `@CombineFrom(SourceA::class, SourceB::class, ...)` | ターゲットクラス | `@CombineTo` と同じ（アノテーションをターゲット側に置く） | [docs](doc/combine.ja.md#combinefrom) |
| `@CombineMapping(...)` | 自モジュール内の宣言 | 変更できないクラス同士の combine 関数 | [docs](doc/combine.ja.md#combinemapping) |

## カスタマイズ

細かなカスタマイズが必要な場合は以下を参照してください。

| やりたいこと | API | Docs |
|---|---|---|
| 名前が違うプロパティを対応付ける | `.Map`（`@CopyTo.Map` など） | [Property mapping](doc/customization/property-mapping.ja.md) |
| 自動コピーのデフォルト値を外して必須引数にする | `.Exclude`（`@CopyTo.Exclude` など） | [Exclude](doc/customization/exclude.ja.md) |
| 生成される KDoc に説明・例を追加する | `kdoc = KDoc(...)` | [KDoc](doc/customization/kdoc.ja.md) |
| 生成される関数の可視性を制御する | `visibility` / `CopyVisibility` | [Visibility](doc/customization/visibility.ja.md) |
| 生成される関数名を変える（宣言ごと / モジュール全体） | `funName` / `cream.copyFunNamePrefix` / … | [Function name](doc/customization/fun-name.ja.md) |
| モジュール全体の KSP オプションを一覧する | `cream.*` KSP オプション | [KSP Options](doc/customization/options.ja.md) |

