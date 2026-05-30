---
paths:
  - cream-ksp/src/test/**
  - cream-ksp/src/test/resources/snapshots/**
---

# KSP Compilation Tests (kctfork)

`cream-ksp/src/test/kotlin/me/tbsten/cream/ksp/` には [kctfork](https://github.com/zacsweers/kotlin-compile-testing)
(`dev.zacsweers.kctfork:core` / `:ksp`) を使った JVM 専用の end-to-end テストを置いている。
マルチプラットフォームの `test/` モジュールでは表現しにくいシナリオを補完する。

## レイアウト

- `testing/` — テスト基盤
  - `CreamCompilation.kt` — `compileWithCream(...)` ヘルパー。`KotlinCompilation` + `useKsp2()` を組み合わせ
    cream のプロセッサを inline ソース文字列に対して走らせる
  - `CreamCompilationResult.kt` — 結果ラッパー (`exitCode`, `messages`, `compilerOutput`, ジェネレートされた
    ソースへのアクセサ)
  - `CreamCompilationResultUtils.kt` — `generatedSourceText()` などのユーティリティ
  - `SnapshotAssertion.kt` — golden 比較ヘルパー
- `diagnostic/` — 不正な annotation 使用や不正な KSP option 値に対して、`InvalidCreamUsageException` /
  `InvalidCreamOptionException` がコンパイル失敗としてメッセージに乗ることを assert する。
  exit code を `assertNotEquals(OK, ...)` で確認したうえで、`normalizedCompilerOutput()` を
  `*.output.md` の golden と突き合わせて error message 全文を固定化する
- `options/` — `cream.copyFunNamePrefix` / `cream.copyFunNamingStrategy` / `cream.escapeDot`
  / `cream.notCopyToObject` のあらゆる値を end-to-end で走らせ、生成された関数名を検証
- `snapshot/` — 代表的な成功シナリオ (basic `@CopyTo`, sealed `@CopyToChildren`, generic `@CopyFrom`,
  object-target `@CombineTo`) について、生成 Kotlin ソース全文を golden ファイルと比較。
  Golden は `cream-ksp/src/test/resources/snapshots/` に格納

### Snapshot file format

golden ファイルは `*.md` (Markdown)。すべての captured 値は **facet** として宣言する
(「最初が main」のような特別扱いはない)。各 facet は宣言順に `## <facet 名>` セクション
として書き出される:

```kt
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
```

infix `facetOf` は default `lang = "kt"`。違う言語が必要な facet は
`facet(name, content, lang)` を使う。block 内で最低 1 つの facet が必要。

#### ファイル階層

golden は `snapshots/<TestName>/<testCase>.md` に test クラスごとのディレクトリで格納する。
パスは `assertMatchesSnapshot(name)` の `name` から導出され、**最初の `.` が
test クラスと test case の境界**としてディレクトリ区切りになる (`SnapshotAssertion.kt` の
`snapshotRelativePath`):

- `"BasicSnapshotTest.copyTo"` → `BasicSnapshotTest/copyTo.md`
- `"CopyToDiagnosticTest.enumTarget.output"` → `CopyToDiagnosticTest/enumTarget.output.md`

最初の `.` だけが置換されるので、test case 側の `.output` / `.default` などの
ドット付き接尾辞・variant はファイル名の一部としてそのまま残る。

edge case を扱う test case は `name` に `edgeCase` セグメントを `/` で含める。両形を取れる:

- `"FooTest.edgeCase/scenario"` → `FooTest/edgeCase/scenario.md` (test クラス配下にまとめる / 推奨)
- `"edgeCase/FooTest.scenario"` → `edgeCase/FooTest/scenario.md`

#### SSoT を保つ

test code と snapshot で input を二重管理しないために、input source は test code 側で
`val source = """...""".trimIndent()` として抽出し、`compileWithCream(source)` と
`"Input" facetOf source` の両方に同じ値を渡す。

#### Facet 名の規約

呼び出し側が自由に付けられる。主用途での慣習:

- snapshot 系 (生成された Kotlin source を見る): `"Generated"` + `"Input"`
- diagnostic 系 (コンパイラ出力を見る): `facet("Compiler output", ..., lang = "text")` + `"Input"`

順序は test 著者が決める。慣習は「主要な観測値が先、コンテキストが後」 (= `Generated` →
`Input` / `Compiler output` → `Input`)。

#### Backtick collision

snapshot 本文に backtick run (例: cream が出す KDoc の ` ```kt ... ``` ` 例) が含まれる
場合、各セクションのフェンスは独立に「内部の最長 backtick run + 1」の長さで拡張される
(最小 3)。比較は file 全体 (見出し + フェンス含む) で行うので、フェンスを直接書き換えると
壊れる。

### `normalizedCompilerOutput()`

以下を置換/集約してから返す:

- `java.io.tmpdir` → `<TMPDIR>`
- `Kotlin-CompilationNNN` → `Kotlin-Compilation<N>`
- 連続する `\tat ...` および `\t... NN more` (stack-trace フレーム) → 1 行の `\t<stack trace omitted>`

stack frame は Gradle / JUnit / KSP / cream 自身の line 変更や `... NN more` の depth count
で簡単にずれるため、diagnostic snapshot で固定化しているのは「error message 本文」だけ。
特定 frame の存在を assert したいなら、snapshot とは別に `messages.contains(...)` を併用する。

## 運用

### スナップショット更新

```bash
./gradlew :cream-ksp:test -Dcream.snapshot.update=true
```

`-D` フラグは `cream-ksp/build.gradle.kts` の `tasks.named<Test>("test")` 設定で systemProperty として
転送される。

### 新しいテストを足すとき

- 既存パターンに合うなら該当ディレクトリ (`diagnostic/` / `options/` / `snapshot/`) に追加
- 新しいカテゴリが必要なら同じレベルにディレクトリを切る
- 共有ヘルパーが必要になったら `testing/` に追加し、本ドキュメントの「レイアウト」 を更新
