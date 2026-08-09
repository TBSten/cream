# Design: `@CallFrom` — 関数をコピー先にする (issue #10)

> Status: 実装済み仕様のリファレンス。issue #10 の論点への回答と、Kotlin 言語機能ごとの
> 対応方針（decision table）を含む。

## 概要

データクラス（引数ホルダー）から **関数** を呼び出すブリッジオーバーロードを生成する。
「constructor も関数である」という視点で、cream のコピー生成（source プロパティ → callable パラメータの名前マッチング）を関数に拡張する。

```kotlin
data class ProcessDataArgs(val data1: String, val data2: Int)

@CallFrom(ProcessDataArgs::class)
fun processData(data1: String, data2: Int) { ... }

// generated
public fun processData(
    processDataArgs: ProcessDataArgs,
    data1: String = processDataArgs.data1,
    data2: Int = processDataArgs.data2,
): Unit = processData(
    data1 = data1,
    data2 = data2,
)
```

> issue の owner コメント例は生成 body で `action.data1` を渡しているが、それでは override 引数が無視される。
> 本設計では **パラメータ（`data1`）を渡す** 形に修正する。

## Issue の論点への回答

### 方式: `@CopyTo.Fun(funName = "...")`（issue 本文）vs 関数側アノテーション（owner コメント）

**owner コメントの関数側アノテーション方式を採用。**

- `@CopyTo.Fun(funName = "createMyTarget")` は文字列参照のため type-safe でない（typo・rename に追従不能）。
- 関数側に `KClass` 参照で書く方式は完全に type-safe。
- 本文方式は Rejected alternatives として記録（KClass で関数を参照できない Kotlin の制約上、type-safe 化の目処が立つまで見送り）。

### アノテーション名

- owner 自身が「`@CopyFrom` 流用は名前が良くない、rename すべき」とコメント済み。
- **`@CallFrom` を採用。** 根拠:
  - `CopyFrom` / `CombineFrom` と同じ「〜From(source)」文法で cream の命名体系に整合。
  - 生成物は「コピー関数」ではなく「関数呼び出しブリッジ」なので Copy ではなく Call。
- 代替案（PR 説明に列挙）: `@CopyFrom` の FUNCTION target 拡張 / `@InvokeFrom` / `@ApplyFrom`。

### 生成形: オーバーロード vs 拡張関数

- **オーバーロード形（owner コメント準拠）を採用**: `processData(args)` — 呼び出しが元関数と同じ見た目になり、args ホルダー型の API を汚さない。
- 拡張関数形（`args.processData()`）は「コピー」ではなく「呼び出し」なので cream のコピー関数と意味が衝突する。不採用（Rejected alternatives）。

## Kotlin 言語機能 decision table

各 Kotlin の形に対して (a) 生成コードが正しい（snapshot の `ExitCode OK` + `test/` の E2E で証明）
/ (b) 位置付き診断エラー / (c) 文書化された制限 のいずれかに落とす。

| # | 言語機能 | 判定 | 根拠 / 生成形 |
|---|---|---|---|
| 1 | `suspend` | (a) 対応 | ブリッジも `suspend`。E2E: `BasicTest` / `ExtensionTest` |
| 2 | 拡張関数 (top-level) | (a) 対応 | 同じレシーバの拡張関数として生成（generic / nullable receiver 含む）。member 拡張（二重レシーバ）は (b) 診断 |
| 3 | member 関数 | (a) 対応 | enclosing 型（class / object / companion / interface / 非 generic の inner・nested）の拡張関数。local スコープは (b) 診断 |
| 4 | generics | (a) 対応 | 型パラメータ・境界・`where` 句を転写。source クラスの型パラメータとマージ。`reified` は (b) 診断（ブリッジは inline でない）。generic クラスの member は (b) 診断（receiver への型引数転写が未設計） |
| 5 | 元関数のデフォルト引数 | (a) 仕様として確定 | マッチしない + デフォルトあり → ブリッジから **省略**（元のデフォルトが適用）。KSP はデフォルト式を読めず、必須化は関数作者の意図に反する。マッチするパラメータは自動コピーが優先。`@Exclude` は必須化が優先 |
| 6 | `vararg` | (a) 対応 | 対応する配列型プロパティにマッチ。`name = name` 転送（spread 不要は検証済み） |
| 7 | `operator` / `infix` / `inline` / `tailrec` | (a) 対応 | 修飾子は転写しない（ブリッジは規約を満たせない / inline・tailrec は元関数内で有効）。snapshot family `13--modifiers` + E2E `ModifiersTest` |
| 8 | 可視性 | (a)+(b) | INHERIT は min(関数, enclosing 連鎖, source 連鎖) に clamp。private/protected/local な source・enclosing は (b) 診断。internal 制約下の明示 PUBLIC も (b) 診断 |
| 9 | 戻り型 | (a) 対応 | `Unit` / `Nothing` / nullable / generic すべて転写。family `14--returnShape` |
| 10 | typealias | (a) 対応 | 双方向にマッチ（K2 の assignability が alias を解決）。シグネチャには alias 名を保持。family `15--typealiasShape` |
| 11 | expect/actual | (b)+(a) | `expect` は診断（KSP はプラットフォームごとに処理し actual と付き合わせられない）。`actual` / common の通常関数は通常どおり (a) |
| 12 | context parameters (Kotlin 2.2) | (c) 文書化された制限 | KSP 2.2.20-2.0.4 に表現 API が無く `parameters` にも JVM signature にも現れない（検証済み）。プロセッサはクラッシュせず、生成ファイル側の `No context argument` エラーになる。docs の Known limitations に失敗モードを明記 |
| 13 | `@Deprecated` | (a)+(b) | WARNING → 関数・enclosing・source・参照プロパティから伝播（未参照プロパティからは伝播しない）。ERROR / HIDDEN は (b) 診断 — K2 では deprecated 宣言内でも fatal レベルの参照は抑制されないことを検証済み |
| 14 | args クラスの形 | (a) 対応 | nested / value class / object / enum / sealed interface / private ctor（ブリッジは construct しない）。アクセス不能プロパティ（private / protected / 他モジュール internal / fatal deprecated）はマッチ対象から除外（パラメータは必須のまま） |
| 15 | オーバーロード衝突 | (b) 診断 | 同名関数群は 1 ファイルに集約（`FileAlreadyExistsException` 解消）。ブリッジ同士・既存 top-level 関数とのシグネチャ一致（型パラメータは位置正規化で比較）を検出して診断 |
| 16 | キーワード識別子 | (a) 対応 | funName / パラメータ名 / プロパティ参照 / source パラメータ名 / 修飾名セグメント（`escapeDottedKotlinIdentifier`）を escape。root package（空 package）も修正済み |

## 詳細仕様

### `@CallFrom`（cream-runtime）

```kotlin
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
public annotation class CallFrom(
    vararg val sources: KClass<*>,
    val kdoc: KDoc = KDoc(),
    val visibility: CopyVisibility = CopyVisibility.INHERIT,
) {
    @Target(AnnotationTarget.VALUE_PARAMETER)
    public annotation class Map(vararg val propertyNames: String)

    @Target(AnnotationTarget.VALUE_PARAMETER)
    public annotation class Exclude
}
```

- `sources` が複数なら source ごとに 1 オーバーロード生成（すべて元関数と同名・第 1 引数型で区別される）。空はエラー。
- `funName` パラメータは持たない（生成関数は常に元関数と同名のオーバーロード）。既存の funName template 機構は「コピー先クラス名」前提のため、関数向け設計が固まるまで見送り（Future work）。
- `Map` / `Exclude` は `CopyFrom.Map` / `CopyFrom.Exclude` と同じ意味論（名前マッチの上書き / auto-copy default の除去）。

### パラメータマッチングとブリッジの形

- 既存のコピー生成と同一: source クラスのプロパティと関数パラメータを名前（+ `@CallFrom.Map`）でマッチ（`findMatchedProperty` を再利用）。
- ただし **生成コードから読めないプロパティはマッチ対象外**（private / protected / 他モジュール internal / ERROR・HIDDEN deprecated）。
- マッチしたパラメータ → `= <sourceParam>.<property>` デフォルト付き（元関数のデフォルトより優先）。
- マッチしない + 元関数デフォルトなし → デフォルトなしで転写（呼び出し側必須）。
- マッチしない + 元関数デフォルトあり → **ブリッジから省略**（元のデフォルトが適用）。
- `@CallFrom.Exclude`（マッチしたパラメータ）→ 必須化（元関数デフォルトの有無に関わらず）。
- 第 1 パラメータ（source オブジェクト）の名前 = source クラス simpleName の lowerCamelCase。既存パラメータ名と衝突する場合はエラー診断。
- この分類は `core/callFrom/CallFromBridgeShape.kt` の `callFromBridgeParameters` が SSoT
  （generator / KDoc / 衝突検出が共有）。

### 可視性

- 実効可視性 = 明示（annotation）→ `cream.defaultVisibility` → INHERIT の順に解決。
- INHERIT = min(関数, enclosing 連鎖, source 連鎖)。internal 制約があれば `internal` に clamp。
- internal 制約下の明示 PUBLIC、private / protected / local な参照、他モジュール internal の source はすべて診断エラー。

### 検証（`logger.error` + skip。throw しない）

- 関数種別: private / protected / local（local class・anonymous object の member 含む）/ abstract / expect / member 拡張 / reified / generic クラスの member / ERROR・HIDDEN deprecated。
- annotation の形: sources 空・重複、source パラメータ名の衝突、ERROR・HIDDEN deprecated な source / enclosing クラス参照。
- 可視性違反（上記）。
- シグネチャ衝突: ブリッジ同士（同 scope で型リスト一致。型パラメータは位置正規化）/ 既存 top-level 関数（`Resolver.getFunctionDeclarationsByName`）。衝突した unit はファイルごと skip（部分生成なし）。

### ファイル生成

- 生成ファイル名: `CallFrom__<function underPackageName の '.' を '_' に置換>`（member fun は `Class_fn` 形）。パッケージ = 元関数のパッケージ。
- **同名オーバーロードは 1 ファイルに集約**（unit を (package, fileName) で groupBy）。従来はファイル名衝突で `FileAlreadyExistsException` クラッシュしていた。
- `Dependencies(aggregating = true, <群の全 containingFile>)`。

### アーキテクチャ配置

| 層 | 追加物 |
|---|---|
| cream-runtime | `CallFrom.kt`（+ nested `Map` / `Exclude`） |
| core/common | `GenerateSourceAnnotation.CallFrom` / `ExcludeProperty` の分岐 / `deprecatedAnnotationLineOfDeclarations` / raw 読みの `deprecatedAnnotation()`（KSP2 metadata の typed proxy 制約対応） |
| core/callFrom | `CallFromFunction.kt`（生成）+ `CallFromBridgeShape.kt`（パラメータ分類・シグネチャ計算） |
| feature/callFrom | `ProcessCallFrom.kt`（orchestration）+ `CallFromValidation.kt` / `CallFromSourcesValidation.kt` / `CallFromVisibility.kt` / `CallFromCollision.kt` |
| root | `CreamSymbolProcessor.process()` に dispatch 1 行 |
| util | `escapeDottedKotlinIdentifier` |

### テスト

- **SnapshotTest**（generator 駆動）: family 00–16（sourceKind / functionKind / nesting / generics / signatureShape / propertyShape / matching / multiSource / map / exclude / kdoc / visibility / extension / modifiers / returnShape / typealiasShape / deprecated）。全 golden `ExitCode OK`（= 生成コードのコンパイル証明）。
- **InvalidUsageTest**（diagnostic golden）: 上記「検証」の各ケース。
- **統合（test/ モジュール）**: デフォルト適用 / override / suspend / member / companion / 拡張関数 / modifiers / defaults fallback / typealias / Nothing・nullable 戻り値 / deprecated 伝播 / vararg。

### Future work（スコープ外）

- member 拡張関数対応（context parameters が安定したら二重 receiver を表現できる可能性）
- `funName` カスタマイズ（関数向け naming template 設計後）
- class 側から関数を指す `@CopyTo.Fun` 相当（type-safe に表現できる案が出るまで見送り）
- 元関数のデフォルト引数の「転写」（KSP の制約で現状不可。現仕様は省略によるフォールバック）
- context parameters の検出・対応（KSP API 待ち）
