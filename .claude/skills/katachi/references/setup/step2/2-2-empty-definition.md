### 2-2. Run once with an empty definition

```sh
mkdir -p build/katachi
./gradlew :architecture-test:test --console=plain -Dkatachi.maxViolations=all > build/katachi/test-empty-definition.log 2>&1; echo "gradle exit status: $?"
```

This runs the verification module's `test` task and keeps the full log. [run-the-check](../../run-the-check/README.md) says how to read the log and compare two of them; throughout these steps, run the check the way it says (with `-Dkatachi.maxViolations=all`, and the output in a log). Run `./gradlew` from the project root, and replace `:architecture-test` with the Gradle path of `meta.moduleDir` if it is somewhere else.

**Failing here is the expected outcome.** `architecture { }` is empty, so by the deny-by-default principle every file is reported as `Unexpected`. The log has this line (the number of violations differs).

```
Katachi check failed: 4 violations (Unexpected: 4)
```

A paragraph of the following shape follows for each violation (an example of a file and of a directory).

```
[UnexpectedFile] file:///path/to/project/.gitignore
  No role is defined for this file.

  How to fix:
    - Delete it if it is not needed
    - Add a new role for it:
        "Gitignore".role {
          layout {
            ".gitignore".file()
          }
        }

[UnexpectedDirectory] file:///path/to/project/.gemini
  No role is defined for this directory. Nothing below it was checked.

  How to fix:
    - Delete it if it is not needed
    - Declare what belongs in it in the layout of an existing role
    - Add a new role for it:
        "Gemini".role {
          layout {
            ".gemini".ignore()
          }
        }
```

katachi shows only 10 violations by default, but `-Dkatachi.maxViolations=all` makes the log hold all of them (katachi's Gradle plugin delivers it to the test JVM). This lets the comparison in 3-3 see the 11th violation onward too. Do not write `maxViolations` in the test code. If you do, that value wins and the log is cut off.

**Failing in this shape means the wiring is correct.**

The first Gradle run makes `.kotlin/` (Kotlin 2.x). If the project's `.gitignore` does not have it, `[UnexpectedDirectory] .kotlin` shows up. So does the `.gradle/` that Gradle creates. Treat them the same as the untracked files you looked for in 0-2: ask the user whether to add them to `.gitignore`. If git tracks files inside `.idea/` (such as `.idea/codeStyles/`), they are checked too, so declare them by writing a Role in step 3 (if you do not check their contents, `ignore()` is enough). Do not edit `.gitignore` yourself. Only an error that is not `Unexpected` is a problem — a compile error, a dependency resolution failure, something like `Could not start Gradle Test Executor` (the log then has neither `BUILD SUCCESSFUL` nor `Katachi check failed`; read the end of the log).

In that case, following the same policy as step 4, **attempt to fix errors unrelated to katachi (Gradle, Java, dependency resolution).** Only if it still can't be resolved, pass the output to the user exactly as it is and ask for their judgment.

If `scaffold` says "has a buildscript { } block, so it was not edited automatically," **add the single line shown in the output to the root build file.** It will keep failing until you add it. The same goes for "could not read the shape of pluginManagement { }" and "could not find where the include statement ends": add what the output shows to the settings file.

Once you have confirmed it fails with `Unexpected` only, record the files that were created or changed in the checklist's `changedFiles`, then mark `2-2` done.

```json
"changedFiles": [
  { "path": "architecture-test/build.gradle.kts", "change": "New", "summary": "Build definition for the verification module" },
  { "path": "settings.gradle.kts", "change": "Modified", "summary": "Added include(\":architecture-test\")" },
  { "path": "gradle/libs.versions.toml", "change": "Modified", "summary": "Added the katachi and JUnit aliases" }
]
```

Enter the files listed in `scaffold`'s output. The output shows them as absolute URIs starting with `file://`, but `path` takes **the path relative to the project root** (like `architecture-test/build.gradle.kts` in the JSON above). If it appended to `pluginManagement`, say so in `summary` as well.
