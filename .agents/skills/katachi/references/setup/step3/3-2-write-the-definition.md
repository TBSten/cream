### 3-2. Write the definition

**The goal is a definition that runs.** Writing it in one file is fine. Splitting files and cleaning up duplication happen in 3-3. When unsure about an API, read the llms.txt in "When unsure about an API" in 3-1.

**These are the imports you will need.** The documentation's snippet examples do not show them, so they are here.

```kt
import me.tbsten.katachi.dsl.architecture
import me.tbsten.katachi.dsl.DeclarationContainerScope   // when splitting the definition into functions
import me.tbsten.katachi.dsl.FileConstraintFailure       // when writing fileConstraint (an experimental API)
import me.tbsten.katachi.dsl.gradle.*                    // mainSourceSet / testSourceSet / kotlin / gradle()
import me.tbsten.katachi.dsl.kotlin.ktFile
import me.tbsten.katachi.dsl.kotlin.ktsFile
import me.tbsten.katachi.konsist.declarations.ktFileContent   // when writing ktFileContent { } (an experimental API)
```

`fileConstraint` itself needs no import (it is a member of the scopes you call it in); the `FileConstraintFailure` its check returns does, and so do `FileConstraintReason` and `FileConstraintFix` from the same package if you use them.

**Star-import `me.tbsten.katachi.dsl.gradle`:** one line brings `mainSourceSet`, `testSourceSet`, `kotlin` and `gradle()`. Importing them one by one also works (`/` needs no import).

**`capture()`, `fileConstraint`, `FileConstraintFailure` and `ktFileContent { }` are experimental APIs.** A file that uses any of them starts like this:

```kt
@file:OptIn(ExperimentalKatachiApi::class)

import me.tbsten.katachi.ExperimentalKatachiApi
```

**Do not rename `val projectArchitecture` or move it to another package.** `katachi { architecture = "..." }` in the verification module's `build.gradle.kts` points at that fully qualified name. Splitting the definition into functions or files is fine (3-3).

You can write `title = "..."` and `description = "..."` at the top of `architecture { }`. They become the heading and body of the root page of the documentation generated in step 4 (they cannot be set from `build.gradle.kts`). Take their content from `overview` / `architecture` in the step 1 report.

#### One Role = one kind of file

**A Role (`"Name".role { }`) stands for one kind of file. What bundles different kinds of files is a group (`"Name".group { }`).** Pack different kinds of files into one Role and neither its `summary` nor its `ktFileContent { }` fits all of them any more, and the generated documentation shows a box that anything goes into.

**Name Roles and groups alike in UpperCamelCase** (`"ViewModel".role { }`, `"Feature".group { }`, `"BuildLogic".group { }`), as katachi's own `Gradle` group (from `gradle()`) is. Do not start a group name with a lowercase letter (`"feature".group { }`): the name shows up as it is in the generated documentation and in `katachiTemplate`'s `template=Group.Role`, next to Role names that are UpperCamelCase. Only the directories of a split definition start lowercase (3-3). `katachi lint` lists names that start lowercase.

Decide whether files belong in the same Role with these three questions. If any file gets a "no", split the Role.

- Does the Role's one-sentence `summary` describe every file?
- Does every file follow the same naming and placement rule (rather than exceptions listed one by one)?
- Does every `ktFileContent { }` constraint you would write for the Role apply to every file?

The other way round, **it is not one Role per file either.** Eighteen `*UseCase.kt` files are one Role, `UseCase`. The snippet a violation's `How to fix:` offers names the Role after the file (`"GetUserUseCase"` and so on), so do not paste it as is — rename it to the kind.

Files directly under the root also get one Role per kind (README, LICENSE, `.gitignore`, …), never one Role for all of them. Write the file directly under its Role's `layout { }` (`"README.md".file()`): no directory is needed.

**For the Gradle files (the wrapper, settings, each module's build script, `gradle.properties`, the version catalog), do not write Roles yourself: write the one line `gradle()`.** katachi declares a `Gradle` group split into one Role per kind of file (it comes with the star import of `me.tbsten.katachi.dsl.gradle.*`). Only `buildSrc` and builds added with `includeBuild`, which it does not cover, need Roles of your own. The four wrapper files (`gradlew`, `gradlew.bat`, `gradle/wrapper/gradle-wrapper.jar`, `gradle/wrapper/gradle-wrapper.properties`) are required, and a missing one is `[MissingFile]`. When the project has no wrapper, or keeps only part of it, write `gradle(requireWrapper = false)` instead.

When you want to check the contents of a build file (the ones marked `constraintOnly` in the report), you do not write a Role either. Write the constraint in a hook of the `gradle()` block, as in `gradle { buildScript { fileConstraint("...") { subject -> ... } } }` (`settingsScript { }` for settings, `versionCatalog { }` for the version catalog). `fileConstraint` and `FileConstraintFailure` are experimental APIs, so the file starts with the two lines in the "experimental APIs" paragraph above, and it imports `me.tbsten.katachi.dsl.FileConstraintFailure` (the import list at the top). A `buildScript { }` constraint receives the `build.gradle(.kts)` of every module, the root included, in `subject.files`. To look at one module only, narrow `subject.files` by path inside the constraint (for example `subject.files.filter { it == "app/build.gradle.kts" }`). Writing a Role inside `gradle { }` with the same name as one of katachi's (`"BuildScript".role { }` and so on) raises a duplicate exception. A Role outside `gradle { }` that declares a build file claims the same file as katachi's `BuildScript` and becomes `[AmbiguousLayout]`.

<details>
<summary>Common mistakes and how to fix them</summary>

In the field tests of the install kit, these shapes turned up in 13 projects.

| Mistake (packed into one Role) | Fix |
|---|---|
| `Gradle` holding `settings.gradle.kts`, `gradle.properties`, `gradlew`, the version catalog and the wrapper — and then `.gitignore` or even `README.md` | Replace the Gradle files with the one line `gradle()`, and give `.gitignore` and `README.md` a Role each |
| `ProjectMeta` / `RootDoc` holding `README`, `LICENSE`, `CONTRIBUTING`, `CLAUDE.md` | One Role per kind — `ProjectReadme` / `License` / … — bundled in a group. A Role named `Readme` gets a documentation page whose name differs from the index `README.md` only in case, and `katachiDocs` fails |
| `AgentAndCiConfig` holding `.claude/`, `.github/`, `.run/` | One Role per tool (if you do not check their contents, `ignore()` in each is fine) |
| `AndroidApp` holding a whole module's contents (Manifest, Activity, `res/`, `build.gradle.kts`) | Make the module a group, with a Role per kind inside |
| Production code and its tests in the same Role | Put the tests in a Role of their own |

```kt
// ✗ Different kinds of files packed into one Role
"ProjectMeta".role {
  layout {
    "README.md".file()
    "LICENSE".file()
    ".gitignore".file()
  }
}

// ○ One Role per kind, bundled in a group
"Meta".group {
  "ProjectReadme".role { layout { "README.md".file() } }
  "License".role { layout { "LICENSE".file() } }
  "GitIgnore".role { layout { ".gitignore".file() } }
}
```

</details>

#### Do not stop at placement

**Take the conventions you read in step 1 and go through them as candidates for constraints on the contents (`ktFileContent { }`).** A definition that only says where files go only ever tells you that a file is in the right place. `ktFileContent { }` is an experimental API, so the file that uses it starts with the two lines in the "experimental APIs" paragraph above. How to write it is on the `kt-file-content` page (3-1).

**Do not write catch-all Roles.** A layout like `commonMain/**/*.kt` — "anything, as long as it is here" — looks like a check and is not one. In a real adoption this let **a test for the same responsibility be created somewhere else entirely, and accepted.** If a Role would end up a catch-all, give it at least one of the following.

- a pattern in the file name (`"*ViewModel".ktFile()`)
- one constraint on the contents (what a file of this Role declares; write it with `ktFileContent { }`)

A constraint on a file's imports goes in the `import` entries of `ktFileContent { }`. Only the `import`s you write are allowed, so "do not import this" is expressed by not listing that import. Many of a project's conventions are of this form.

```kt
"ViewModel".role {
  layout { /* ... */ }

  ktFileContent {
    `package`("com.example.viewModel")

    // android.** is not listed. Importing it is a violation
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

Constraint names follow `constraintNames` from 1-3. With `numbered`, prefix the name with the id of the question the constraint came from (`q3`, `cq2` and so on), as in `"q3: Does not import the android package".ktFileContent { }`. With `plain`, write the name without the id (`"Does not import the android package".ktFileContent { }`).

#### Dividing the work with the static analysis already there

The project may already run detekt, Android Lint, ktlint or a compiler plugin of its own. **Do not drop a constraint because something else covers it.** In a real adoption the agent leaned so hard on avoiding duplication that it under-constrained, and **the only convention violation actually caught came from the existing checker.**

Decide like this.

- **A convention that belongs to a Role goes into katachi.** "A ViewModel never calls a repository directly" *is* the definition of that Role. Another tool checking something similar does not change that — having it here keeps "what this Role is" in one place
- **The only safe thing to drop is a rule an existing tool already fails the build on, with no per-file suppression.** If it can be suppressed file by file, katachi's copy still earns its place
- When unsure, write it. A constraint is easy to delete later

#### The verification module needs Roles too

The verification module is checked like any other module. Its `build.gradle.kts` is covered by `gradle()`, but the definition files and the test under `<verification module>/src/test/kotlin/` need a Role, or they come back as `[UnexpectedFile]` (3-3 assumes they have one). Give them a group of their own, with `documented = false` (they are tool configuration, as in 1-2):

```kotlin
"ArchitectureTest".group {
    documented = false
    "ArchitectureDefinitionEntry".role {
        summary = "The entry of the katachi definition: the architecture { } that gathers every group"
        layout {
            "architecture-test" / testSourceSet / kotlin / "**" / "ProjectArchitecture".ktFile()
        }
    }
    "ArchitectureDefinitionTest".role {
        summary = "The test that runs the katachi definition"
        layout {
            "architecture-test" / testSourceSet / kotlin / "**" / "ProjectArchitectureTest".ktFile()
        }
    }
    "RoleDefinition".role {
        summary = "One <Name>Role.kt per Role, split out of architecture { }"
        layout {
            "architecture-test" / testSourceSet / kotlin / "**" / "*Role".ktFile()
        }
    }
    "GroupDefinition".role {
        summary = "One <Name>Group.kt per group, split out of architecture { }"
        layout {
            "architecture-test" / testSourceSet / kotlin / "**" / "*Group".ktFile()
        }
    }
}
```

Read `"architecture-test"` as `meta.moduleDir`. One Role per kind of file, as everywhere else: the entry `ProjectArchitecture.kt`, the `ProjectArchitectureTest.kt` that scaffold writes, and the `<Role>Role.kt` / `<Group>Group.kt` files that 3-3 splits the definition into (the `"**"` takes the group directories). The name patterns keep these Roles from being catch-alls ("Do not stop at placement"). A file of another shape there (a helper with shared `val`s, say) gets a Role of its own rather than a `"*"`.

If scaffold created `<verification module>/.gitignore` (it does when the project's `.gitignore` does not already ignore `build/`), that file needs a Role too, or step 4 reports it as `[UnexpectedFile]`. Declare it next to the root `.gitignore`, for example `"**/.gitignore".file()` in the `GitIgnore` Role.

Fill in the `architecture { }` in `<verification module>/src/test/kotlin/<package>/test/architecture/ProjectArchitecture.kt`.

#### Run it and save the result

Once written, run it once and **save the output as the reference log before refactoring** (not to be confused with katachi's `baseline()` in 4-2). 3-3 compares against it.

```sh
mkdir -p build/katachi
./gradlew :architecture-test:test --console=plain -Dkatachi.maxViolations=all > build/katachi/test-before-refactor.log 2>&1; echo "gradle exit status: $?"
```

([run-the-check](../../run-the-check/README.md) says how to read the log.)

What you need here is **for it to compile and for the check to run to the end.** Failing with `Katachi check failed: ...` is fine (violations are dealt with in step 4). If a compile error or a Gradle error keeps the check from running, fix it and run it again.

Record the files you created or changed in `changedFiles` (the split files can be one entry by directory or wildcard; files already recorded in 2-2 need not be recorded again), mark `3-2` done, and run:

```sh
katachi lint
```

It scans the definition and lists the Roles that look like they hold different kinds of files. What it lists is dealt with in the 3-3 checklist.
