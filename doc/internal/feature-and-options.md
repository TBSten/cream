# 機能とオプションの対応

> v0.9.0-alpha01 時点の情報 + #135 の @ParentOptional / @ChildOptionals。出典は `cream-runtime` の注釈定義と `CreamOptions`（`cream-ksp/shared`）。

凡例: **✓** = その単位で指定可 ／ **✕** = 該当引数なし ／ **適用** = 引数は無いがプロジェクト設定が効く ／ **✱** = 別オプションが担当

| オプション | プロジェクト単位 (`ksp.arg`) | CopyTo | CopyFrom | CopyToChildren | SealedCopy | CombineTo | CombineFrom | CopyMapping | CombineMapping | ParentOptional | ChildOptionals |
|---|---|:--:|:--:|:--:|:--:|:--:|:--:|:--:|:--:|:--:|:--:|
| **命名 — 生成関数の名前** | | | | | | | | | | | |
| 関数名 prefix | ✓ `cream.copyFunNamePrefix` | ✕ | ✕ | ✕ | ✕ | ✕ | ✕ | ✕ | ✕ | ✕ | ✕ |
| 関数名 strategy | ✓ `cream.copyFunNamingStrategy` | ✕ | ✕ | ✕ | ✕ | ✕ | ✕ | ✕ | ✕ | ✕ | ✕ |
| ドット処理 escapeDot | ✓ `cream.escapeDot` | ✕ | ✕ | ✕ | ✕ | ✕ | ✕ | ✕ | ✕ | ✕ | ✕ |
| funName（名前個別上書き） | —（既定値が上記3つを合成） | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✱ | ✱ |
| **可視性・ドキュメント** | | | | | | | | | | | |
| visibility | ✓ `cream.defaultVisibility` | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ |
| kdoc | ✕ 無 | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ |
| **コピー挙動の制御（機能固有トグル）** | | | | | | | | | | | |
| notCopyToObject | ✓ `cream.notCopyToObject` | 適用 | 適用 | ✓ | ✱ | 適用 | 適用 | 適用 | 適用 | ✕ | ✕ |
| nonCopyableStrategy | ✕ 無 | ✕ | ✕ | ✕ | ✓ | ✕ | ✕ | ✕ | ✕ | ✕ | ✕ |
| canReverse | ✕ 無 | ✕ | ✕ | ✕ | ✕ | ✕ | ✕ | ✓ | ✕ | ✕ | ✕ |
| **プロパティ単位の制御** | | | | | | | | | | | |
| 名前マッピング (`.Map`/`properties`) | ✕ 無 | ✓ Map | ✓ Map | ✓ Map | ✓ Map | ✓ Map | ✓ Map | ✓ prop | ✓ prop | ✕ | ✕ |
| デリゲート選択 (`.Via`) | ✕ 無 | ✕ | ✕ | ✕ | ✓ | ✕ | ✕ | ✕ | ✕ | ✕ | ✕ |
| 自動コピー除外 (`.Exclude`) | ✕ 無 | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ excludes | ✓ excludes | ✕ | ✓ † |
| **対象の指定** | | | | | | | | | | | |
| 対象指定 targets/sources | —（必須引数） | ✓ | ✓ | —(自身) | —(自身) | ✓ | ✓ | ✓ | ✓ | —(sealed 祖先) | —(自身) |

✱ (ParentOptional / ChildOptionals 列): 生成するのは関数ではなく拡張プロパティ。名前の個別上書きは `funName` ではなく `@ParentOptional(propertyName = ...)` が担当（`@ChildOptionals` の一括対象のプロパティでも尊重される）。

† (ChildOptionals 列の `.Exclude`): コピー系の `.Exclude` が「自動コピーの既定値を落として引数を必須にする」のに対し、`@ChildOptionals.Exclude` は「その子プロパティを一括 sweep から外し、生成アクセサ自体を作らない」を意味する（除外＝生成アクセサからの除外）。ParentOptional は opt-in のため除外の概念を持たない（対象にしたくないプロパティは注釈しなければよい）。
