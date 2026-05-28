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

golden ファイルは `*.md` (Markdown)。最小形は単一の fenced code block:

````md
```kt
// snapshot 対象のテキスト
```
````

`assertMatchesSnapshot(name, actual, lang)` の `lang` でフェンス言語を指定する
(default `kt`)。コンパイラ出力など非 Kotlin テキストは `lang = "text"` を渡す。

#### Facets (multi-section)

`assertMatchesSnapshot` に block を渡すと、main 部分が `## [mainTitle]` 配下に置かれ、
block 内で宣言した facet が `## <facet 名>` セクションとして追加される。
SSoT (test code と snapshot で input を二重管理しない) を保つため、input source は test
code 側で local val として抽出し、`compileWithCream(...)` と facet の両方に渡す:

```kt
val source = """
    @CopyTo(Target::class)
    data class Source(...)
""".trimIndent()
val result = compileWithCream(source)

assertMatchesSnapshot("name", result.generatedSourceText()) {
    "Input" facetOf source                              // default lang = "kt"
    facet("KSP options", optionsText, lang = "properties")
}
```

infix `facetOf` は default `lang = "kt"`。違う言語を使う facet は `facet(name, content, lang)`
を使う。facet は宣言順に書き出される。

diagnostic 系では main が compiler output なので `mainTitle = "Compiler output"` を渡す。
それ以外 (snapshot 系) は default の `"Generated"` のまま。

#### Backtick collision

snapshot 本文に backtick run (例: cream が出す KDoc の ` ```kt ... ``` ` 例) が含まれる
場合、各セクションのフェンスは独立に「内部の最長 backtick run + 1」の長さで拡張される
(最小 3)。比較は fenced block 全体 (フェンス含む) で行うので、フェンスを直接書き換えると壊れる。

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
