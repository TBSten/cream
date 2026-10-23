[cream.kt](../../README.md) / [cream-ksp](../README.md)

# Composition root

The KSP entry point and the context it hands to every feature: the only files directly in `me.tbsten.cream.ksp`

`ksp/` 直下は **オーケストレーション + 横断型のみ**。生成ロジックは置かない。
置いてよいのはこの group の 3 ファイルだけで、それ以外は deny by default で `[UnexpectedFile]` になる。

❌ いけないこと:
- 生成ロジックを書く（→ `core/`）
- 注釈ごとの処理を書く（→ `feature/<name>/`）
- 汎用ヘルパを書く（→ `util/`）
- 例外を書く（→ `core/error/`。`CreamException` 階層はそこにある）
- KSP オプションのモデル・パースを書く（→ `options/`）
- `ProcessContext.kt` から feature / core / util を import する

> 補足: `GenerateSourceAnnotation` は core が生成時に使う型なので `core/common/` に置く（直下ではない）。

| Role | Summary |
|---|---|
| [SymbolProcessor](./SymbolProcessor.md) | `CreamSymbolProcessor`: `process()` で全 feature を順に dispatch する。option パース・round / deferred 集約 |
| [SymbolProcessorProvider](./SymbolProcessorProvider.md) | `CreamSymbolProcessorProvider`: KSP provider。環境から `ProcessContext` の材料を取り出す |
| [ProcessContext](./ProcessContext.md) | `ProcessContext = {resolver, options, codeGenerator, logger}`: every feature receives it as a context parameter |
| [ServiceRegistration](./ServiceRegistration.md) | The `META-INF/services` entry through which KSP discovers `CreamSymbolProcessorProvider` |

## Placement in this group

```
cream-ksp/src/main/
  kotlin/me/tbsten/cream/ksp/
    CreamSymbolProcessor.kt                                                               SymbolProcessor
    CreamSymbolProcessorProvider.kt                                                       SymbolProcessorProvider
    ProcessContext.kt                                                                     ProcessContext
  resources/META-INF/services/com.google.devtools.ksp.processing.SymbolProcessorProvider  ServiceRegistration
```
