[cream.kt](../README.md) / [cream-ksp tests](README.md)

# Snapshot golden file

`assertMatchesSnapshot` が比較する golden Markdown（`snapshots/<TestName>/<testCase>.md`）

更新: `./gradlew :cream-ksp:test -Dcream.snapshot.update=true`（`-D` フラグは `cream-ksp/build.gradle.kts` の
`tasks.named<Test>("test")` 設定で systemProperty として転送される）。

#### Format

golden ファイルは `*.md`。すべての captured 値は **facet** として宣言する（「最初が main」のような特別扱いはない）。
各 facet は宣言順に `## <facet 名>` セクションとして書き出される:

```kt
internal class MyTest : FreeSpec({
    "scenario" {
        val source = """
            @CopyTo(Target::class)
            data class Source(...)
        """.trimIndent()
        val result = compileWithCream(source)

        assertMatchesSnapshot("MyTest.scenario") {
            "Generated" facetOf result.generatedSourceText()            // default lang = "kt"
            "Input" facetOf source                                      // default lang = "kt"
            facet("Compiler messages", result.messages, lang = "text")  // 明示で別言語
        }
    }
})
```

infix `facetOf` は default `lang = "kt"`。違う言語が必要な facet は `facet(name, content, lang)` を使う。block 内で最低 1 つの
facet が必要。

#### File hierarchy

golden は `snapshots/<TestName>/<testCase>.md` に test クラスごとのディレクトリで格納する。パスは `assertMatchesSnapshot(name)` の
`name` から導出され、**最初の `.` が test クラスと test case の境界**としてディレクトリ区切りになる（`SnapshotAssertion.kt` の
`snapshotRelativePath`）:

- `"BasicSnapshotTest.copyTo"` → `BasicSnapshotTest/copyTo.md`
- `"CopyToDiagnosticTest.enumTarget.output"` → `CopyToDiagnosticTest/enumTarget.output.md`

最初の `.` だけが置換されるので、test case 側の `.output` / `.default` などのドット付き接尾辞・variant はファイル名の一部として
そのまま残る。edge case を扱う test case は `name` に `edgeCase` セグメントを `/` で含める
（`"FooTest.edgeCase/scenario"` → `FooTest/edgeCase/scenario.md` を推奨）。

#### SSoT を保つ

test code と snapshot で input を二重管理しないために、input source は test code 側で `val source = ...trimIndent()` として
抽出し、`compileWithCream(source)` と `"Input" facetOf source` の両方に同じ値を渡す。

#### Facet 名の規約

- snapshot 系（生成された Kotlin source を見る）: `"Generated"` + `"Input"`
- diagnostic 系（コンパイラ出力を見る）: `facet("Compiler output", ..., lang = "text")` + `"Input"`

順序の慣習は「主要な観測値が先、コンテキストが後」。

#### Backtick collision

snapshot 本文に backtick run（例: cream が出す KDoc の ```` ```kt ... ``` ```` 例）が含まれる場合、各セクションのフェンスは独立に
「内部の最長 backtick run + 1」の長さで拡張される（最小 3）。比較は file 全体（見出し + フェンス含む）で行うので、フェンスを
直接書き換えると壊れる。

## Placement

| Path | When to use |
|---|---|
| `cream-ksp/src/test/resources/snapshots/*Test/**/*.md` |  |

## Examples

- `CopyToSnapshotTest/UseCase/<topic>.md`
