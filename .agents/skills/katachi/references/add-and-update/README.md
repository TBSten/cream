# Adding to and updating the definition

katachi is already set up. The project has moved on, and the `architecture { }` definition has to follow: a new kind of file, a module or package that moved, a rule the team wants enforced, a newer katachi. This reference is for changing a definition that works, without making it any looser than it needs to be.

If the check is failing and you do not yet know why, read [../fix-violations/README.md](../fix-violations/README.md) first. It sends you back here when the answer is "the definition is out of date".

## Pick the file for the change

| The change | Read |
|---|---|
| A new kind of file appears (a new layer, a new kind of module, a new tool's config) | [add-a-role.md](add-a-role.md) |
| Files moved: a package or module was renamed, split or merged, a directory was reorganized | [follow-a-move.md](follow-a-move.md) |
| The team wants a rule enforced (what a file may import or declare, a naming rule, a constraint on build files) | [add-a-constraint.md](add-a-constraint.md) |
| Generate new files of an existing Role from a template, or add a template | [use-templates.md](use-templates.md) |
| A newer katachi | [upgrade-katachi.md](upgrade-katachi.md) |

## The loop every change follows

1. **Find the definition and how the check runs.** The verification module is the Gradle module that applies the `me.tbsten.katachi` plugin; its `katachi { architecture = "…" }` names `projectArchitecture`, and the definition lives under `src/test/kotlin/<package>/test/architecture/` of that module. Read the files you are about to change, and how the existing ones are split and named, before writing anything: follow the conventions already there. If that module is not `architecture-test` and the setup's working directory (with its check list) is gone, add `--module-dir <its path>` to every `katachi lint` in these steps, and read `:architecture-test` as its Gradle path in the `./gradlew` commands.
2. **Take the result before the change.** Run the check once and keep the log.

   ```sh
   mkdir -p build/katachi
   ./gradlew :architecture-test:test --console=plain -Dkatachi.maxViolations=all > build/katachi/test-before-refactor.log 2>&1; echo "gradle exit status: $?"
   ```

   It runs the check with every violation in the log (not only the first 10) and keeps the log as `build/katachi/test-before-refactor.log`. [../run-the-check/README.md](../run-the-check/README.md) says how to read it.
3. **Make the smallest change that says what is true now.** Declare the new thing where it belongs; do not widen an existing pattern to swallow it.
4. **Run the check again and compare.** Every violation that went away should be one you meant to remove, and nothing should have appeared. Run the same command into `test-after-refactor.log`, then compare the two logs as "Compare two runs" in [../run-the-check/README.md](../run-the-check/README.md) says.

   Here the result is meant to change, so `diff` prints lines. Each `<` line (only before) should be a violation you meant to remove, and there should be no `>` line (only after). A `>` line, or a `<` line you did not expect, means the change did more than you meant: fix the definition.
5. **Make sure it still fails when it should.** If the check was green before and after, a loosened definition looks the same as a correct one. Put a file no Role covers in the area you changed, run the check, see `[UnexpectedFile]`, and delete the file.
6. **Run `katachi lint`.** It lists Roles that look like they hold different kinds of files, hand-written Roles for Gradle files, flat groups, Role and group names that start lowercase, files that hold two or more Roles or groups, and candidates for `documented = false`. Fix what it prints, or tell the user why you kept it. The candidates for `documented = false` are the exception: they are for review, not a count to bring to 0. Set it on generated output, tool configuration and single files such as a README or LICENSE (as in [add-a-role.md](add-a-role.md)), and leave the rest.
7. **Write the descriptions.** A new Role gets `summary` (one sentence that describes every file it holds, shown as the Role's line in the generated documentation), `description` if there is more to say, and `example()` with a real file. All of them are optional and the check does not ask for them, but a Role without `summary` leaves its line in the documentation empty, so write them without waiting for a warning.
8. **If the project commits the generated documentation** (a `processors { docs { outputDir = … } }` in the verification module's `build.gradle.kts`), regenerate it with `./gradlew :architecture-test:katachiDocs`, and commit it with the change. CI may run `katachiDocs --arg mode=check`, which fails when the pages are stale.

## What not to do

- Do not add a line to the nearest Role's `layout { }` to make an `[UnexpectedFile]` go away. If that Role's `summary` does not describe the file, it needs a Role of its own.
- Do not use `ignore()` or `anyFile()` for files whose contents matter. `ignore()` is for directories katachi should not look inside at all (generated output, a vendored tool).
- Do not write `maxViolations` in the test code; it overrides `-Dkatachi.maxViolations` and cuts the output.
- Do not change the name or package of `val projectArchitecture`.
- Do not rewrite the verification module's `build.gradle.kts`, except for a katachi version bump ([upgrade-katachi.md](upgrade-katachi.md)) or `processors { }` the user agreed to.
- Do not make functions that split the definition `inline`: katachi reads a violation's declaration site off the stack trace, and `inline` shifts it.
