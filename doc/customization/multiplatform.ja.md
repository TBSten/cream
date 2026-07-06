[← README](../../README.ja.md) | [English](./multiplatform.md)

# Kotlin Multiplatform サポート

cream.kt は Kotlin Multiplatform (KMP) を前提に設計されています。このページでは、公開されている
各アーティファクトが対応するプラットフォーム、知っておくべき KSP の制約 (`commonMain`)、
および推奨の workaround セットアップを説明します。

## 対応ターゲット

| アーティファクト | 役割 | 対応プラットフォーム |
|---|---|---|
| `me.tbsten.cream:cream-runtime` | コードに書くアノテーション | すべての Kotlin プラットフォーム |
| `me.tbsten.cream:cream-ksp` | KSP プロセッサ | JVM のみ |
| `me.tbsten.cream:cream-ksp-shared` | プロセッサ内部で使う共有ロジック | JVM / JS / WasmJs（直接依存することはありません） |

生成される copy 関数は **通常の Kotlin ソースファイル** です。処理対象のソースセットに追加され、 手書きのコードとまったく同様にモジュールの全ターゲット向けにコンパイルされます。

プラットフォーム固有のランタイム機構はありません。

## commonMain の制約

KSP は `commonMain` のような中間ソースセットへのコード生成をサポートしていません
([google/ksp#567](https://github.com/google/ksp/issues/567))。

デフォルトでは KSP はプラットフォームごとのコンパイルでしか動かないため、`commonMain` のクラスから生成されたコードは プラットフォームのソースセット側に置かれ、**`commonMain` のコードから呼び出せません**。

つまり、追加のセットアップなしでは「`commonMain` のクラスにアノテーションを付けて、生成された
copy 関数を `commonMain` から呼ぶ」ことができません。

下記の workaround は、cream を**metadata (common) コンパイル** で実行し、その出力を `commonMain` に追加し直すことでこれを解決します。

トレードオフ: workaround 適用後は `commonMain` だけが処理されます。プラットフォームソースセット
（`androidMain`、`jvmMain`、`iosMain` など）に置いた cream アノテーションは、プラットフォームごとの
KSP タスクが無効化されるため **処理されません**。アノテーションを付けるクラスは `commonMain` に
置いてください。またこのため、プラットフォームソースセットを処理する必要のある他の KSP plugin との併用は同じモジュールではできません。

## ワークアラウンドの設定

プロセッサを `kspCommonMainMetadata` configuration に追加し、生成されたソースを `commonMain` に
戻す配線を行います:

```kts
// module/build.gradle.kts
dependencies {
    add("kspCommonMainMetadata", "me.tbsten.cream:cream-ksp:<cream-version>")
}

fun Project.setupKspForMultiplatformWorkaround() {
    kotlin.sourceSets.commonMain {
        kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
    }

    tasks.configureEach {
        if (name.startsWith("ksp") && name != "kspCommonMainKotlinMetadata") {
            dependsOn(tasks.named("kspCommonMainKotlinMetadata"))
            enabled = false
        }
    }
}
setupKspForMultiplatformWorkaround()
```

生成ファイルは `build/generated/ksp/metadata/commonMain/kotlin/` 以下に出力されます。

補足: 上のスニペットは metadata 以外の `ksp*` タスクを **すべて無効化** するため、同じモジュールの他の KSP プロセッサ（例: kotest の KSP プラグイン）のタスクもそのままでは止まります。併用する場合は `if` の条件を調整して、他のプロセッサのタスク（例: `*Test` 系）を無効化の対象から外してください。

## 関連ドキュメント

- [Setup](../../README.ja.md#セットアップ) — 基本の（KMP でない）セットアップ
