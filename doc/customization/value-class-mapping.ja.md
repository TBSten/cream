[← README](../../README.ja.md) | [English](./value-class-mapping.md)

# 自動 value class マッピング

cream は、名前が一致するプロパティの型が遷移先コンストラクタ引数の型と
**`value class` のラップだけ** 違う場合、自動でマッピングします
（[issue #21](https://github.com/TBSten/cream/issues/21)）。これは
**デフォルトで有効** で、すべての copy / combine アノテーション — `@CopyTo`・`@CopyFrom`・
`@CopyToChildren`・`@CombineTo`・`@CombineFrom`・`@CopyMapping`（`canReverse` の逆方向関数を含む）・
`@CombineMapping` — に、アノテーション側の引数なしで適用されます:

- **Wrap（ラップ）** — 遷移先の引数の型が value class `V(val u: U)` で、遷移元プロパティが `U`
  → デフォルト値として `V(this.<property>)` を生成
- **Unwrap（アンラップ）** — 遷移元プロパティが value class `V(val u: U)` で、遷移先の引数の型が `U`
  → デフォルト値として `this.<property>.u` を生成

型互換な名前一致が常に優先されます。この変換は通常のプロパティマッチングが何も見つけられなかった
場合にのみ参照されるため、通常のマッチングの挙動は変わりません。変換が成立しない場合、
そのような引数は単に必須引数のまま — この機能が入る前とまったく同じ挙動 — です。

`@SealedCopy` には影響しません: 生成される `copy()` の引数は sealed 親自身の抽象プロパティ
（両側とも同じ型）なので、ラップ / アンラップの余地がありません。

## Wrap: 通常の型 → value class

```kt
import me.tbsten.cream.CopyTo

@JvmInline
value class DomainId(val value: String)

@CopyTo(DomainModel::class)
data class DataModel(
    val id: String,
    val name: String,
)

data class DomainModel(
    val id: DomainId,
    val name: String,
)

// usage
val dataModel = DataModel(id = "id-1", name = "cream")
val domainModel: DomainModel = dataModel.copyToDomainModel()
// domainModel.id == DomainId("id-1")
```

<details>
<summary>生成されるコード</summary>

```kt
// auto generate
fun DataModel.copyToDomainModel(
    id: DomainId = DomainId(this.id), // 自動でラップされます
    name: String = this.name,
): DomainModel = ...
```

</details>

## Unwrap: value class → 通常の型

逆方向も同じように動作します（ここでは `@CopyFrom` の例）:

```kt
import me.tbsten.cream.CopyFrom

@JvmInline
value class DomainId(val value: String)

data class DomainModel(
    val id: DomainId,
    val name: String,
)

@CopyFrom(DomainModel::class)
data class DataModel(
    val id: String,
    val name: String,
)

// usage
val domainModel = DomainModel(id = DomainId("id-1"), name = "cream")
val dataModel: DataModel = domainModel.copyToDataModel()
// dataModel.id == "id-1"
```

<details>
<summary>生成されるコード</summary>

```kt
// auto generate
fun DomainModel.copyToDataModel(
    id: String = this.id.value, // 自動でアンラップされます
    name: String = this.name,
): DataModel = ...
```

</details>

combine 関数（`@CombineTo` / `@CombineFrom` / `@CombineMapping`）では、変換は正しい遷移元の
qualifier を選びます。例えば一致するプロパティが 2 つ目以降の遷移元にある場合は
`id: DomainId = DomainId(sourceB.id)` になります。

## Nullability

この変換は **健全（sound）なデフォルト値だけ** を生成します — `null` を non-null な value class
に変えてしまう可能性のあるデフォルト値は決して生成されません。その場合、引数は必須のままになり、
おそらく意図と違うため cream は [警告](#警告) を出します。

value class `V(val u: U)`（underlying が non-null）の場合:

| 遷移元プロパティ | 遷移先引数 | 生成されるデフォルト値 |
|---|---|---|
| `U` | `V` | `V(this.x)` |
| `U` | `V?` | `V(this.x)` |
| `U?` | `V?` | `this.x?.let { V(it) }` |
| `U?` | `V` | なし — 必須引数 + 警告 |
| `V` | `U` | `this.x.u` |
| `V` | `U?` | `this.x.u` |
| `V?` | `U?` | `this.x?.u` |
| `V?` | `U` | なし — 必須引数 + 警告 |

**underlying が nullable** な `V(val u: U?)` の場合、ラップは `U` と `U?` の両方の遷移元を受け
入れます（underlying 自体が nullable なので `V(this.x)`）。一方アンラップは常に nullable な値に
なるため、遷移先の引数は nullable である必要があります:

| 遷移元プロパティ | 遷移先引数 | 生成されるデフォルト値 |
|---|---|---|
| `U` または `U?` | `V` または `V?` | `V(this.x)` |
| `V` | `U?` | `this.x.u` |
| `V` | `U` | なし — 必須引数 + 警告 |

## チェーンした value class

value class が value class をラップしている場合、**すべての層を通して** 変換されます
（最大 8 層 — 現実のモデリングでは十分すぎる深さで、上限は病的な階層へのガードです）:

```kt
@JvmInline value class RawId(val value: String)
@JvmInline value class UserId(val rawId: RawId)

// Source(id: String) -> Target(id: UserId)
id: UserId = UserId(RawId(this.id))       // チェーンしたラップ

// Source(id: UserId) -> Target(id: String)
id: String = this.id.rawId.value          // チェーンしたアンラップ
```

上記の nullability ルールはチェーン全体に適用されます（例: `String?` → `UserId?` は
`this.id?.let { UserId(RawId(it)) }` を生成し、アンラップチェーンの途中に nullable な層があると
`?.` に切り替わり以降の式が nullable になります）。1 つの変換はラップのみ、またはアンラップのみ
です — ある value class をアンラップして *別の* value class にラップし直すことはありません
（[制限](#制限) を参照）。

## typealias

typealias は両側で解決されます。引数の型が value class の alias の場合、生成されるコンストラクタ
呼び出しには **解決後のクラス** を使います（nullable 型の alias はコンストラクタ呼び出しに
使えないため）:

```kt
@JvmInline value class DomainId(val value: String)
typealias DomainIdAlias = DomainId

// Source(id: String) -> Target(id: DomainIdAlias)
id: DomainIdAlias = DomainId(this.id)
```

## `.Map` との相互作用

この変換は **通常のマッチングと同じ名前解決ルール** で遷移元プロパティを解決します。そのため
`.Map`（またはマッピングアノテーションの `properties = [Map(...)]` エントリ）でリネームされた
プロパティも、型が value class 分だけ違えばラップ / アンラップされます:

```kt
@CopyTo(DomainModel::class)
data class DataModel(
    @CopyTo.Map("id") val rawId: String,
)

data class DomainModel(val id: DomainId)

// generated
id: DomainId = DomainId(this.rawId)
```

## `.Exclude` との相互作用

**`.Exclude` が優先されます。** デフォルト値が value class 変換由来になる引数も、通常のマッチと
まったく同じように除外できます: 変換のデフォルト値は抑制され、引数は必須のままになります。
この場合 exclude は *効いている* ので、「`@Exclude ... has no effect`」警告は出ません — また、
明示的な `.Exclude` はそのプロパティに対する下記の near-miss 警告も抑制します。

## 警告

変換が **あと一歩** で適用できたのに、おそらく意図しない理由でスキップされた場合、cream は位置
情報付きの KSP 警告（`Automatic value class mapping for '<param>' skipped: ...`）を出し、引数を
必須のままにします:

- 遷移元プロパティが nullable なのに遷移先の引数の型が non-null（ラップ方向）
- アンラップした値が nullable なのに遷移先の引数の型が non-null（アンラップ方向）
- value class の primary constructor に生成コードからアクセスできない（ラップ方向）
- underlying プロパティに生成コードからアクセスできない（アンラップ方向）

単純な型不一致（例: `id: Int` と `DomainId(val value: String)`）には警告は出ません — 従来どおり
引数が黙って必須のままになります。

## 両方向が成立する場合

ラップとアンラップの両方が成立するのは、遷移元プロパティが「underlying が遷移先の value class
そのもの」という value class（`Holder(val wrapper: Wrapper)` → `Wrapper`）で、かつ遷移先の
underlying が遷移元の supertype（例: `Wrapper(val raw: Any)`）の場合だけです。このとき
**決定的にアンラップが優先** されます: `Holder` の中にすでに入っている `Wrapper` そのものを
取り出します（`this.x.wrapper`）。ラップを選ぶと `Holder` ごと `Any` に箱詰めしてしまいます。

## オプトアウト: `cream.autoValueClassMapping`

アノテーション単位のスイッチはありませんが、モジュール全体の KSP オプション
[`cream.autoValueClassMapping`](./options.ja.md)（デフォルト `true`）で変換を（警告も含めて）
完全に無効化できます。対象の引数はこの機能が入る前と同じく必須引数のままになります:

```kts
// module/build.gradle.kts
ksp {
    arg("cream.autoValueClassMapping", "false")
}
```

`cream.notCopyToObject` と同様、このオプションは寛容にパースされます: リテラル `"false"`
（大文字小文字を区別しない）だけが無効化し、それ以外の値はデフォルトのままです。

## 制限

以下のケースでは引数が **必須** のままになります（この機能が入る前と同じ、安全側の挙動）。
コンパイルできないコードが生成されることはありません:

- **ジェネリックな value class**（`V<T>(val t: T)`）は変換されません。これは Kotlin の
  experimental な言語機能（`-XXLanguage:+GenericInlineClasses`）であり、cream は一律スキップ
  します。
- **`vararg` 引数** は変換されません — 対応するプロパティは要素型の *配列* であり、要素単位の
  ラップ / アンラップはスコープ外です。
- **混合変換はしません。** 1 つの変換はラップのみ、またはアンラップのみです。value class を
  アンラップして別の value class にラップし直すこと
  （`SourceId(val value: String)` → `TargetId(val value: String)`）はありません。このような
  プロパティは明示的に渡してください。
- **可視性**: ラップには呼び出せる primary constructor、アンラップには読める underlying
  プロパティが必要です — `public` は常に可、`internal` は同じモジュールで宣言された value class
  のみ可（`@CopyMapping` / `@CombineMapping` の classpath 上の value class では `internal` は
  使えません）、`private` / `protected` は不可です。惜しいケースでは警告が出ます
  （[警告](#警告) を参照）。
- **検出は modifier ベース**（`value class`）なので、マルチプラットフォームの `value class` /
  `expect value class` 宣言も `@JvmInline` 付きと同様に対象です — `expect` value class は
  expect 宣言自体が可視な単一引数の primary constructor を持っていれば変換されます。
- **ジェネリック value class の typealias** はジェネリック value class と同様にスキップされます。

## See also

- [KSP Options](./options.ja.md) — `cream.autoValueClassMapping`（モジュール全体のオプトアウト）
- [Property mapping (.Map)](./property-mapping.ja.md) — 名前が違うプロパティへのマッピング
- [自動コピー除外 (.Exclude)](./exclude.ja.md) — 引数の自動コピーデフォルト値を除去
- [Copy — @CopyTo / @CopyFrom / @CopyMapping](../copy.ja.md)
- [Combine — @CombineTo / @CombineFrom / @CombineMapping](../combine.ja.md)
