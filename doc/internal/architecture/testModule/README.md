[cream.kt](../README.md)

# test module

Multiplatform integration tests: annotated classes in commonMain, run through KSP, called from commonTest

| Role | Summary |
|---|---|
| [Integration test data](./TestData.md) | Annotated classes cream generates code from, one directory per feature |
| [Integration test](./IntegrationTest.md) | A kotest spec calling the generated functions: `<feature>/<Name>Test.kt`, named after the data it uses |

## Placement in this group

```
test/src/
  commonMain/kotlin/me/tbsten/cream/test/*/**/*.kt   Integration test data
  commonTest/kotlin/me/tbsten/cream/test/*/*Test.kt  Integration test
```
