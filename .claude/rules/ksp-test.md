---
paths:
  - cream-ksp/src/test/**
  - cream-ksp/src/test/resources/snapshots/**
---

# KSP Compilation Tests (kctfork)

`cream-ksp/src/test/kotlin/me/tbsten/cream/ksp/` には [kctfork](https://github.com/zacsweers/kotlin-compile-testing)
(`dev.zacsweers.kctfork:core` / `:ksp`) を使った JVM 専用の end-to-end テストを置いている。
マルチプラットフォームの `test/` モジュールでは表現しにくいシナリオを補完する。

テストは [kotest](https://kotest.io) の `FunSpec` スタイルで書く
(`internal class XxxTest : FunSpec({ test("...") { ... } })`)。assert は kotest matcher を使い、
**語順は `actual shouldBe expected`** (kotlin.test の `assertEquals(expected, actual)` とは逆)。
失敗時メッセージは `withClue(message) { ... }` で保持する。cream-ksp は JVM のみなので
`kotest-runner-junit5` + `tasks.test { useJUnitPlatform() }` で動く (KSP も io.kotest プラグインも不要)。

## レイアウト（issue #127: feature 単位に分割）

機能 (feature) ごとにディレクトリを分け、各 feature は同じ **5 種類** のテストを持つ。
**現状 (#127 第 1 段階)** はテスト基盤 (`testing/`) / smoke / Konsist のみ実装済みで、
各 feature の個別テストと `core/common` / `MultipleDiagnosticsTest` は `xtest` の空スタブ
（`// TODO(#127): reimplement ...`）。これらは順次実装し直す。
generated case × snapshot（`testing/generator/` と `<Feat>SnapshotTest`）は **書き方を再検討中のため
コード未配置**。下記の generator 関連はあくまで予定の設計で、再設計後に実装する。

```
cream-ksp/src/test/kotlin/me/tbsten/cream/ksp/
├── AllKotlinFilesTest.kt       # 全ファイル横断 Konsist（root 直下許可ファイル / 1 ファイル行数上限）
├── MultipleDiagnosticsTest.kt  # フィーチャー横断（複数アノテ併用 / 複数エラー同時） ※stub
├── feature/
│   ├── ArchTest.kt             # feature 層レイヤリング Konsist
│   └── <copyTo|copyFrom|copyToChildren|sealedCopy|combineTo|combineFrom|copyMapping|combineMapping>/
│       ├── <Feat>BasicUsageTest.kt    # 正常系 (example-based)
│       ├── <Feat>InvalidUsageTest.kt  # 不正利用 → エラー (diagnostic)
│       ├── <Feat>EdgeUsageTest.kt     # レアケース ＋ @Map/@Exclude 等の意味的ケース
│       ├── <Feat>PropertyTest.kt      # PBT（可視性/エスケープ/オプションは generator 次元で網羅）
│       └── <Feat>SnapshotTest.kt      # generator 駆動スナップショット
├── core/
│   ├── ArchTest.kt             # core / util 層レイヤリング Konsist
│   └── common/CommonLogicTest.kt      # KSP 型に依存しない純ロジック ※stub
└── testing/                    # テスト基盤
    ├── CreamCompilation.kt            # compileWithCream(...)（KotlinCompilation + useKsp2）
    ├── CreamCompilationResult.kt      # 結果ラッパー (exitCode / messages / compilerOutput / 生成ソース)
    ├── CreamCompilationResultUtils.kt # generatedSourceText() / normalizedCompilerOutput() など
    ├── SnapshotAssertion.kt           # golden 比較 (assertMatchesSnapshot / facet)
    ├── generator/                     # ※予定（書き方再検討中・未配置）: generated case × snapshot 基盤
    ├── smoke/                         # 基盤が動くことの最小確認
    │   └── CreamCompilationSmokeTest.kt
    └── konsist/KonsistSupport.kt      # Konsist scope / レイヤ判定ヘルパ（3 つの ArchTest が共有）
```

- **feature 5 種**: 各 feature は上記 5 ファイルを持つ（正常系 / 不正系 / エッジ / PBT / snapshot）。
- **generator × snapshot（予定 / 未実装）**: `<Feat>SnapshotTest` は generated case を
  `compileWithCream` → `assertMatchesSnapshot` で golden 比較する想定。case 生成の仕組み
  (`testing/generator/`) は書き方を再検討中のため一旦コードを置いていない。再設計してから
  `<Feat>SnapshotTest` を実装する（それまでは `xtest` スタブ）。snapshot は決定的に保つこと。
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
internal class MyTest : FunSpec({
    test("scenario") {
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

> #127 第 1 段階で再構成済み: 基盤 (`testing/`)・generator・smoke・Konsist (3 つの ArchTest) は実装済みで
> green。各 feature の 5 種テストと `core/common` は `xtest` スタブなので、ここから順次実装し直す。
