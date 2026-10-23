---
paths:
  - cream-ksp/src/main/**
---

# cream-ksp architecture → katachi

cream-ksp のアーキテクチャ規約（3 層 + composition root の置き場所・依存方向テーブル・命名・ファイル行数・横断ルール）の
SSoT は katachi の定義（`architecture-test/`）。`./gradlew :architecture-test:test` が全ファイルを検査する。

読むもの（定義から生成したページ）:

- 全体・依存方向テーブル・横断ルール: [`doc/internal/architecture/creamKsp/README.md`](../../doc/internal/architecture/creamKsp/README.md)
- `ksp/` 直下: [`creamKsp/compositionRoot/`](../../doc/internal/architecture/creamKsp/compositionRoot/README.md)
- `feature/<name>/`: [`creamKsp/feature/`](../../doc/internal/architecture/creamKsp/feature/README.md)
- `core/<sub>/`: [`creamKsp/core/`](../../doc/internal/architecture/creamKsp/core/README.md)
- `options/` / `util/`: [`creamKsp/KspOption.md`](../../doc/internal/architecture/creamKsp/KspOption.md) / [`creamKsp/util/`](../../doc/internal/architecture/creamKsp/util/README.md)

規約を変えるときは `architecture-test/src/test/kotlin/me/tbsten/cream/test/architecture/creamKsp/` の Role / group を直し、
`./gradlew :architecture-test:katachiDocs` でページを再生成する（[`architecture-test.md`](architecture-test.md)）。
