[← README](../../README.ja.md) | [English](./fun-name.md)

# 関数名をカスタマイズする

cream.kt が生成するコピー関数の名前をカスタマイズしたい場合のガイドです。

関数名のカスタマイズにはいくつかオプションが存在します。

- アノテーションの funName プロパティを設定する
- ksp option を設定してモジュール全体の関数名の命名規則を設定する

## アノテーションの funName プロパティを設定する

各アノテーションに用意されている funName プロパティを使用して、そのアノテーションから生成する 関数名を設定します。

```kt
import me.tbsten.cream.CopyTo

@CopyTo(UiState.Success::class, funName = "copyToSuccess")
data object Loading

fun Loading.copyToSuccess(): UiState.Success
```

funName には事前に設定された トークンと組み合わせることで動的に関数名を変えることも可能です。

```kt
import me.tbsten.cream.CopyTo
import me.tbsten.cream.CopyTargetSimpleName

@CopyTo(UiState.Success::class, funName = "copyTo${CopyTargetSimpleName}")
data object Loading

// この場合 CopyTargetSimpleName は `Success` に置き換えられます。

// auto generate
fun Loading.copyToSuccess(): UiState.Success = ...
```

使用できるトークンは以下のとおりです。

| トークン | 展開結果（遷移先が `com.example.UiState.Success` の場合） |
|---|---|
| `DefaultCopyFunctionName` | cream が導出したデフォルト名（`copyToUiStateSuccess`。`@SealedCopy` では `copy`） |
| `CopyTargetSimpleName` / `copy_target_simple_name` | `Success` / `success` |
| `CopyTargetUnderPackage` / `copy_target_under_package` | `UiStateSuccess` / `uistate_success` |
| `CopyTargetInnerName` / `copy_target_inner_name` | `Success` / `success` |
| `CopyTargetFullName` / `copy_target_full_name` | `ComExampleUiStateSuccess` / `com_example_uistate_success` |

1 つのアノテーションから複数の関数が生成される場合（複数ターゲット・sealed への適用など）、
リテラルだけの funName は全関数が同名になってしまうためビルド時エラーになります。
トークンを含めて、関数ごとに異なる名前になるようにしてください。

## ksp option を設定してモジュール全体の関数名の命名規則を設定する

ksp option を使用することでモジュール全体のコピー関数の命名規則を制御できます。

```kts
// module/build.gradle.kts

ksp {
    arg("cream.copyFunNamePrefix", "copyTo")
    arg("cream.copyFunNamingStrategy", "under-package")
    arg("cream.escapeDot", "replace-to-underscore")
}
```

### `cream.copyFunNamePrefix`

| デフォルト | 設定可能な値 |
|-----------|--------------|
| `copyTo`  | 任意の文字列 |

生成されるコピー関数名の先頭に付ける文字列を設定します。`copyTo` や `to` など、コピーや状態遷移を
表す分かりやすい文字列を設定してください。プレフィックスが英字で終わる場合、続く名前の先頭は
大文字化されて連結されます（例: `copyTo` + `uiStateSuccess` → `copyToUiStateSuccess`）。

```kts
// module/build.gradle.kts
ksp {
    arg("cream.copyFunNamePrefix", "transitionTo")
}
```

```kt
@CopyTo(UiState.Success::class)
data object Loading

// auto generate — プレフィックスが transitionTo になる
fun Loading.transitionToUiStateSuccess(/* ... */): UiState.Success = /* ... */
```

### `cream.copyFunNamingStrategy`

| デフォルト        | 設定可能な値 |
|------------------|--------------|
| `under-package`  | `under-package`, `diff`, `simple-name`, `full-name`, `inner-name` のいずれか |

コピー関数名のプレフィックスの後ろに続く「クラス名部分」の作り方を設定します
（下表の例はすべて `com.example.Aaa.Bbb` -> `com.example.Aaa.Bbb.Ccc.Ddd` への遷移の場合です）。

| 設定値           | 説明                                                                | strategy の結果 → 生成される関数                                          |
|-----------------|---------------------------------------------------------------------|----------------------------------------------------------------------------|
| `under-package` | パッケージ階層を反映した名前を使用します。                          | `Aaa.Bbb.Ccc.Ddd` → `Aaa.Bbb.copyToAaaBbbCccDdd(...)`                      |
| `diff`          | 遷移元クラスとの差分のみを含めた名前を使用する。                    | `.Ccc.Ddd` → `Aaa.Bbb.copyToCccDdd(...)`                                   |
| `simple-name`   | 遷移先クラス::class.simpleName を使用する。                         | `Ddd` → `Aaa.Bbb.copyToDdd(...)`                                           |
| `full-name`     | 対象クラス::class.qualifiedName を使用する。                        | `com.example.Aaa.Bbb.Ccc.Ddd` → `Aaa.Bbb.copyToComExampleAaaBbbCccDdd(...)` |
| `inner-name`    | ネストされたクラスの 2 階層目以降のクラス名を使用する。（ネストされていないクラスの場合は `simple-name` と同じ） | `Bbb.Ccc.Ddd` → `Aaa.Bbb.copyToBbbCccDdd(...)` |

<img src="../cream.copyFunNamingStrategy.png" width="800" alt="各 strategy がクラスの完全修飾名のどの部分を使うかの図解" />

```kts
// module/build.gradle.kts
ksp {
    arg("cream.copyFunNamingStrategy", "simple-name")
}
```

```kt
@CopyTo(UiState.Success::class)
data object Loading

// auto generate — 遷移先の simpleName（Success）だけを使う
fun Loading.copyToSuccess(/* ... */): UiState.Success = /* ... */
```

### `cream.escapeDot`

| デフォルト          | 設定可能な値                                          |
|--------------------|-------------------------------------------------------|
| `lower-camel-case` | `lower-camel-case`, `replace-to-underscore` のいずれか。 |

Kotlin の関数名には通常 `.` を含めることはできないため、設定例に示すいずれかの方法で命名可能な文字列に変更する必要があります。

`cream.copyFunNamingStrategy` で取得したクラス名をエスケープする方法を設定します。
エスケープでクラス名の大文字小文字は変わりません — `lower-camel-case` は各セグメントを camelCase で
連結して全体の先頭 1 文字だけを小文字化し、`replace-to-underscore` は `.` を `_` に置き換えます
（どちらもプレフィックスとの結合時に先頭 1 文字が大文字化されます）。

```kts
// module/build.gradle.kts
ksp {
    arg("cream.escapeDot", "replace-to-underscore")
}
```

```kt
@CopyTo(UiState.Success::class)
data object Loading

// auto generate — under-package の結果 UiState.Success の `.` が `_` に置き換わる
fun Loading.copyTo_UiState_Success(/* ... */): UiState.Success = /* ... */
```

## 関連ドキュメント

- [KSP options](./options.ja.md) — KSP 引数の索引
- [KDoc](./kdoc.ja.md) — 生成関数への `kdoc = KDoc(...)` 引数
- [Visibility](./visibility.ja.md) — `visibility` 引数と `cream.defaultVisibility`
- [Copy — @CopyTo / @CopyFrom / @CopyMapping](../copy.ja.md)
