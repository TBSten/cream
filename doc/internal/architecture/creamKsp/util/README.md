[cream.kt](../../README.md) / [cream-ksp](../README.md)

# util layer

他プロジェクトでも使える汎用ヘルパのみ（cream 固有型を含まない）

`util/` も `util/ksp/` も core / feature / options / cream-runtime / cream 固有型（`CreamOptions` / `CreamException` /
`CopyVisibility` / `GenerateSourceAnnotation` / `TargetValidation` 等）を参照しない。KSP 依存のヘルパは `util/ksp/` へ。

| Role | Summary |
|---|---|
| [util](./GenericUtil.md) | Kotlin stdlib だけに依存するヘルパ。KSP 以外の文脈でも再利用できる |
| [util/ksp](./KspUtil.md) | KSP API 上の汎用ヘルパ（汎用範囲の KSP API のみ） |

## Placement in this group

```
cream-ksp/src/main/kotlin/me/tbsten/cream/ksp/util/
  *.kt      util
  ksp/*.kt  util/ksp
```
