[← README](../README.ja.md) | [English](./comparison.md)

# 他ライブラリとの比較

Kotlin のデータマッピングライブラリを選択する際、いくつかの選択肢があります。ここでは cream.kt と他の人気ライブラリとの比較を示します。

> **最終更新: 2026-07**（cream.kt ドキュメント再構成時点）。各ライブラリは継続的に更新されるため、最新情報は必ず公式ドキュメント（[MapStruct](https://mapstruct.org/) / [KOMM](https://github.com/Scogun/komm) / [Mappie](https://mappie.tech/)）を参照してください。

## vs. MapStruct

**[MapStruct](https://mapstruct.org/)**（[GitHub](https://github.com/mapstruct/mapstruct)）は、異なるオブジェクトタイプ間のマッピングに特化した成熟した Java ベースのコード生成ライブラリです。

| 機能                 | cream.kt                    | MapStruct             |
|--------------------|-----------------------------|-----------------------|
| **言語**             | Kotlin ファーストで KSP を使用       | Java ファーストでアノテーション処理  |
| **状態遷移**           | ✅ sealed class の状態遷移に最適化（[@CopyToChildren](./copy-to-children.ja.md)、[@SealedCopy](./sealed-copy.ja.md)） | ❌ 公式には Java bean（Entity-DTO）マッピングが主眼 |
| **デフォルト値のオーバーライド** | ✅ 名前が一致したプロパティはデフォルト引数になり、呼び出し側でオーバーライド可能（[Copy](./copy.ja.md)） | ⚠️ マッピングは mapper インターフェース側で宣言し、デフォルト引数スタイルの呼び出し側オーバーライドはなし |
| **マルチプラットフォーム**    | ✅ Kotlin Multiplatform サポート | ❌ JVM のみ              |
| **IDE サポート**       | ✅ ネイティブな Kotlin IDE 統合      | ⚠️ Java ファーストなツーリング（Kotlin からはアノテーション処理 / kapt 経由） |
| **ユースケース**         | フロントエンド状態管理（UI 状態など）        | バックエンド Entity-DTO 変換  |

**MapStruct より cream.kt を選ぶべきケース:**

- Kotlin で開発している（特に Kotlin Multiplatform）
- sealed class での UI 状態遷移を管理する必要がある
- 状態遷移時にデフォルト値をオーバーライドしたい
- 軽量で Kotlin ネイティブなソリューションを好む

**MapStruct の方が適しているケース:**

- Java 中心のバックエンドで、チームがすでに MapStruct に習熟している場合。MapStruct の成熟した
  エコシステムとチームの既存の知識の方が、cream.kt の Kotlin ファーストな利点を上回る可能性が高いです。

## vs. KOMM (Kotlin Object Multiplatform Mapper)

**[KOMM](https://github.com/Scogun/komm)** は軽量な Kotlin Multiplatform マッピングライブラリで、同じく KSP を使用してコード生成を行います。

| 機能                     | cream.kt                                        | KOMM               |
|------------------------|-------------------------------------------------|--------------------|
| **構造の不一致処理**           | ✅ マッチしないプロパティは必須引数になり、名前の不一致は [`.Map`](./customization/property-mapping.ja.md) で対応付け可能 | ⚠️ `@MapName` / resolver でプロパティごとに設定（KOMM ドキュメントより） |
| **デフォルト値のオーバーライド**     | ✅ 名前が一致したプロパティはデフォルト引数になり、呼び出し側でオーバーライド可能（[Copy](./copy.ja.md)） | ⚠️ 生成される `toX()` 関数は引数を取らず、値はアノテーション / resolver で固定 |
| **高度な機能**              | ✅ [`@CopyToChildren`](./copy-to-children.ja.md)、[`@CombineTo`](./combine.ja.md)、[`@CopyMapping`](./copy.ja.md#copymapping) | ⚠️ マルチソースマッピング（ソースごとに関数を生成）とプラグイン。sealed 階層への一括生成はなし |
| **Object シングルトンへのコピー** | ✅ `object` 型へのコピー（オプトアウト可能: [notCopyToObject](./copy-to-children.ja.md#notcopytoobject)） | ⚠️ KOMM ドキュメントに記載なし |
| **複雑さ**                | ⚠️ より多くの機能 = 学習曲線がやや急                           | ✅ よりシンプルで軽量        |
| **柔軟性**                | ⚠️ 状態管理パターンに特化している                              | ✅ より汎用的な柔軟性        |

**KOMM より cream.kt を選ぶべきケース:**

- 複雑な状態管理を持つアプリケーションを構築している（例: [Koma](https://github.com/komakt/koma) のようなライブラリを使用）
- sealed interface からすべての子クラスへコピーする必要がある（`@CopyToChildren`）
- UI 状態のための sealed class 階層を頻繁に扱う
- 複数のソースクラスを 1 つのターゲットに結合する必要がある（`@CombineTo`）
- ソースコードを変更せずにライブラリ間マッピングが必要（`@CopyMapping`）

**KOMM の方が適しているケース:**

- よりシンプルで汎用的なマッピングライブラリが欲しい
- 意見の強いパターンよりも最大限の柔軟性を好む
- 複雑な状態階層なしでよりシンプルなマッピングニーズがある

### コード比較

同じ `SourceObject` → `DestinationObject` のコピーを、それぞれのライブラリで書いた例です。
KOMM 側のコードは [KOMM README](https://github.com/Scogun/komm) の最小例を簡略化したものです。

**cream.kt**（[@CopyFrom](./copy.ja.md#copyfrom)）:

```kt
class SourceObject {
    val id = 150
}

@CopyFrom(SourceObject::class)
data class DestinationObject(
    val id: Int,
)

// auto generate
fun SourceObject.copyToDestinationObject(
    id: Int = this.id,
): DestinationObject = /* ... */

// usage — マッチしたプロパティは呼び出し側でオーバーライド可能
val copied = sourceObject.copyToDestinationObject()
val overridden = sourceObject.copyToDestinationObject(id = 0)
```

**KOMM**（[KOMM README](https://github.com/Scogun/komm) より）:

```kt
class SourceObject {
    val id = 150
}

@KOMMMap(from = [SourceObject::class])
data class DestinationObject(
    val id: Int,
)

// auto generate
fun SourceObject.toDestinationObject(): DestinationObject = DestinationObject(
    id = id,
)

// usage — 値はマッピングで固定（呼び出し側でのオーバーライドは不可）
val copied = sourceObject.toDestinationObject()
```

## vs. Mappie

**[Mappie](https://github.com/Mr-Mappie/mappie)**（[公式ドキュメント](https://mappie.tech/)）は Kotlin **コンパイラプラグイン** として動作するオブジェクトマッピングライブラリです。`ObjectMappie<From, To>` を継承した mapper オブジェクトを宣言し、暗黙にマッピングできないプロパティを `mapping { ... }` DSL で記述します。

| 機能 | cream.kt | Mappie |
|---|---|---|
| **仕組み** | KSP（通常の Kotlin ソースを生成し、読める・デバッグできる） | コンパイラプラグイン（コンパイル時に生成され、生成コードはソースとして現れない） |
| **書き方** | クラスにアノテーションを付けるだけ（mapper クラス不要） | mapper object を宣言し、`mapping { }` DSL で記述 |
| **複数ソースの合成** | ✅ [`@CombineTo` / `@CombineFrom` / `@CombineMapping`](./combine.ja.md)。複数ソースに同名プロパティがあっても自動マッピング（引数側が優先） | ⚠️ `ObjectMappie2`〜`ObjectMappie5`（最大 5 ソース）。複数ソースに同名プロパティがあると暗黙マッピングされず、明示的な指定が必要 |
| **デフォルト値のオーバーライド** | ✅ 名前が一致したプロパティはデフォルト引数になり、呼び出し側でオーバーライド可能（[Copy](./copy.ja.md)） | ❌ マッピングは mapper 内で確定し、呼び出し側は `mapper.map(from)` を呼ぶだけ |
| **状態遷移** | ✅ sealed class の状態遷移に最適化（[@CopyToChildren](./copy-to-children.ja.md)、[@SealedCopy](./sealed-copy.ja.md)） | ❌ 主眼はオブジェクト間マッピング（Entity-DTO など） |
| **マルチプラットフォーム** | ✅ Kotlin Multiplatform サポート | ✅ Kotlin Multiplatform 対応のコンパイラプラグイン |

**Mappie より cream.kt を選ぶべきケース:**

- 呼び出し側でデフォルト値をオーバーライドしたい（状態遷移のように「一部だけ差し替える」呼び出しが多い）
- sealed class の状態遷移や、6 つ以上のソースの合成が必要
- mapper クラスを書かずに、アノテーション 1 つで済ませたい
- 生成コードを通常の Kotlin ソースとして確認したい

**Mappie の方が適しているケース:**

- 変換ロジックを mapper クラスに集約して一元管理したい（enum マッピングなども含む）
- Kotlin コンパイラプラグイン方式が許容でき、呼び出し側でのオーバーライドが不要な固定的マッピングが中心

## vs. 手書きのマッピングコード

すべてのプロジェクトにライブラリが必要なわけではありません。次のようなケースでは、ライブラリを導入せず手書きのコピーコードの方が適している可能性が高いです:

- **マッピングが 1〜2 箇所しかない場合。** その規模なら手書きのコピーコードの方が保守コストが低く、KSP 導入の手間とコード生成によるビルド時間のオーバーヘッドに見合わない可能性が高いです。
- **変換ロジック（型変換・バリデーション・集約）が大半を占める場合。** cream.kt が自動化するのは「同名プロパティの素通しコピー」の部分なので、それがほとんどないなら cream.kt にできることは多くありません。
- **プロジェクトの方針としてコード生成 / KSP を導入したくない場合。** cream.kt は本質的に KSP プロセッサであり、リフレクションやランタイムベースの代替モードはありません。
- **commonMain 中心の Kotlin Multiplatform で、workaround の制約が許容できない場合。** 推奨セットアップでは各プラットフォームソースセット（`androidMain`、`iosMain` など）のアノテーションは処理されません。詳細は [Multiplatform](./customization/multiplatform.ja.md) を参照してください。

## 関連ドキュメント

- [Copy — @CopyTo / @CopyFrom / @CopyMapping](./copy.ja.md)
- [Copy to children — @CopyToChildren](./copy-to-children.ja.md)
- [Combine — @CombineTo / @CombineFrom / @CombineMapping](./combine.ja.md)
- [Sealed copy — @SealedCopy](./sealed-copy.ja.md)
