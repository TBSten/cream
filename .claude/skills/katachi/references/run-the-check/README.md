# Running the check and reading its result

The check is the verification module's Gradle `test` task. Run `./gradlew` yourself: there is no `katachi` command for it. This page says how to run it, keep the log, read the result, and compare two runs (before and after a change to the definition).

Read `:architecture-test` as the Gradle path of the verification module (`meta.moduleDir` of the checklist; `static-analysis/katachi` is `:static-analysis:katachi`). On Windows, run `gradlew.bat` instead of `./gradlew`.

## Run it and keep the log

```sh
mkdir -p build/katachi
./gradlew :architecture-test:test --console=plain -Dkatachi.maxViolations=all > build/katachi/test-<name>.log 2>&1; echo "gradle exit status: $?"
```

- `-Dkatachi.maxViolations=all` puts every violation in the log. katachi prints only the first 10 by default, and a comparison is worthless when the 11th violation onward is missing. Do not write `maxViolations` in the test code: it wins over `-D` and cuts the log again.
- The output goes to the log, not to your screen. Read the log with the commands below instead of pasting all of it.
- `<name>` says what the run is for: `before-refactor`, `after-refactor`, or anything else. While the setup's working directory exists you may keep logs in its `tmp/` instead of `build/katachi/` (`build/` is ignored by git in a Gradle project, so the logs never become an `[UnexpectedFile]`).
- Anything else you want Gradle to do goes on the same line: `-Dkatachi.baseline.update=true`, `--project-cache-dir <dir>`, `--no-daemon`.
- When the check runs in a `git worktree`, run `./gradlew` from inside that work tree so that it checks the work tree's files.

## Read the result

```sh
grep -E 'Katachi check (failed|found)|BUILD (SUCCESSFUL|FAILED)|There were failing tests|Showing first' build/katachi/test-<name>.log
```

| What the log has | What it means |
|---|---|
| `BUILD SUCCESSFUL` | The check passed. A `Katachi check found N warnings` line, if there is one, lists warnings (`[AmbiguousLayout]` and the like) that never fail the check |
| `Katachi check failed: N violations (...)` | The check failed. The violations follow, one `[Kind] <path>` heading each |
| `There were failing tests` and no `Katachi check failed` | Another test of the module failed, not the check |
| Neither of them (`BUILD FAILED` with a compile error, for example) | The build did not get as far as the check. Read the end of the log and fix that first: **this says nothing about the definition, so never report it as 0 violations** |
| `Showing first N (M more...)` | The log is cut off, so it cannot be compared. Run it again with `-Dkatachi.maxViolations=all`; if it is still cut off, the test code passes `maxViolations` to `assert()`, so remove that |

List the violations (the headings, without `[MissingDescription]`, which goes down as descriptions are written):

```sh
grep -E '^[[:space:]]*\[[A-Za-z]+\] ' build/katachi/test-<name>.log | grep -vE '^[[:space:]]*\[(Incubating|MissingDescription)\]' | sed 's/^[[:space:]]*//' | sort
```

The number of `[MissingDescription]` warnings: `grep -c '\[MissingDescription\]' build/katachi/test-<name>.log`.

## Compare two runs

Run the check before the change and keep the log as `test-before-refactor.log`, make the change, run it again as `test-after-refactor.log`, then compare their violations:

```sh
list() { grep -E '^[[:space:]]*\[[A-Za-z]+\] ' "$1" | grep -vE '^[[:space:]]*\[(Incubating|MissingDescription)\]' | sed 's/^[[:space:]]*//' | sort; }
list build/katachi/test-before-refactor.log > build/katachi/violations-before.txt
list build/katachi/test-after-refactor.log > build/katachi/violations-after.txt
diff build/katachi/violations-before.txt build/katachi/violations-after.txt && echo "no difference"
```

- No difference: the check result is the same. A `<` line exists only before, a `>` line only after.
- Check first that both logs reached the check and neither is cut off (see the table above). A log without a result compares as "everything went away", and a cut-off one as a false difference.
- **If the earlier log was green (no violations), this is nothing against nothing, and it cannot tell you that the change loosened the definition.** Put a file that no Role covers somewhere, run the check, see `[UnexpectedFile]`, and delete the file.
- A refactoring of the definition (setup 3-3) should change nothing: any difference means the definition changed what it accepts, so fix the definition instead of taking the earlier log again. When the result is meant to change (add-and-update), each `<` line should be a violation you meant to remove and there should be no `>` line.
