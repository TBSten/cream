[← README](../../../README.ja.md) | [English](./README.md)

# sealed class を使った UI 状態の管理に cream.kt を利用する

宣言的 UI や UDF (Unidirectional Data Flow) を採用したアプリでは、画面の状態を `sealed interface` / `sealed class` の UiState として表現するのが定番です。しかしその状態遷移コードは、共通プロパティの書き写しや全サブタイプを列挙する `when` といった「自明なボイラープレート」で埋め尽くされがちで、本当に伝えたい差分がノイズに埋もれていきます。このシリーズでは、商品詳細・フィード・チェックアウト・カウンタ・検索という 5 つの具体的な画面を題材に、cream.kt のアノテーション（`@CopyToChildren` / `@SealedCopy`）でその遷移コードを「本質的な差分だけを語る」形に整える方法を紹介します。各記事は独立して読めるので、手元の課題に近いものから読み始めてください。

## シリーズ記事一覧

1. **[第 1 回: Loading / Success / Error と共通プロパティの保守](./01.ja.md)**
   `itemId` のような共通プロパティを状態遷移のたびに手で書き写すコードが、共通プロパティの追加でどう膨らむかを示し、遷移元の状態クラスへの `@CopyTo` で遷移側の修正を不要にします。まず最初に読むのにおすすめの回です。
2. **[第 2 回: データを保ったままの遷移とリフレッシュ・楽観的更新](./02.ja.md)**
   プルリフレッシュや楽観的更新のように「サブタイプと既存データを保ったまま共有プロパティだけ差し替える」遷移を、増殖する `when` 分岐の代わりに `@SealedCopy` が生成する親型の `copy()` 1 行で書きます。`nonCopyableStrategy` による object サブタイプの扱いも解説します。
3. **[第 3 回: ネストした sealed StateMachine を1つの注釈で網羅する](./03.ja.md)**
   チェックアウトフローのような「中間 sealed を挟んだ入れ子の StateMachine」でも、ルートへの `@CopyToChildren` 1 回ですべての推移的な子クラスへの copy 関数が生成されることを示します。子クラスごとに `@CopyTo` を書き続ける方式との違いが分かる回です。
4. **[第 4 回: MVI の reduce を宣言的に書く](./04.ja.md)**
   `@CopyToChildren`（状態間の遷移）と `@SealedCopy`（状態内の共有プロパティ更新）を併用し、MVI の reducer を「イベントごとの差分の宣言」だけで書けるようにする、シリーズの集大成的な回です。

5. **[第 5 回: 状態管理ライブラリ Koma との併用](./05.ja.md)**
   状態管理ライブラリ [Koma](https://github.com/komakt/koma) の Store DSL と cream.kt を併用し、`nextState` の中身を「その遷移で本質的に変化したもの」だけにします。

## 関連

- [異なるレイヤーのモデルのマッピングを cream.kt で簡素化する](../model-mapping.ja.md) — UiState 以外の代表的ユースケースである、data layer ↔ domain layer 間のモデル詰め替えについて。
