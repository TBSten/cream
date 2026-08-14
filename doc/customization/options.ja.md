[← README](../../README.ja.md) | [English](./options.md)

# KSP オプション

生成される copy 関数をカスタマイズするためのいくつかの KSP オプションが用意されています。
すべてのオプションの設定は任意です。必要に応じてモジュールの `build.gradle.kts` の
`ksp { arg(...) }` で設定してください:

```kts
// module/build.gradle.kts

ksp {
    arg("cream.copyFunNamePrefix", "copyTo")
    arg("cream.copyFunNamingStrategy", "under-package")
    arg("cream.escapeDot", "replace-to-underscore")
    arg("cream.notCopyToObject", "false")
    arg("cream.defaultVisibility", "INHERIT")
    arg("cream.autoValueClassMapping", "true")
}
```

各オプションの動作を確認するためには [Option Builder](https://tbsten.github.io/cream/option-builder) が便利です。

## オプション索引

各オプションの詳細は、それぞれのトピックページに記載しています:

| オプション名                        | 説明                                                                                                 | デフォルト          | 詳細                                                                              |
|-----------------------------------|--------------------------------------------------------------------------------------------------------|--------------------|-------------------------------------------------------------------------------------|
| **`cream.copyFunNamePrefix`**     | 生成されるコピー関数の先頭につく文字列 (`copyTo`, `transitionTo`, `to`, `mapTo` など)                     | `copyTo`           | [Function name (funName)](./fun-name.ja.md#creamcopyfunnameprefix)                |
| **`cream.copyFunNamingStrategy`** | コピー関数の命名方法 (`under-package`, `diff`, `simple-name`, `full-name`, `inner-name`)                | `under-package`    | [Function name (funName)](./fun-name.ja.md#creamcopyfunnamingstrategy)            |
| **`cream.escapeDot`**             | `cream.copyFunNamingStrategy` で命名された名前に含まれる `.` をエスケープする方法 (`lower-camel-case`, `replace-to-underscore`) | `lower-camel-case` | [Function name (funName)](./fun-name.ja.md#creamescapedot)                        |
| **`cream.notCopyToObject`**       | `true` の場合 `@CopyToChildren` で `object` へのコピー関数を生成しないようにします                        | `false`            | [@CopyToChildren](../copy-to-children.ja.md)                                       |
| **`cream.defaultVisibility`**     | 生成される関数のモジュール全体のデフォルト可視性。アノテーションの `visibility` が `INHERIT` の場合に適用されます | `INHERIT`          | [Visibility](./visibility.ja.md#モジュール全体のデフォルト-creamdefaultvisibility)       |
| **`cream.autoValueClassMapping`** | `false` の場合、名前が一致するプロパティの自動 `value class` ラップ/アンラップを無効化します（issue #21）。対象の引数は必須引数のままになります。無効化できるのはリテラル `"false"`（大文字小文字を区別しない）だけです | `true`             | [Value class mapping](./value-class-mapping.ja.md)                                 |

## 関連ドキュメント

- [Function name (funName)](./fun-name.ja.md) — 命名オプションの詳細 + 宣言ごとの `funName` による上書き
- [Visibility](./visibility.ja.md) — `cream.defaultVisibility` とアノテーションごとの `visibility` 引数
- [@CopyToChildren](../copy-to-children.ja.md) — `cream.notCopyToObject` (モジュール全体) とアノテーションの `notCopyToObject` プロパティ
- [Value class mapping](./value-class-mapping.ja.md) — `cream.autoValueClassMapping`（自動ラップ/アンラップのモジュール全体でのオプトアウト）
