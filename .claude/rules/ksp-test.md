---
paths:
  - cream-ksp/src/test/**
  - cream-ksp/src/test/resources/snapshots/**
---

# cream-ksp tests → katachi

cream-ksp のテストの置き場所・種類・書き方（kotest `FreeSpec`、feature ごとの 5 種のテスト、snapshot golden の形式と更新方法、
diagnostic の `normalizedCompilerOutput()`、テスト基盤）の SSoT は katachi の定義（`architecture-test/`）。

読むもの: [`doc/internal/architecture/creamKspTest/README.md`](../../doc/internal/architecture/creamKspTest/README.md) と、その配下の
Role ページ（特に `featureTest/` と `SnapshotGolden.md`）。

```bash
./gradlew :cream-ksp:test -Dcream.snapshot.update=true   # snapshot 更新
./gradlew :architecture-test:test                        # 置き場所・命名・FreeSpec の検査
```
