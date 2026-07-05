[← cream.kt README](../../README.ja.md) | [English](./README.md)

# ユースケース

実際のプロジェクトで cream.kt が効く場面を紹介する実践レシピ集です。各記事は素朴な手書き実装から始め、要件が増えるとそれがどう劣化していくかを示した上で、cream.kt のアノテーションで「意味のある差分だけが残る」形に書き直します。

## 概説

- **[sealed class を使った UI 状態の管理に cream.kt を利用する](./ui-state-management-by-sealed-class/README.ja.md)** — 全 5 回の UiState シリーズの索引。状態間の遷移には `@CopyTo` / `@CopyToChildren`、サブタイプを保った更新には `@SealedCopy` を使います。
- **[異なるレイヤーのモデルのマッピングを cream.kt で簡素化する](./model-mapping.ja.md)** — data layer → domain layer の自明な詰め替えを `@CopyMapping` に任せ、意味のある変換（value class 化・型変換）だけをマッピング関数に残します。

## UI 状態管理シリーズ

上の概説で挙げたシナリオを 1 つずつ深掘りするシリーズです。

1. **[第 1 回: Loading / Success / Error と共通プロパティの保守](./ui-state-management-by-sealed-class/01.ja.md)** — 遷移元の状態クラスへの `@CopyTo` で、すべての状態遷移から共通プロパティの書き写しをなくします。
2. **[第 2 回: データを保ったままの遷移とリフレッシュ・楽観的更新](./ui-state-management-by-sealed-class/02.ja.md)** — 共有プロパティの更新のたびに増殖する `when` を、`@SealedCopy` が生成する親型の `copy()` に置き換えます。
3. **[第 3 回: ネストした sealed StateMachine を1つの注釈で網羅する](./ui-state-management-by-sealed-class/03.ja.md)** — ルートへの `@CopyToChildren` 1 回で、入れ子のチェックアウトフロー階層の全ての推移的な子クラスへの copy 関数を生成します。
4. **[第 4 回: MVI の reduce を宣言的に書く](./ui-state-management-by-sealed-class/04.ja.md)** — `@CopyToChildren` と `@SealedCopy` を併用し、reducer をイベントごとの差分の宣言だけにします。
5. **[第 5 回: 状態管理ライブラリ Koma との併用](./ui-state-management-by-sealed-class/05.ja.md)** — 状態管理ライブラリ Koma の Store DSL と併用し、`nextState` の中身を「本質的に変化したもの」だけにします。
