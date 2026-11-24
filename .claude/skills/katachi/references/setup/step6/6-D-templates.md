### 6-D. (After reporting in 6) Set up code generation from templates

Do this only for the Roles, among the proposals recorded in step 6, that the user agreed to.

**Before you start, read the page on generating code from a template.** It is at https://tbsten.github.io/katachi/guides/generate-code-from-template/ . The words `.template { }` offers (the `*Parameter()` functions such as `stringParameter()`), why you don't write the destination path, and what happens when a file already exists are all there. When unsure about a signature, read the llms.txt in "When unsure about an API" in 3-1 instead of guessing (the entry point for `.template { }` is https://tbsten.github.io/katachi/api-docs/katachi/me.tbsten.katachi.dsl/template.html.md ).

1. Check the proposals (`templates` in the report).
2. Attach `.template { }` to a file declaration in that Role's `layout { }`. **`.template` is an extension function, so add `import me.tbsten.katachi.dsl.template` to the file you write it in** (`captureValue()` and the like can be used inside the block, so they need no import). **`.template { }`, `capture()` and `captureValue()` are experimental APIs, so that file also starts with `@file:OptIn(ExperimentalKatachiApi::class)` and `import me.tbsten.katachi.ExperimentalKatachiApi`.** The examples below show only the Role, without those lines and the imports. **Base its content on the `basedOn` file.** The destination path comes from the `layout { }` declaration itself, so don't write it on the template's side

   ```kt
   "UseCase".role {
     layout {
       "domain/src/main/kotlin/com/example/app/domain/useCase" / "${capture("name")}UseCase".ktFile()
         .template {
           val name = captureValue("name")
           """
             package com.example.app.domain.useCase

             interface ${name}UseCase {
               suspend operator fun invoke()
             }
           """.trimIndent()
         }
     }
   }
   ```

   **For a Role whose destination directory has a wildcard (`*`), name that `*` with `capture()`.** A `capture()` used once accepts the same files as the `*` it replaces, so the check's result doesn't change. Using the same name in two places is different: it adds the condition that both places have the same value (see "Name every `*`" below). Attaching `.template { }` to a path that still has an unnamed `*` or `**` fails when layout is expanded (`assert()` when the check runs). Read the value inside the template with `captureValue("name")` (don't redeclare it with `stringParameter()` — the names collide and it fails)

   ```kt
   "ViewModel".role {
     layout {
       // was "feature" / "*" / mainSourceSet / kotlin / "com/example/app/feature" / "*" / "*ViewModel".ktFile()
       "feature" / capture("feature") / mainSourceSet / kotlin /
         "com/example/app/feature" / capture("feature") /
         "${capture("name")}ViewModel".ktFile()
           .template {
             val feature = captureValue("feature")   // "home" for --arg feature=home
             val name = captureValue("name")         // "Home" for --arg name=Home

             """
               package com.example.app.feature.$feature

               class ${name}ViewModel
             """.trimIndent()
           }
     }
   }
   ```

   For a directory's `*`, rewrite the `"*"` in `"feature" / "*" / ...` as `capture("feature")`. With more than one `*`, write a `capture()` for each, as in `"feature" / capture("feature") / capture("layer") / ...`.

   **Name every `*` left in the directory part of the destination.** If the module directory's `*` and the package directory's `*` get the same value, as in the ViewModel example above (`"feature" / capture("feature") / … / "com/example/app/feature" / capture("feature")`), write one name (`capture("feature")`) in both. katachi checks that the two places have the same value, and reports a file where they differ as `[CaptureMismatch]`. If the values may differ, give each its own name. If even one unnamed `*` is left, an error occurs when layout is expanded for the file with `.template { }` attached (`assert()` catches it; it doesn't wait for `katachiTemplate` to run).

   **If the package directory's name differs from the module directory's name (module `appConfig` and directory `appconfig`, for example), give that directory's `*` its own name, separate from the module's, and use its `captureValue()` in the package.** If you put the module's name (`captureValue("feature")`) in the package, you write a package that disagrees with its directory, and since both the check and the compiler pass, you won't notice.

   All there is to use is `stringParameter()` / `booleanParameter()` / `intParameter()` (optionally with `default = ...`), `enumParameter()` (given `entries` or a default value), plus `capture("name")` — embeddable anywhere in a layout's directory or file name — to name a wildcard in the destination, and, to read its value, `captureValue("name")` (inside the template). Do not add other words by guessing.
3. A template's own parameters (`--arg name=...`) can be passed as-is, with no setting needed on the module side. What is accepted is exactly the set of names the named Role's `.template { }` declares, plus the names given to wildcards in its `layout { }`; a typo still fails, as before, with `Unknown processor argument(s): ...`
4. Generate one file, and confirm right after that **the test adds no violation**: 0 violations, or only the ones recorded in the checklist's `violations` (the summary's diff from the previous run adds none). `--arg template=` takes the Role's full name: `Group.Role` if there is a group (`Outer.Inner.Role` with nested groups), or just the Role name if there is none. The Role name alone also works while no other Role has the same name.

   ```sh
   ./gradlew :architecture-test:katachiTemplate --arg template=UseCase --arg name=Sample
   ./gradlew :architecture-test:test --console=plain -Dkatachi.maxViolations=all
   ```

   For a Role whose wildcard was named, pass that value too. **A directory's value becomes the destination path as it is, even if no such directory exists** (a typo creates the file in a directory that isn't there). Pass the name of a directory that exists.

   ```sh
   ./gradlew :architecture-test:katachiTemplate --arg template=ViewModel --arg feature=home --arg name=Sample
   ./gradlew :architecture-test:test --console=plain -Dkatachi.maxViolations=all
   ```

   If the generated file shows up as a violation, check that the path of the file declaration `.template { }` is attached to is right.
5. **Delete the generated file, since it was only for checking** (unless the user says to keep it). After deleting it, run the check once more ([run-the-check](../../run-the-check/README.md)) and confirm the same: 0 violations, or only the ones in `violations`.

   If 6-C committed the generated pages, the definition changes above change them too: run `./gradlew :architecture-test:katachiDocs` again and commit the pages with the definition, or `--arg mode=check` fails in CI.

Record the files you created or changed in `changedFiles`, then mark `6-4` done.

**Steps 6-A to 6-D are optional.** The checklist treats them as optional too, and they do not count toward progress or step 5.

> **The session may end here.** The next session finds the working directory (the directory with `check-list.html` below the project root), reads the checklist's JSON, and goes on from there: from the first item that is not done, or, once step 5 is done, from what the user asks for (6-0, or one of 6-A to 6-D). The sections are in the table in `references/setup/README.md`. The previous session's results are in the checklist and the report; if `tmp/katachi-command.txt` is there, run katachi the way it says, and put a `JAVA_HOME=<path>` it starts with in front of `./gradlew` too (0-1). Do not rely on the conversation's memory.
