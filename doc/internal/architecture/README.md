# cream.kt documentation

cream.kt is a KSP plugin that generates cross-class copy functions.
`cream-runtime` holds the annotations users write, `cream-ksp` is the processor that reads them,
and `test` is a multiplatform module that compiles real annotated classes and calls the generated functions.

## Document map

### [cream-runtime](./creamRuntime/README.md)

The multiplatform artifact users depend on: the annotations and the constants their arguments take

- [Runtime API](./creamRuntime/RuntimeApi.md)

### [cream-ksp](./creamKsp/README.md)

The KSP processor: three layers (feature → core → util) plus a composition root and its context

- [KSP option](./creamKsp/KspOption.md)

#### [Composition root](./creamKsp/compositionRoot/README.md)

The KSP entry point and the context it hands to every feature: the only files directly in `me.tbsten.cream.ksp`

- [SymbolProcessor](./creamKsp/compositionRoot/SymbolProcessor.md)
- [SymbolProcessorProvider](./creamKsp/compositionRoot/SymbolProcessorProvider.md)
- [ProcessContext](./creamKsp/compositionRoot/ProcessContext.md)
- [ServiceRegistration](./creamKsp/compositionRoot/ServiceRegistration.md)

#### [feature layer](./creamKsp/feature/README.md)

One directory per annotation: discover → extract arguments → validate → call core. No generation logic

- [Feature entry point](./creamKsp/feature/FeatureEntryPoint.md)
- [Feature diagnostics](./creamKsp/feature/FeatureDiagnostics.md)

#### [core layer](./creamKsp/core/README.md)

cream-specific code generation, split into fixed sub-packages. Nothing lives directly in `core/`

- [core/common](./creamKsp/core/CoreCommon.md)
- [core/copyFun](./creamKsp/core/CopyFunGenerator.md)
- [core/combineFun](./creamKsp/core/CombineFunGenerator.md)
- [core/sealedCopy](./creamKsp/core/SealedCopyGenerator.md)
- [core/parentOptional](./creamKsp/core/ParentOptionalGenerator.md)
- [core/error](./creamKsp/core/CreamException.md)

#### [util layer](./creamKsp/util/README.md)

他プロジェクトでも使える汎用ヘルパのみ（cream 固有型を含まない）

- [util](./creamKsp/util/GenericUtil.md)
- [util/ksp](./creamKsp/util/KspUtil.md)

### [cream-ksp tests](./creamKspTest/README.md)

JVM-only kctfork compilation tests and their shared infrastructure

- [Cross-feature test](./creamKspTest/CrossFeatureTest.md)
- [Option test](./creamKspTest/OptionTest.md)
- [core unit test](./creamKspTest/CoreUnitTest.md)
- [Snapshot golden file](./creamKspTest/SnapshotGolden.md)

#### [Feature tests](./creamKspTest/featureTest/README.md)

Every `feature/<name>/` carries the same five kinds of test, plus the curated scenarios its snapshot test uses

- [BasicUsageTest](./creamKspTest/featureTest/BasicUsageTest.md)
- [InvalidUsageTest](./creamKspTest/featureTest/InvalidUsageTest.md)
- [EdgeUsageTest](./creamKspTest/featureTest/EdgeUsageTest.md)
- [PropertyTest](./creamKspTest/featureTest/PropertyTest.md)
- [SnapshotTest](./creamKspTest/featureTest/SnapshotTest.md)
- [Scenario](./creamKspTest/featureTest/Scenario.md)

#### [Test infrastructure](./creamKspTest/testing/README.md)

テスト基盤（feature 非依存）

- [CompileHarness](./creamKspTest/testing/CompileHarness.md)
- [SnapshotAssertion](./creamKspTest/testing/SnapshotAssertion.md)
- [InputBuilder](./creamKspTest/testing/InputBuilder.md)
- [InfrastructureTest](./creamKspTest/testing/InfrastructureTest.md)
- [CaseGenerator](./creamKspTest/testing/CaseGenerator.md)
- [KotlinCodeLikeString](./creamKspTest/testing/KotlinCodeLikeString.md)
- [SmokeTest](./creamKspTest/testing/SmokeTest.md)

### [test module](./testModule/README.md)

Multiplatform integration tests: annotated classes in commonMain, run through KSP, called from commonTest

- [Integration test data](./testModule/TestData.md)
- [Integration test](./testModule/IntegrationTest.md)

### [architecture-test](./architectureTest/README.md)

This katachi definition, the one test that checks the repository against it, and the pages it generates

- [ArchitectureDefinition](./architectureTest/ArchitectureDefinition.md)
- [ArchitectureSpec](./architectureTest/ArchitectureSpec.md)
- [ArchitectureDocs](./architectureTest/ArchitectureDocs.md)

### [Build logic](./buildLogic/README.md)

The `buildLogic` included build, and the build-tool files `gradle()` does not cover

- [ConventionPlugin](./buildLogic/ConventionPlugin.md)
- [ConventionDsl](./buildLogic/ConventionDsl.md)

### [Documentation](./documentation/README.md)

User-facing documentation: English, with a Japanese counterpart (`*.ja.md`) next to each page

- [ProjectReadme](./documentation/ProjectReadme.md)
- [License](./documentation/License.md)
- [FeatureGuide](./documentation/FeatureGuide.md)
- [CustomizationGuide](./documentation/CustomizationGuide.md)
- [UseCaseGuide](./documentation/UseCaseGuide.md)
- [AiIntegrationGuide](./documentation/AiIntegrationGuide.md)
- [DistributedSkill](./documentation/DistributedSkill.md)
- [DocImage](./documentation/DocImage.md)
- [DesignDoc](./documentation/DesignDoc.md)
- [FeatureOptionMatrix](./documentation/FeatureOptionMatrix.md)

## cream-runtime

The multiplatform artifact users depend on: the annotations and the constants their arguments take

- [Runtime API](./creamRuntime/RuntimeApi.md) ... A public annotation, or the enum / name-token constants its arguments take, that users write in their own code

## cream-ksp

The KSP processor: three layers (feature → core → util) plus a composition root and its context

- [KSP option](./creamKsp/KspOption.md) ... KSP オプションのモデルとパース（`CreamOptions` / `EscapeDot` / `CopyFunNamingStrategy`）。全層から参照される横断モデル

### Composition root

The KSP entry point and the context it hands to every feature: the only files directly in `me.tbsten.cream.ksp`

- [SymbolProcessor](./creamKsp/compositionRoot/SymbolProcessor.md) ... `CreamSymbolProcessor`: `process()` で全 feature を順に dispatch する。option パース・round / deferred 集約
- [SymbolProcessorProvider](./creamKsp/compositionRoot/SymbolProcessorProvider.md) ... `CreamSymbolProcessorProvider`: KSP provider。環境から `ProcessContext` の材料を取り出す
- [ProcessContext](./creamKsp/compositionRoot/ProcessContext.md) ... `ProcessContext = {resolver, options, codeGenerator, logger}`: every feature receives it as a context parameter
- [ServiceRegistration](./creamKsp/compositionRoot/ServiceRegistration.md) ... The `META-INF/services` entry through which KSP discovers `CreamSymbolProcessorProvider`

### feature layer

One directory per annotation: discover → extract arguments → validate → call core. No generation logic

- [Feature entry point](./creamKsp/feature/FeatureEntryPoint.md) ... `Process<Name>.kt`: top-level `context(ctx: ProcessContext) internal fun processXxx(): List<KSAnnotated>`
- [Feature diagnostics](./creamKsp/feature/FeatureDiagnostics.md) ... その注釈に固有の補助: 引数 parse・注釈固有の検証 / 診断ヘルパ

### core layer

cream-specific code generation, split into fixed sub-packages. Nothing lives directly in `core/`

- [core/common](./creamKsp/core/CoreCommon.md) ... 全生成系で共有する部品: 型パラメータ・sealed 型レンダリング・プロパティマッチング・KDoc・命名・target 検証・visibility・診断ヘルパ
- [core/copyFun](./creamKsp/core/CopyFunGenerator.md) ... copy 関数生成（class / object / sealed への dispatch、Class / Object / SealedClass 生成）
- [core/combineFun](./creamKsp/core/CombineFunGenerator.md) ... combine 関数生成（N source → 1 target、CombineToClass、combine の KDoc 例）
- [core/sealedCopy](./creamKsp/core/SealedCopyGenerator.md) ... `@SealedCopy` 生成（`when(this)` 自己 copy、leaf 分類。肥大ファイルは責務単位で複数に分割）
- [core/parentOptional](./creamKsp/core/ParentOptionalGenerator.md) ... `@ParentOptional` / `@ChildOptionals` 生成（sealed 親への nullable 拡張プロパティ accessor。両 feature が共有）
- [core/error](./creamKsp/core/CreamException.md) ... 例外階層（`CreamException` / `InvalidCreamUsageException` / `InvalidCreamOptionException` / `UnknownCreamException` と `reportToGithub`）

### util layer

他プロジェクトでも使える汎用ヘルパのみ（cream 固有型を含まない）

- [util](./creamKsp/util/GenericUtil.md) ... Kotlin stdlib だけに依存するヘルパ。KSP 以外の文脈でも再利用できる
- [util/ksp](./creamKsp/util/KspUtil.md) ... KSP API 上の汎用ヘルパ（汎用範囲の KSP API のみ）

## cream-ksp tests

JVM-only kctfork compilation tests and their shared infrastructure

- [Cross-feature test](./creamKspTest/CrossFeatureTest.md) ... フィーチャー横断（複数アノテ併用 / 複数エラー同時）
- [Option test](./creamKspTest/OptionTest.md) ... option 系: パース・不正 option の診断・各 option が切り替える挙動
- [core unit test](./creamKspTest/CoreUnitTest.md) ... KSP 型に依存しない core の純ロジックのテスト（コンパイル不要）
- [Snapshot golden file](./creamKspTest/SnapshotGolden.md) ... `assertMatchesSnapshot` が比較する golden Markdown（`snapshots/<TestName>/<testCase>.md`）

### Feature tests

Every `feature/<name>/` carries the same five kinds of test, plus the curated scenarios its snapshot test uses

- [BasicUsageTest](./creamKspTest/featureTest/BasicUsageTest.md) ... 正常系 (example-based)
- [InvalidUsageTest](./creamKspTest/featureTest/InvalidUsageTest.md) ... 不正利用 → エラー (diagnostic)
- [EdgeUsageTest](./creamKspTest/featureTest/EdgeUsageTest.md) ... レアケース ＋ `@Map` / `@Exclude` 等の意味的ケース
- [PropertyTest](./creamKspTest/featureTest/PropertyTest.md) ... PBT（可視性 / エスケープ / オプションは generator 次元で網羅）
- [SnapshotTest](./creamKspTest/featureTest/SnapshotTest.md) ... generator 駆動スナップショット（`UseCase` + `All patterns`）
- [Scenario](./creamKspTest/featureTest/Scenario.md) ... curated case（family ごとの `*Scenarios()` + `UseCases.kt`）

### Test infrastructure

テスト基盤（feature 非依存）

- [CompileHarness](./creamKspTest/testing/CompileHarness.md) ... `compileWithCream`（KotlinCompilation + `useKsp2`）/ `runCompileSnapshotTest` / 結果ラッパー
- [SnapshotAssertion](./creamKspTest/testing/SnapshotAssertion.md) ... golden 比較（`assertMatchesSnapshot` / facet）
- [InputBuilder](./creamKspTest/testing/InputBuilder.md) ... 入力ビルダー（`Prop` / `clazz` / `sealedInterface` / `SnapshotScenario` …)
- [InfrastructureTest](./creamKspTest/testing/InfrastructureTest.md) ... A unit test of the test infrastructure itself, next to what it tests
- [CaseGenerator](./creamKspTest/testing/CaseGenerator.md) ... generated case 基盤（`Generator` / `union` / `cartesian` / `validCreamOptions`）
- [KotlinCodeLikeString](./creamKspTest/testing/KotlinCodeLikeString.md) ... `CreamOptions` → ksp config 文字列などの表示ヘルパ
- [SmokeTest](./creamKspTest/testing/SmokeTest.md) ... 基盤が動くことの最小確認

## test module

Multiplatform integration tests: annotated classes in commonMain, run through KSP, called from commonTest

- [Integration test data](./testModule/TestData.md) ... Annotated classes cream generates code from, one directory per feature
- [Integration test](./testModule/IntegrationTest.md) ... A kotest spec calling the generated functions: `<feature>/<Name>Test.kt`, named after the data it uses

## architecture-test

This katachi definition, the one test that checks the repository against it, and the pages it generates

- [ArchitectureDefinition](./architectureTest/ArchitectureDefinition.md) ... The `architecture { }` definition: one file per group (`<group>/<Group>.kt`) and per role (`<group>/<role>/<Role>.kt`)
- [ArchitectureSpec](./architectureTest/ArchitectureSpec.md) ... The single test calling `projectArchitecture.assert(FileConstraintCheck())`
- [ArchitectureDocs](./architectureTest/ArchitectureDocs.md) ... Pages generated by `katachiDocs` from this definition. Not written by hand

## Build logic

The `buildLogic` included build, and the build-tool files `gradle()` does not cover

- [ConventionPlugin](./buildLogic/ConventionPlugin.md) ... A convention plugin class, applied with `id("buildLogic.xxx")`
- [ConventionDsl](./buildLogic/ConventionDsl.md) ... Internal helpers the convention plugins share: version catalog and extension accessors

## Documentation

User-facing documentation: English, with a Japanese counterpart (`*.ja.md`) next to each page

- [ProjectReadme](./documentation/ProjectReadme.md) ... The README shown on GitHub, in English and Japanese
- [License](./documentation/License.md) ... The MIT license text
- [FeatureGuide](./documentation/FeatureGuide.md) ... One page per feature or topic (copy, combine, sealed copy, comparison, ...), linked from the README
- [CustomizationGuide](./documentation/CustomizationGuide.md) ... One page per customization point (options, funName, KDoc, visibility, ...)
- [UseCaseGuide](./documentation/UseCaseGuide.md) ... A worked example of cream in real code; the snapshot tests reproduce it in `scenario/UseCases.kt`
- [AiIntegrationGuide](./documentation/AiIntegrationGuide.md) ... How to use cream from AI agents
- [DistributedSkill](./documentation/DistributedSkill.md) ... An agent skill users copy into their own projects. English only, since agents read it
- [DocImage](./documentation/DocImage.md) ... An image embedded in the documentation
- [DesignDoc](./documentation/DesignDoc.md) ... An internal design note, one per issue (`<issue number>-<topic>.md`)
- [FeatureOptionMatrix](./documentation/FeatureOptionMatrix.md) ... The internal reference of which option affects which feature: the Markdown source and its rendered HTML
