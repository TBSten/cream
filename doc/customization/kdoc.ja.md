[← README](../../README.ja.md) | [English](./kdoc.md)

# KDoc のカスタマイズ

各ソースアノテーションには `kdoc = KDoc(...)`
パラメータを指定でき、生成される関数の KDoc に独自の説明や例を追加できます。

## 基本の例

```kt
import me.tbsten.cream.CopyTo
import me.tbsten.cream.KDoc

@CopyTo(
    Target::class,
    kdoc = KDoc(
        description = "この関数は ~ の場合は使わないでください。",
        examples = [
            """
            # 推奨

            ```kt
            val target = source.copyToTarget()
            ```
            """,
        ],
    ),
)
data class Source(val shared: String)
```

<details>
<summary>生成されるコード</summary>

````kt
/**
 * (Auto generate by @[CopyTo] annotation of [Source])
 *
 * Source -> Target copy function.
 *
 * この関数は ~ の場合は使わないでください。
 *
 * # Example: Basic
 *
 * ```kt
 * val source = Source(...)
 * val target = source.copyToTarget()
 * ```
 *
 * # Example: Override property values
 *
 * ```kt
 * val source = Source(...)
 * val target = source.copyToTarget(property = value)
 * ```
 *
 * # 推奨
 *
 * ```kt
 * val target = source.copyToTarget()
 * ```
 *
 * @see Source
 * @see Target
 */
fun Source.copyToTarget(
    shared: String = this.shared,
): Target = Target(
    shared = shared,
)
````

</details>

## 生成される KDoc のセクション順

生成される KDoc は以下の順序でレンダリングされます:

1. 自動生成ヘッダ (`(Auto generate by @[...] of [...])`)
2. 自動生成の説明行 (`Source -> Target copy function.`)
3. `KDoc.description` (指定した場合のみ)
4. 自動生成の `# Example: Basic` / `# Example: Override property values`
5. `KDoc.examples` (各要素を `trimIndent` した上でそのまま挿入)
6. `@see` 参照

## 詳細

`examples` の各要素はそのまま挿入されるため、`# 見出し` や
` ```kt ... ``` ` のフェンスは要素内で自由に記述してください。

## 関連ドキュメント

- [Visibility](./visibility.ja.md) — 生成される関数の可視性修飾子を制御する
- [Function name (funName)](./fun-name.ja.md) — 宣言ごとに生成される関数名を上書きする
- [KSP Options](./options.ja.md) — モジュール全体の KSP オプションの索引
