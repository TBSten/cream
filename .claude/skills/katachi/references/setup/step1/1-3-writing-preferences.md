### 1-3. Decide the writing preferences

Decide first the parts of the definition's style that depend on the user's preferences. They go into `preferences` of the checklist (`moduleDir` into `meta.moduleDir`), and `scaffold` and `lint` read them from there.

Show the user this table, with the "Recommended" column filled in for this project, and ask once: "answer only the ones you want to change from the recommendation". Do not ask one item at a time.

| key | Question | Values | Recommended | Read by |
|---|---|---|---|---|
| `nestedGroups` | Nest groups? | `yes` / `no` | `yes` | `lint` |
| `moduleDir` | Where the verification module goes | Path relative to the Gradle root | `architecture-test` | `scaffold` and everything after |
| `testFramework` | Test framework | `junit5` / `kotest` / `kotest-<style>` | When the project already depends on Kotest, the `kotest-<style>` its existing tests use (`kotest-free` when they extend `FreeSpec`; plain `kotest` when the styles are mixed); otherwise `junit5` | `scaffold` |
| `dependencyStyle` | How dependencies are written | `catalog` / `literal` | `catalog` if `gradle/libs.versions.toml` exists | `scaffold` |
| `constraintNames` | Put the question's number in constraint names? | `plain` / `numbered` | `plain` | 3-2 (you read it) |
| `oneRolePerFile` | Split the definition files per Role? | `yes` / `no` | `yes` | `lint` (3-3) |

`kotest-<style>` is one of `fun`, `string`, `should`, `describe`, `behavior`, `word`, `free`, `feature`, `expect`, `annotation` (`kotest-free` writes a `FreeSpec`; plain `kotest` writes a `FunSpec`).

Record the answers, the recommended values included, as strings:

```json
"preferences": {
  "nestedGroups": "yes",
  "testFramework": "kotest",
  "dependencyStyle": "catalog",
  "constraintNames": "plain",
  "oneRolePerFile": "yes"
}
```

and `moduleDir` in `meta.moduleDir`. A value outside the "Values" column makes `scaffold` stop and list the values it takes.

Then mark `1-3` done.
