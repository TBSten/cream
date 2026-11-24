# Add a rule the team wants enforced

Placement says where a file is. Many conventions are about what is inside it: "a ViewModel does not import `android.*`", "a repository implementation is `internal`", "every feature module's namespace matches its package". These are constraints on contents.

Read the documentation before writing one: https://tbsten.github.io/katachi/guides/kt-file-content/ . For signatures, the API reference: https://tbsten.github.io/katachi/api-docs/katachi-konsist/llms.txt .

## Where the rule goes

| The rule is about | Write it as |
|---|---|
| What a Kotlin file of one Role may declare or import | `ktFileContent { }` on that Role (from `katachi-konsist`) |
| A build file (`build.gradle.kts`, settings, the version catalog) | `fileConstraint("…") { subject -> … }` in a `gradle { }` hook: `buildScript { }`, `settingsScript { }`, `versionCatalog { }` |
| Any other file's contents | `fileConstraint("…") { subject -> … }` in the Role (`"X".role { fileConstraint(…) { … } }`) |

`ktFileContent { }`, `fileConstraint` and `FileConstraintFailure` are experimental: a file that uses one starts with `@file:OptIn(ExperimentalKatachiApi::class)` and `import me.tbsten.katachi.ExperimentalKatachiApi`. Two of them also need an import of their own: `import me.tbsten.katachi.konsist.declarations.ktFileContent` for `ktFileContent { }`, and `import me.tbsten.katachi.dsl.FileConstraintFailure` for the failures a `fileConstraint` returns (`fileConstraint` itself needs none).

`ktFileContent { }` is allow-list based: the imports and declarations you list are what is allowed, and anything else is a violation. "Must not import X" is written by listing what may be imported and leaving X out.

```kt
"ViewModel".role {
  layout { /* … */ }

  ktFileContent {
    `package`("com.example.viewModel")

    // android.** is not listed, so importing it is a violation
    `import`("kotlin.**")
    `import`("kotlinx.coroutines.**")
    `import`("androidx.lifecycle.**")
    `import`("com.example.**")

    required(`class`, fileName) {
      allow(anythingElse)
    }
  }
}
```

A `buildScript { }` constraint receives the build files of every module in `subject.files`; to look at one module, filter them by path inside the constraint (`subject.files.filter { it == "app/build.gradle.kts" }`).

Name the constraint after the rule (`"no android imports in ViewModel".ktFileContent { … }`), so a violation says which rule it broke.

## Do not skip a rule because another tool has it

If detekt, ktlint, Android Lint or a compiler plugin checks something similar, the rule still belongs in katachi when it is part of what a Role is ("a ViewModel does not call a repository directly" defines the ViewModel Role). Leave it out only when the other tool already fails the build and cannot be suppressed per file.

## Then

Run the check. A new rule often finds existing code that breaks it. Do not loosen the rule to get to 0 violations, and do not rewrite the code without asking: list the violations for the user, with what each would take to fix. If they want to fix them later, the baseline can hold the current ones ([../fix-violations/README.md](../fix-violations/README.md), "Tolerating what is there now").

Then make sure the rule really fires: with a green result, write one temporary file that breaks it, see the violation, and delete the file.
