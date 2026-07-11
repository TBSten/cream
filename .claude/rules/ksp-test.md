---
paths:
  - cream-ksp/src/test/**
  - cream-ksp/src/test/resources/snapshots/**
---

# KSP Compilation Tests (kctfork)

`cream-ksp/src/test/kotlin/me/tbsten/cream/ksp/` には [kctfork](https://github.com/zacsweers/kotlin-compile-testing)
(`dev.zacsweers.kctfork:core` / `:ksp`) を使った JVM 専用の end-to-end テストを置いている。
マルチプラットフォームの `test/` モジュールでは表現しにくいシナリオを補完する。

テストは [kotest](https://kotest.io) の `FreeSpec` スタイルで書く
(`internal class XxxTest : FreeSpec({ "..." { ... } })`、ネストは `"group" - { "..." { ... } }`)。
無効化テストは `"...".config(enabled = false) { }`。assert は kotest matcher を使い、
**語順は `actual shouldBe expected`** (kotlin.test の `assertEquals(expected, actual)` とは逆)。
失敗時メッセージは `withClue(message) { ... }` で保持する。cream-ksp は JVM のみなので
`kotest-runner-junit5` + `tasks.test { useJUnitPlatform() }` で動く (KSP も io.kotest プラグインも不要)。

## レイアウト（issue #127: feature 単位に分割）

機能 (feature) ごとにディレクトリを分け、各 feature は同じ **5 種類** のテストを持つ。
**現状 (#127)**: テスト基盤 (`testing/`、generator / poet 含む) / Konsist / `MultipleDiagnosticsTest` /
全 8 feature の `<Feat>SnapshotTest`（`scenario/` 付き）は実装済み。`<Feat>BasicUsageTest` /
`<Feat>InvalidUsageTest` / `<Feat>EdgeUsageTest` / `<Feat>PropertyTest` は一部を除きまだ `xtest` の
空スタブ（`// TODO(#127): reimplement ...`）で、順次実装し直す。

```
cream-ksp/src/test/kotlin/me/tbsten/cream/ksp/
├── AllKotlinFilesTest.kt       # 全ファイル横断 Konsist（root 直下許可ファイル / 1 ファイル行数上限）
├── MultipleDiagnosticsTest.kt  # フィーチャー横断（複数アノテ併用 / 複数エラー同時）
├── OptionsDiagnosticTest.kt / DefaultVisibilityOptionTest.kt / options/  # option 系
├── feature/
│   ├── ArchTest.kt             # feature 層レイヤリング Konsist
│   └── <copyTo|copyFrom|copyToChildren|sealedCopy|combineTo|combineFrom|copyMapping|combineMapping>/
│       ├── <Feat>BasicUsageTest.kt    # 正常系 (example-based) ※多くは stub
│       ├── <Feat>InvalidUsageTest.kt  # 不正利用 → エラー (diagnostic) ※多くは stub
│       ├── <Feat>EdgeUsageTest.kt     # レアケース ＋ @Map/@Exclude 等の意味的ケース ※多くは stub
│       ├── <Feat>PropertyTest.kt      # PBT（可視性/エスケープ/オプションは generator 次元で網羅） ※多くは stub
│       ├── <Feat>SnapshotTest.kt      # generator 駆動スナップショット（"UseCase" + "All patterns"）
│       └── scenario/                  # curated case（family ごとの *Scenarios() + UseCases.kt）
├── core/
│   ├── ArchTest.kt             # core / util 層レイヤリング Konsist
│   └── common/                 # KSP 型に依存しない純ロジック（CopyFunctionNameTest 等）
└── testing/                    # テスト基盤（feature 非依存）
    ├── compile/                # compileWithCream（KotlinCompilation + useKsp2）/ runCompileSnapshotTest / 結果ラッパー
    ├── snapshot/SnapshotAssertion.kt  # golden 比較 (assertMatchesSnapshot / facet)
    ├── poet/                   # 入力ビルダー（Prop / clazz / sealedInterface / SnapshotScenario …）
    ├── generator/              # generated case 基盤（Generator / union / cartesian / validCreamOptions）
    ├── kotlincodelikestring/   # CreamOptions → ksp config 文字列などの表示ヘルパ
    ├── smoke/                  # 基盤が動くことの最小確認
    └── konsist/KonsistSupport.kt      # Konsist scope / レイヤ判定ヘルパ（3 つの ArchTest が共有）
```

- **feature 5 種**: 各 feature は上記 5 ファイルを持つ（正常系 / 不正系 / エッジ / PBT / snapshot）。
- **generator × snapshot**: `<Feat>SnapshotTest` は `scenario/` の curated case（family ごとの
  `Generator.snapshotScenarios(...)`）を `union` でまとめ、`Generator.validCreamOptions()` と
  `cartesian` で掛け合わせて `runCompileSnapshotTest` で golden 比較する（全 8 feature 実装済み。
  横展開の手順は `.claude/skills/cream-snapshot-test`）。snapshot は決定的に保つこと。
- **UseCase snapshot cases**: `<Feat>SnapshotTest` には family × options の `"All patterns"` に加え、
  `doc/use-case/` の利用例を固定する `"UseCase" - { "<題材>" { ... } }` グループを置く
  （golden は `<Feat>SnapshotTest/UseCase/<題材>.md`。テスト名に `:` は使わない —
  golden ファイル名になるため Windows 非対応になる）。scenario は
  `feature/<name>/scenario/UseCases.kt` に doc のクラスを忠実に再現して置き（doc が定義しない型は
  最小 stub で補完）、options は基本 `CreamOptions.default`。doc がその利用例で明示的に option に
  言及している場合のみ `CreamOptions.default.copy(...)` のバリアントケースを追加する。
  doc パスとの対応・意図的な逸脱は各テストクラスの KDoc に記録する。
- **diagnostic 系**: 不正 annotation / 不正 option は exit code を
  `result.exitCode shouldNotBe KotlinCompilation.ExitCode.OK` で確認し、`normalizedCompilerOutput()`
  を `*.output.md` golden と突き合わせる（`<Feat>InvalidUsageTest` / `MultipleDiagnosticsTest`）。
- **Konsist (architecture, issue #130)**: kctfork ではなく [Konsist](https://github.com/LemonAppDev/konsist)
  (`testImplementation(libs.konsist)`) で `cream-ksp/src/main` の feature / core / util レイヤリングを
  import ベースで強制。3 ファイルに分割: `AllKotlinFilesTest`（モジュール横断）/ `feature/ArchTest` /
  `core/ArchTest`。共有 scope・ヘルパは `testing/konsist/KonsistSupport.kt`。scope は
  `Konsist.scopeFromProduction(moduleName = "cream-ksp", sourceSetName = "main")` で main のみ
  (test source set と nested `:cream-ksp:shared` を除外)。正本は
  `.claude/rules/ksp-architecture.md` の依存方向テーブル。Konsist 0.17.x は Kotlin 2.2 の
  `context(...)` パラメータ付き宣言も問題なくパースできることを確認済み。

### Snapshot file format

golden ファイルは `*.md` (Markdown)。すべての captured 値は **facet** として宣言する
(「最初が main」のような特別扱いはない)。各 facet は宣言順に `## <facet 名>` セクション
として書き出される:

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
特定 frame の存在を assert したいなら、snapshot とは別に `result.messages shouldContain "..."` を併用する。

## 運用

### スナップショット更新

```bash
./gradlew :cream-ksp:test -Dcream.snapshot.update=true
```

`-D` フラグは `cream-ksp/build.gradle.kts` の `tasks.named<Test>("test")` 設定で systemProperty として
転送される。

### 新しいテストを足すとき

- 対象の feature が決まっているなら `feature/<name>/` の該当 5 種ファイル
  (`BasicUsage` / `InvalidUsage` / `EdgeUsage` / `Property` / `Snapshot`) に追加する。
  まだ `xtest` スタブのファイルは中身を実装し直す形で置き換える。
- feature 横断のシナリオ（複数アノテ併用 / 複数エラー）は `MultipleDiagnosticsTest.kt` に置く。
- KSP 型に依存しない core の純ロジックは `core/<sub>/` 配下のテストに置く（コンパイル不要）。
- 共有ヘルパーが必要になったら `testing/`（基盤）/ `testing/generator/`（case 生成）/
  `testing/konsist/`（Konsist 共有）に追加し、本ドキュメントの「レイアウト」を更新する。

> #127 進捗: 基盤 (`testing/`)・generator・smoke・Konsist (3 つの ArchTest)・全 8 feature の
> `<Feat>SnapshotTest`・`MultipleDiagnosticsTest`・`core/common` は実装済みで green。残る
> `<Feat>BasicUsageTest` / `InvalidUsageTest` / `EdgeUsageTest` / `PropertyTest` は一部を除き
> `xtest` スタブなので、ここから順次実装し直す。
