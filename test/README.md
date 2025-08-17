# Cream Test Module

このモジュールは、Creamライブラリのテストコードを含むモジュールです。

## ディレクトリ構造

```
test/
├── src/
│   ├── commonMain/          # テスト対象となるクラス定義
│   │   └── kotlin/me/tbsten/cream/test/
│   │       ├── copyFrom/    # CopyFromアノテーションのテストケース
│   │       │   ├── CopyFromClasses.kt
│   │       │   └── edgeCase/
│   │       │       └── CopyFromEdgeCaseClasses.kt
│   │       ├── copyTo/      # CopyToアノテーションのテストケース
│   │       │   ├── CopyToClasses.kt
│   │       │   └── edgeCase/
│   │       │       └── CopyToEdgeCaseClasses.kt
│   │       ├── copyToChildren/  # CopyToChildrenアノテーションのテストケース
│   │       │   ├── CopyToChildrenClasses.kt
│   │       │   └── edgeCase/
│   │       │       └── CopyToChildrenEdgeCaseClasses.kt
│   │       └── generic/     # ジェネリクスのテストケース
│   │           └── GenericClasses.kt
│   └── commonTest/          # テストコード
│       └── kotlin/me/tbsten/cream/test/
│           ├── copyFrom/    # CopyFromのテスト
│           │   ├── CopyFromTest.kt
│           │   └── edgeCase/
│           │       └── CopyFromEdgeCaseTest.kt
│           ├── copyTo/      # CopyToのテスト
│           │   ├── CopyToTest.kt
│           │   └── edgeCase/
│           │       └── CopyToEdgeCaseTest.kt
│           ├── copyToChildren/  # CopyToChildrenのテスト
│           │   ├── CopyToChildrenTest.kt
│           │   └── edgeCase/
│           │       └── CopyToChildrenEdgeCaseTest.kt
│           └── generic/     # ジェネリクスのテスト
│               └── GenericClassTest.kt
```

## テストコード記述のお作法

### 1. ディレクトリ構成の原則

- **commonMain**: テスト対象となるクラス定義を配置
- **commonTest**: 実際のテストコードを配置
- **edgeCase**: エッジケースや特殊なケースのテストを配置
- 各機能（copyFrom、copyTo、copyToChildren、generic）ごとにディレクトリを分離

### 2. テストクラスの命名規則

- テスト対象クラス: `{機能名}Classes.kt`
- テストコード: `{機能名}Test.kt`
- エッジケース: `{機能名}EdgeCaseClasses.kt` / `{機能名}EdgeCaseTest.kt`

### 3. テストコードの記述パターン

#### 基本的なテスト構造

```kotlin
class {機能名}Test {
    @Test
    fun {テストケース名}() {
        // テストデータの準備
        val source = SourceClass(...)
        
        // 期待される結果
        val expected = TargetClass(...)
        
        // 実際の結果
        val actual = source.copyToTargetClass(...)
        
        // アサーション
        assertEquals(expected, actual)
    }
}
```

#### 複数のテストケースを効率的に記述するパターン

```kotlin
@Test
fun {テストケース名}() {
    val source = SourceClass(...)
    
    mapOf(
        source.copyToTarget1() to ExpectedTarget1(...),
        source.copyToTarget2(...) to ExpectedTarget2(...),
    ).forEach { (actual, expected) ->
        assertEquals(expected, actual)
    }
}
```

### 4. アサーションの記述

- **assertEquals(expect, actual)** の順序で記述（プロジェクト全体のルール）
- 期待値（expected）を先に、実際の値（actual）を後に記述

### 5. エッジケースのテスト

エッジケースは以下のような状況をカバー：

- **データオブジェクト間のコピー**: `data object` から `data object` への変換
- **null安全性**: 非nullからnullableプロパティへの変換
- **ネストしたクラス**: 複雑なオブジェクト構造のコピー
- **可視性**: 異なる可視性を持つプロパティの処理
- **プロパティマッピング**: `@CopyFrom.Map` を使用したカスタムマッピング
- **複雑な型**: List、Map、Setなどのコレクション型
- **null値の処理**: nullableプロパティがnullの場合の動作

### 6. ジェネリクスのテスト

ジェネリクスクラスのテストでは：

- 型パラメータの制約を考慮したテストケース
- 型マッピング（`@CopyFrom.Map`、`@CopyTo.Map`）の動作確認
- 異なる型引数の数での変換テスト

### 7. テストデータの準備

- 意味のあるテストデータを使用（例：`"test string"`, `42`, `true`）
- 空の値や境界値もテストに含める
- 複雑なオブジェクト構造も適切にテスト

### 8. コメントとドキュメント

- テストクラスにはKDocコメントを記述
- プロパティの説明を`@property`タグで記述
- 複雑なテストケースには適切なコメントを追加

### 9. 依存関係

- `kotlin.test` を使用したテストフレームワーク
- `kotest` ライブラリの利用
- KSP（Kotlin Symbol Processing）によるコード生成のテスト

## 注意事項

- エッジケースのテストは必ず `edgeCase/` ディレクトリに配置
- テストコードは英語でコメントを記述（プロジェクト全体のルール）
- アサーションは `assertEquals(expect, actual)` の順序で記述
- 複雑なテストケースは適切に分割し、可読性を保つ
