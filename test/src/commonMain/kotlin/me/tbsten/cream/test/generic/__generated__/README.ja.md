# 自動生成されたソースコード

このディレクトリには、`dumpGeneratedSource` Gradle タスクによって自動的にコピーされた KSP 生成のソースコードが含まれています。

**⚠️ このディレクトリ内のファイルを手動で編集しないでください**

これらのファイルは自動生成され、以下の場所からコピーされています：
- コピー元: `test/build/generated/ksp/metadata/commonMain/kotlin/me/tbsten/cream/test/generic/`
- コピー先: `test/src/commonMain/kotlin/me/tbsten/cream/test/generic/__generated__/`

## 目的

このディレクトリは、ビルドプロセス中に自動生成されるコードをソースツリー内で可視化し、開発者が生成されるコードを理解しやすくするために存在します。

## ファイル拡張子について

全ての Kotlin ファイル（`.kt`）は、IDE のエラー表示やコンパイル時の衝突を防ぐために `.kt.generated` 拡張子でコピーされています。実際の KSP 生成コードは `build/generated/ksp/` に存在します。

## 生成コードの更新方法

このディレクトリ内のファイルを更新するには：
1. 次のコマンドを実行: `./gradlew :test:dumpGeneratedSource`
2. タスクが最新の KSP 出力から全てのファイルを再生成します

## 生成元

Gradle タスク: `dumpGeneratedSource`

---

[English version](README.md)