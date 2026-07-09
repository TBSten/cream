# Design: `@ParentOptional` / `@ChildOptionals` (issue #135)

> Status: 実装済み (PR #180)。issue #135 の未解決論点への回答と、production 化レビュー
> (Kotlin 言語機能チェックリスト 12 項目) での決定事項を含む。

## 概要

sealed 階層の子クラスのプロパティを、sealed 親型に対する **nullable 拡張プロパティ** として生成する。

```kotlin
sealed interface MyState {
    data class Success(@ParentOptional val data: String) : MyState
    data object Loading : MyState
}

// generated
public val MyState.data: String?
    get() = when (this) {
        is MyState.Success -> data
        else -> null
    }
```

- `@ParentOptional`: 子クラスのプロパティ単位で opt-in。
- `@ChildOptionals`: sealed 親に付けると、全 transitive concrete leaf の対象プロパティへ一括適用。

## Issue の未解決論点への回答

### 1. そもそも必要か？（safe cast で書けるのでは）

**必要と判断し採用する。** 根拠:

- UI State（MVI / UiState）で `(state as? Success)?.data` の boilerplate が頻出する。
- 同名プロパティが **複数の子クラス** に存在する場合、手書きは `when` 全列挙になり冗長。本機能は複数子クラスのマージ（1 つの accessor に `is` 分岐を列挙）を一級でサポートし、safe cast 単発を超える価値を出す。
- cream の目的（類似クラス間の boilerplate 削減）と一貫する。
- 「fallback が null 以外」のケースはアノテーション引数で任意式を表現できないため v1 スコープ外（Future work）。

### 2. 生成プロパティ名

- **デフォルト: 元プロパティと同名**（issue 提案どおり）。呼び出しが最も自然。
- **per-property カスタマイズ**: `@ParentOptional(propertyName = "dataOrNull")`。
- `xxxOrNull` を一括適用するグローバル template オプションは v1 見送り（funName template 機構との整合を取ってから。Future work）。
- 親型に同名メンバが既に見える場合は生成対象外（extension は member に必ず負けるため）→ エラー診断。

### 3. アノテーション名

- v1 は issue の `@ParentOptional` / `@ChildOptionals` を採用（issue との対応が明確、Draft 段階で rename 容易）。
- 代替案（PR 説明で maintainer 判断を仰ぐ）:
  - property 側: `@LiftToParent`, `@ParentAccessor`
  - parent 側: `@ParentOptionals`（`@ParentOptional` と文法が揃う）

## 詳細仕様

### `@ParentOptional`（cream-runtime）

```kotlin
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.SOURCE)
public annotation class ParentOptional(
    val propertyName: String = "",   // 空 = 元プロパティと同名
    val kdoc: KDoc = KDoc(),
    val visibility: CopyVisibility = CopyVisibility.INHERIT,
)
```

- `AnnotationTarget.PROPERTY` のみなので、primary constructor の `val/var` に付けても property 側に適用される（issue のシンタックスがそのまま動く）。

検証（違反は `logger.error` + skip。throw しない）:

- enclosing class に sealed 上位型が 1 つも無い → エラー。
- private プロパティ → エラー（生成コードから参照不能）。
- 拡張プロパティ → エラー（extension receiver を用意できない。bare 参照は生成アクセサ自身に
  再帰解決されて実行時無限ループになるため、必ず前段で reject する）。
- 同じ生成名にマージされるプロパティ群の型が不一致 → エラー（Future: LUB。`T` vs `T?`、
  typealias vs 展開型も不一致扱い）。
- 親型に同名メンバが既に存在 → エラー。
- `visibility = PUBLIC`（または `cream.defaultVisibility=PUBLIC`）強制時に、シグネチャが
  internal シンボル（internal な親レシーバ / internal なプロパティ型）を公開する → エラー
  （Kotlin の `'public' member exposes its 'internal' type` を生成ファイルで踏ませない）。
  INHERIT 経路は narrowest 継承が internal に落とすため追加検証不要（正当なユーザーコードでは
  public プロパティが internal 型を持てない）。
- `propertyName` の文字列自体は検証しない（`funName` と同じポリシー — 生成ファイルの
  コンパイルエラーに委ねる）。

### `@ChildOptionals`（cream-runtime）

```kotlin
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
public annotation class ChildOptionals(
    val kdoc: KDoc = KDoc(),
    val visibility: CopyVisibility = CopyVisibility.INHERIT,
) {
    @Target(AnnotationTarget.PROPERTY)
    public annotation class Exclude
}
```

- sealed class / sealed interface 以外に付けたらエラー（`@CopyToChildren` と同じ検証）。
- 対象プロパティ = 全 transitive concrete leaf（`collectConcreteSubclasses()`）で **その leaf 自身が宣言する** public / internal プロパティ（constructor + body）。
  - 親型で既に見えるプロパティ（override 含む）は除外。
  - private プロパティは **skip（エラーにしない）** — 一括適用のため。
  - 拡張プロパティも **silent skip**（`@ParentOptional` 付きなら @ParentOptional feature 側が
    エラー報告するため二重報告しない）。
  - 親が pin していない型パラメータを参照するプロパティは **warning 付き skip**
    （一括適用でユーザーが選んだわけではないのでエラーにしない。silent だと「アクセサが無い」
    のがバグに見えるため warning は出す）。判定は `typeParameterNamesUnpinnedBy()` で
    render 時の unmapped 検出と同じ walk を共有する。
  - `@ChildOptionals.Exclude` が付いた子プロパティは **sweep から外す**（その寄与からはアクセサを
    生成しない）。`@ChildOptionals` はプロパティ単位の opt-in を持たないので、一括適用から 1 つだけ
    抜くための唯一の手段。`.Exclude` は cream 共通の「自動挙動から外す」意味だが、コピー系（既定値を
    落として必須化）と異なり `@ChildOptionals` では **生成アクセサ自体を作らない**。
    - **マージ**: 同名にマージされる複数寄与のうち除外された子は `is <子>` 分岐だけ落ちる。ある名前への
      **全寄与**が除外されるとその名前のアクセサは生成されない。
    - **`@ParentOptional` 併用**: `.Exclude` は sweep 由来のプロパティのみに効く。明示的に
      `@ParentOptional` を付けたプロパティは opt-in が sweep opt-out に勝ち、`.Exclude` が同時に
      付いていてもアクセサは生成される（上記所有権ルールの延長：ChildOptionals 親配下の
      `@ParentOptional` プロパティは ParentOptional のルールで生成される）。
    - **効果なし警告**: sweep が元々拾わないプロパティ（`@ChildOptionals` 階層外、あるいは
      private / 拡張 / 親で既に見える / unpinned で既に skip 済み）への `.Exclude` は効果がなく、
      positioned な KSP warning を出す（cream の他の未マッチ `.Exclude` 警告と同じ）。
  - `@ParentOptional` には除外の概念を設けない（opt-in なので、対象にしたくなければ付けなければよい）。

### 併用時の所有権ルール（redeclaration 衝突の防止）

`@ParentOptional`（子プロパティ）と `@ChildOptionals`（親）が同じ親型に同名 accessor を別ファイルへ生成すると
redeclaration でコンパイルエラーになるため、**(プロパティ, sealed 祖先) ペア単位** で生成の所有者を一意に決める:

- sealed 祖先 P に `@ChildOptionals` が **ある** → P への accessor は ChildOptionals feature が生成する。
  配下の `@ParentOptional` 付きプロパティも含めてマージし、その `propertyName` / `kdoc` / `visibility` 指定は尊重する。
- sealed 祖先 P に `@ChildOptionals` が **ない** → P への accessor は `@ParentOptional` feature が生成する
  （`@ParentOptional` 付きプロパティのみが対象）。

各 feature は相手 feature のコードに依存せず、KSP でアノテーションの有無を検査するだけで判定できる
（feature 間依存禁止の Konsist ルールに抵触しない）。

### 生成規則

- 生成単位 = sealed 親型。ファイル名 `ParentOptional__<Parent.underPackageName>` / `ChildOptionals__<Parent.underPackageName>`、パッケージ = 親のパッケージ、`Dependencies(aggregating = true, ...)`。
- 親 P × 生成プロパティ名ごとに 1 つの拡張プロパティ:

```kotlin
public val P.name: T?
    get() = when (this) {
        is Child1 -> name
        is Child2 -> name
        else -> null
    }
```

- 常に `when` ベース（単一子でも）。unchecked cast を避け、generics の bare `is` smart cast と相性が良い。
- **どの親に生成するか**: annotated プロパティを持つ子から見た全ての sealed 上位型（transitive）。中間 sealed 型にも生成（`val Middle.data` と `val Parent.data` の両方。静的型に応じて解決される）。
- 元プロパティ型が nullable（`String?`）ならそのまま `String?`（`??` 重ねない）。null の
  曖昧さ（「その子でない」vs「プロパティ自体が null」）は生成 KDoc に注記する。
- typealias はエイリアスのまま維持（展開しない。KSP2 の resolve が返す宣言をそのまま描画）。
- `@Deprecated`（子クラス / プロパティ、constructor param 表面化も fallback で拾う）は
  message + level を維持してアクセサへ伝播（issue #103 の copyFun 前例）。**関数と違い
  property accessor の body は containment で deprecation 免除されない**ため、伝播時のみ
  `@Suppress` に `DEPRECATION` / `DEPRECATION_ERROR` を追加する（level=ERROR ソースを
  コンパイル可能に保つ）。マージ時は分岐順で最初の deprecation が勝つ。
- 可視性: `visibility = INHERIT` 時は既存の解決機構（`cream.defaultVisibility` → 対象宣言の可視性継承）に従う。継承元は「親型・子型・プロパティのうち最も狭い可視性」（enclosing チェーン込み）。public / internal のみサポート。PUBLIC 強制時は exposure 検証あり（上記）。
- 型パラメータ: 親が generic なら `val <T> State<T>.name: T?` を生成（`core/sealedCopy` の rendering helper を再利用）。複数上限境界は property の `where` 句として描画。子クラス固有の（親に pin されない）型パラメータをプロパティ型が参照する場合は v1 非対応 → エラー診断（@ChildOptionals の一括適用では warning + skip）。
- 識別子は `escapeKotlinIdentifier()` を通す（アクセサ名 / 分岐内のプロパティ参照 / KDoc example 内）。
- KDoc: `appendAutoGeneratedFunctionKDoc`（`(Auto generate by @ParentOptional annotation of [Child])` ヘッダ + 元プロパティへの `@see`）。

### アーキテクチャ配置

| 層 | 追加物 |
|---|---|
| cream-runtime | `ParentOptional.kt`, `ChildOptionals.kt` |
| core/common | `GenerateSourceAnnotation` に `ParentOptional` / `ChildOptionals` 実装追加（`ExcludeProperty.kt` の 3 つの `when` に分岐追加） |
| feature | `feature/parentOptional/ProcessParentOptional.kt`, `feature/childOptionals/ProcessChildOptionals.kt`（`context(processContext: ProcessContext) internal fun processXxx(): List<KSAnnotated>`） |
| core | `core/parentOptional/`（accessor 生成。両 feature が共有） |
| root | `CreamSymbolProcessor.process()` に dispatch 2 行追加 |
| テスト基盤 | `KonsistSupport.CORE_SUBPACKAGES` に `parentOptional` 追加、`.claude/rules/ksp-architecture.md` 等の記述更新 |

feature 間依存は発生しない（`@ChildOptionals` 側が「プロパティに `@ParentOptional` が付いているか」を KSP で検査するのみ）。

### テスト

- **SnapshotTest**（`.claude/skills/cream-snapshot-test` 準拠、generator 駆動）: 想定 family — sealedParentKind / hierarchyShape（中間 sealed）/ merge（複数子同名）/ propertyName / propertyShape（nullable・generic）/ visibility / kdoc / childOptionals 一括。
- **InvalidUsageTest**（diagnostic golden）: sealed 親なし / 型不一致マージ / private プロパティ（@ParentOptional）/ 非 sealed への @ChildOptionals / 親に同名メンバ。
- **統合（test/ モジュール）**: 生成 accessor の実行時挙動（Success → 値、Loading → null、マージ、propertyName 指定）。

### Future work（v1 スコープ外）

- null 以外の fallback 値
- グローバル命名 template オプション（`{property}OrNull` 等）
- 型不一致マージの LUB 解決
- 子クラス固有型パラメータを含むプロパティ対応

### Known limitations

- multi-round 処理: `validate()` に失敗して round を跨いで deferred になった symbol が、
  後続 round で同一 sealed 親のファイル（`ParentOptional__<Parent>` / `ChildOptionals__<Parent>`）
  へ再度集約されると、前 round で作成済みの同名ファイルと衝突し得る（未検証・既知の制限）。
- `expect`/`actual` の sealed 階層は未検証（kctfork は単一プラットフォーム JVM コンパイルのため
  E2E 化できない）。KSP は各コンパイルを独立処理するので、見えている宣言に従って生成される想定。
- ユーザーが手書きした同名の拡張プロパティとの衝突は KSP からは検出できない
  （通常の Kotlin redeclaration エラーとして表面化する）。
- マージされたアクセサの `kdoc` 引数は先頭 entry のみ描画（PR 記載どおり）。
