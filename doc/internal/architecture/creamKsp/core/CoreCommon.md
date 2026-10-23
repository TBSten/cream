[cream.kt](../../README.md) / [cream-ksp](../README.md) / [core layer](README.md)

# core/common

全生成系で共有する部品: 型パラメータ・sealed 型レンダリング・プロパティマッチング・KDoc・命名・target 検証・visibility・診断ヘルパ

- 型パラメータ（header / where 描画）、sealed 型レンダリング（receiver / `is` 分岐型、`SealedTypeRendering.kt`。sealedCopy / parentOptional が共有）
- プロパティマッチング、KDoc 生成、命名（`CopyFunctionName.kt` / `FunctionNameTemplate.kt`）、identifier escape の呼び出し
- `GenerateSourceAnnotation` と、その 10 実装 `<Name>SourceAnnotation`（`CopySourceAnnotation.kt` / `CombineSourceAnnotation.kt` /
  `MappingSourceAnnotation.kt` / `ParentOptionalSourceAnnotation.kt` に family 別）。api 公開のため **意図的に sealed ではない** —
  注釈ごとの差分は `when` ではなく overridable member（安全な既定値つき）で表す
- パラメータ単位の挙動 `FindMappedSourceProperty` / `IsExcluded` は interface のメンバではなく独立した関数型。
  core の `appendXxx` はこれらを**通常の引数**で受け取り、GSA からは読まない（注釈以外から生成を駆動できるようにするため）

## Placement

| Path | When to use |
|---|---|
| `cream-ksp/src/main/kotlin/me/tbsten/cream/ksp/core/common/*.kt` |  |

## Constraints

- declares the package its directory spells
- stays within 300 lines (FindMatchedProperty.kt: 500)
- imports from cream / KSP only: core.*, options.*, util.*, cream-runtime, the KSP API

## Examples

- `GenerateSourceAnnotation`
- `FindMatchedProperty`
