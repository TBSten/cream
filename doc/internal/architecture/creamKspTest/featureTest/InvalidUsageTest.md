[cream.kt](../../README.md) / [cream-ksp tests](../README.md) / [Feature tests](README.md)

# InvalidUsageTest

不正利用 → エラー (diagnostic)

不正 annotation / 不正 option は exit code を `result.exitCode shouldNotBe KotlinCompilation.ExitCode.OK` で確認し、
`normalizedCompilerOutput()` を `*.output.md` golden と突き合わせる（`MultipleDiagnosticsTest` も同じ）。

`normalizedCompilerOutput()` は以下を置換 / 集約してから返す:

- `java.io.tmpdir` → `<TMPDIR>`
- `Kotlin-CompilationNNN` → `Kotlin-Compilation<N>`
- 連続する `\tat ...` および `\t... NN more`（stack-trace フレーム）→ 1 行の `\t<stack trace omitted>`

stack frame は Gradle / JUnit / KSP / cream 自身の line 変更や `... NN more` の depth count で簡単にずれるため、
diagnostic snapshot で固定化しているのは「error message 本文」だけ。特定 frame の存在を assert したいなら、snapshot とは別に
`result.messages shouldContain "..."` を併用する。

## Placement

| Path | When to use |
|---|---|
| `cream-ksp/src/test/kotlin/me/tbsten/cream/ksp/feature/*/*InvalidUsageTest.kt` |  |

## Constraints

- declares the package its directory spells
- is named after its feature directory
- is an internal kotest FreeSpec

## Examples

- `CopyToInvalidUsageTest`
