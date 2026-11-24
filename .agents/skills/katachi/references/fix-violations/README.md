# Fixing violations

The katachi check failed: on CI, in a pull request, or locally. A violation means the project and the `architecture { }` definition disagree. Either side can be the one that is wrong, and **which one is wrong is a question about the team's intent**. Your job is to find out which, fix the clear cases, and put the unclear ones in front of the user.

## 1. Get every violation

Locally, run:

```sh
mkdir -p build/katachi
./gradlew :architecture-test:test --console=plain -Dkatachi.maxViolations=all > build/katachi/test-before-fix.log 2>&1; echo "gradle exit status: $?"
```

This runs the check with every violation in the log (not only the first 10; without `-Dkatachi.maxViolations=all` only the first 10 are printed) and keeps the full log, which section 4 compares against. [../run-the-check/README.md](../run-the-check/README.md) says how to read it (read `:architecture-test` as the verification module's path). The violations are also in the test's failure message, in full under `architecture-test/build/test-results/test/`.

On CI, the job log has the same output. If the failure is not a violation at all (a compile error, dependency resolution, `Could not start Gradle Test Executor`), it is a build problem: fix that first, the check has not run.

Each violation is a paragraph like this:

```
[UnexpectedFile] file:///path/to/project/feature/home/src/main/kotlin/com/example/feature/home/HomeHelper.kt
  No role is defined for this file.

  How to fix:
    - Delete it if it is not needed
    - Add a new role for it: …
```

## 2. Read what each kind means

| Label | What it says | Usually |
|---|---|---|
| `[UnexpectedFile]` / `[UnexpectedDirectory]` | A file or directory no Role declares (for a directory, nothing below it was checked) | A new kind of file (add a Role), a file in the wrong place (move it), or a structure that changed (follow it). A file the OS writes on its own (`.DS_Store`, `Thumbs.db`, `desktop.ini`) never gets a Role: add it to `.gitignore` as its `How to fix:` says (a Role fails with `[MissingFile]` on a checkout without it, such as CI) |
| `[MissingFile]` | A Role declares a single file that does not exist | The file was removed or renamed; drop or fix the declaration, or create the file if it should exist. For a Gradle wrapper file, run `gradle wrapper`, or write `gradle(requireWrapper = false)` when the project has no wrapper |
| `[UnsatisfiedFileConstraint]` | A constraint on contents (`ktFileContent { }`, `fileConstraint`) does not hold | The code breaks the rule (fix the code, with the user's consent), or the rule is out of date |
| `[CaptureMismatch]` | Two places named with the same `capture("…")` have different values | A package that spells its module directory's name in another case (write the conversion the block suggests, such as `capture("feature").flatCase`), a package that does not match its module directory, or a layout that should give the two places separate names |
| `[UncheckedFileConstraint]` / `[UncheckedFile]` / `[UncheckedDirectory]` / `[UncheckedCheck]` | The check could not run for that path; it is neither allowed nor rejected | Read the cause printed under it. `[UncheckedFileConstraint]` with `Pass FileConstraintCheck() to assert()` means the test does not run the constraint check |
| `[StaleBaselineEntry]` | The baseline holds back a violation that no longer exists | Something was fixed: prune the entry (below) |
| `[AmbiguousLayout]` (warning) | Two declarations claim the same file | Narrow one of them; the file must belong to one Role |
| `[IgnoredLayout]` (warning) | A line of a Role's `layout { }` lies in a directory an `ignore()` hides (or a module's `build`, which `gradle()` ignores), so none of its files is checked | Remove or narrow the `ignore()` if the files matter; otherwise drop the line from the Role |

Warnings never fail the check, but do not ignore them: fix them or tell the user why they stay.

## 3. Decide, violation by violation

Go through them one at a time, and sort each into one of three:

1. **The project is wrong, and the fix is clear.** A file in a directory where its kind does not belong, a typo in a file name, an import a rule forbids. Fix the code only if the user asked you to fix the build or agreed; otherwise propose the fix.
2. **The definition is out of date.** The project changed on purpose: a new kind of file, a moved module, a rule the team dropped. Change the definition, following [../add-and-update/README.md](../add-and-update/README.md).
3. **You cannot tell.** Ask the user, with the violation, what you think each side would mean, and your recommendation. Do not guess in either direction.

Look at the pull request or the recent commits for intent: a refactor PR that moved a package means "the definition is out of date"; a feature PR that dropped one file somewhere unusual more likely means "the project is wrong".

**Never make a violation disappear by loosening the definition**: no new catch-all Role, no `ignore()` on files whose contents matter, no extra line in the nearest Role's `layout { }` when that Role's `summary` does not describe the file. A check that accepts everything is green and useless.

## 4. Confirm

Run the check again into `build/katachi/test-after-fix.log` (on CI, look at the job) and compare it with `test-before-fix.log` as "Compare two runs" in [../run-the-check/README.md](../run-the-check/README.md) says: every violation you meant to fix should be gone, and nothing new should appear. If you changed the definition, also put a stray file where you changed it and confirm it still fails, then delete the file.

## Tolerating what is there now

If many violations are real but the team will fix them later, and the user wants the check to fail only on new ones, propose the baseline. It is experimental, so ask first. Read https://tbsten.github.io/katachi/guides/baseline/ . In short:

- `baseline()` in `architecture { }`; like every experimental API, the file that calls it starts with `@file:OptIn(ExperimentalKatachiApi::class)` and `import me.tbsten.katachi.ExperimentalKatachiApi`. The baseline file is `katachi-baseline.json` at the project root by default
- The baseline file is checked too, so declare it in a Role (`"BaselineFile".role { layout { "katachi-baseline.json".file() } }`)
- `./gradlew :architecture-test:test -Dkatachi.baseline.update=true` writes the current violations into it; `-Dkatachi.baseline.prune=true` removes entries that were fixed (`[StaleBaselineEntry]`). Both are refused on CI (`CI=true`)

A baseline hides violations; it does not fix them. Tell the user how many went into it.
