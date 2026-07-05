[← README](../../README.ja.md) | [English](./visibility.md)

# 可視性 (Visibility)

デフォルトでは、生成される copy 関数は生成元となる target (または sealed) 宣言の可視性を引き継ぎます。
特定の可視性を強制したい場合は、copy を生成する各アノテーションに
`visibility = CopyVisibility.<...>` を渡すか、
[`cream.defaultVisibility`](#モジュール全体のデフォルト-creamdefaultvisibility) KSP オプションで
モジュール全体のデフォルトを設定します。リバーシブル (`canReverse`) な `@CopyMapping` では、
forward / reverse 両方の関数に同じ可視性が適用されます。

## 基本の例

```kt
import me.tbsten.cream.CopyTo
import me.tbsten.cream.CopyVisibility

@CopyTo(MergedState::class, visibility = CopyVisibility.INTERNAL)
data class ServerState(val shared: String)

// 自動生成
internal fun ServerState.copyToMergedState(
    shared: String = this.shared,
    /* ... */
): MergedState = ...
```

## `CopyVisibility` の値

`CopyVisibility` には以下の値があります。生成される copy 関数はトップレベルの拡張関数なので、
使用可能なままになる修飾子だけを提供しています。`private` (生成されたファイル内でしか見えない) や
`protected` (トップレベル宣言には付けられない) は生成された関数を使えなくしてしまうため、
意図的に提供していません。

| 値 | 生成される修飾子                  |
|----|---------------------------|
| `INHERIT` (デフォルト) | target/sealed 宣言の可視性を引き継ぐ |
| `PUBLIC` | `public`                  |
| `INTERNAL` | `internal`                |

`visibility` を省略した場合は完全に後方互換であり、これまで生成されていたコードは変わりません。

## モジュール全体のデフォルト: `cream.defaultVisibility`

個々の宣言にアノテーションを付ける代わりに、モジュール全体のデフォルトを設定したい場合は
`cream.defaultVisibility` KSP オプションを使います。

生成されるすべての copy / combine 関数に対する モジュール全体のデフォルト可視性を設定するもので、アノテーションごとの `visibility = CopyVisibility.<...>` 引数のプロジェクトレベル版です。

| デフォルト | 設定可能な値                                |
|-----------|---------------------------------------------|
| `INHERIT` | `INHERIT`, `PUBLIC`, `INTERNAL` のいずれか。 |

```kts
// module/build.gradle.kts

ksp {
    arg("cream.defaultVisibility", "INTERNAL")
}
```

例えば `cream.defaultVisibility=INTERNAL` を設定すると、素の `@CopyTo(Target::class)` でも各アノテーションに
`visibility = CopyVisibility.INTERNAL` を付けずに `internal` な copy 関数が生成されます。

## 優先順位

生成される関数の可視性は以下の順序で決まります:

1. アノテーションで明示した `visibility`（`INHERIT` 以外）が常に優先されます。
2. 次に `cream.defaultVisibility`（`PUBLIC` または `INTERNAL` の場合）。
3. どちらも `INHERIT` の場合は、このオプション追加前と同じく、生成関数は target/sealed 宣言自身の
   可視性を引き継ぎます。

## 関連ドキュメント

- [KSP Options](./options.ja.md) — モジュール全体の KSP オプションの索引
- [Function name (funName)](./fun-name.ja.md) — 生成される関数名をカスタマイズする
- [KDoc customization](./kdoc.ja.md) — 生成される関数の KDoc に説明や例を追加する
