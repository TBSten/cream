# Add a Role for a new kind of file

## Decide: a new Role, or an existing one?

A file goes into an existing Role only if all three hold. If one answer is "no", it needs a new Role.

- The Role's `summary`, one sentence, describes this file too
- The file follows the same naming and placement rule as the Role's other files (not an exception you list)
- Every constraint written on the Role (`ktFileContent { }` and the like) applies to this file too

Not one Role per file either: when a project gains its 19th `*UseCase.kt`, it is the existing `UseCase` Role, and its `layout { }` already matches it. If the new file is not matched, check why before adding anything: a file in an unexpected place may be the thing to fix (ask the user), not the layout.

The `How to fix:` of an `[UnexpectedFile]` suggests a Role named after the file (`"GetUserUseCase".role { … }`). Do not paste it as it is; name the Role after the kind of file. For a file the OS writes on its own (`.DS_Store`, `Thumbs.db`, `desktop.ini`) it suggests `.gitignore` instead of a Role: follow that, and add no Role.

## Where the Role goes

- **In the group the file belongs to.** Groups are nested "module → layer → kind" (for example `Feature` → `Ui` → `ViewModel`; group names are UpperCamelCase like Role names). A new kind of module is usually a new group holding one Role per kind of file in it.
- **In a file of its own, if the definition is split that way.** The naming setup uses (unless the project does something else, which you follow):

  ```
  <package>/test/architecture/
  ├── ProjectArchitecture.kt            only calls the group functions
  ├── <Role>Role.kt                     a Role outside any group
  ├── <group>/<Group>Group.kt           a group
  ├── <group>/<Role>Role.kt             a Role the group calls
  └── <group>/<sub>/<Sub>Group.kt       a nested group
  ```

  Split functions are extension functions on `DeclarationContainerScope` (`fun DeclarationContainerScope.viewModel() { "ViewModel".role { … } }`), not `inline`, and the group's file calls them. Import a function where it is called; an extension function cannot be called by its fully qualified name.
- **Not under a name that `.gitignore` matches** (a group called `Build` or `Out`, whose directory becomes `build/` or `out/`). An ignored definition file is never checked, and nothing tells you.
- **The verification module's own Role must still accept the new file.** The definition files are checked too: the Role that covers `src/test/kotlin/**` has to reach as deep as the deepest definition file.

## What the Role says

The example leaves out the file's `package` and `import` lines, which the definition file already has.

```kt
"Worker".role {
  summary = "A WorkManager worker that runs one background job"
  description = "One class per file, named after the job, extending CoroutineWorker."
  example("app/src/main/kotlin/com/example/app/work/SyncWorker.kt")
  layout {
    "app" / mainSourceSet / kotlin / "com/example/app/work" / "*Worker".ktFile()
  }
}
```

- **A file name pattern, not "anything here".** `"*Worker".ktFile()` says what the Role is; `"work" / "*.kt".file()` accepts any Kotlin file that lands in the directory, and the next unrelated file will be accepted silently. If a pattern is not possible, add a constraint on the contents ([add-a-constraint.md](add-a-constraint.md)).
- **Gradle files are not Roles.** Build scripts, settings, `gradle.properties` and the version catalog are covered by the single line `gradle()`. A rule about a build file goes in a hook such as `gradle { buildScript { fileConstraint("…") { … } } }` ([add-a-constraint.md](add-a-constraint.md)). Only `buildSrc` and builds added with `includeBuild` get hand-written Roles.
- **Files nobody reads in the generated documentation** (generated output, tool configuration such as CI yaml or lint settings, a single README or LICENSE) get `documented = false`.
- Reuse what is already there: if the new Role's layout repeats a directory other Roles use, there is probably a `val` or a function for it already.

## Then

Run the loop in [README.md](README.md) from step 4: the new files are accepted, nothing else changed, a stray file in the same place still fails, `katachi lint` is quiet, the descriptions are written.

If the new Role has many files of the same shape (a ViewModel per screen, a UseCase per action), suggest a template to the user ([use-templates.md](use-templates.md)). Do not add one without asking.
