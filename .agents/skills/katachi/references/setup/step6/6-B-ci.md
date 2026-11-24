### 6-B. (After reporting in 6) If asked to set up CI

Handle this as follows, based on the CI system in use. If `meta.moduleDir` is not `architecture-test`, read `:architecture-test` below as the Gradle path of `meta.moduleDir` (`static-analysis/katachi` is `:static-analysis:katachi`).

### If using GitHub Actions

Add a workflow like the one below. If a CI workflow already exists, use the following as a reference and modify it to run `./gradlew :architecture-test:test` (do not proliferate new yaml files just for `:architecture-test:test`).

```yaml
name: Architecture

on:
  push:
    branches: [main] # the repository's default branch
  pull_request:

permissions:
  contents: read

jobs:
  architecture:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v7

      - uses: actions/setup-java@v6
        with:
          distribution: temurin
          java-version: '17' # the JVM toolchain N from 2-1 (meta.jvmToolchain). Without a toolchain, the major version of "Gradle's JDK" that katachi doctor prints (17 or later)

      - uses: gradle/actions/setup-gradle@v6

      - run: ./gradlew :architecture-test:test
```

Put the repository's default branch in `branches:`. `git ls-remote --symref origin HEAD` prints it (`ref: refs/heads/master` means `master`); if it does not work, ask the user. With another name there, a push to the default branch never runs the check, only pull requests do.

If the workflow file you just created (`.github/workflows/*.yml`) is not in the `architecture { }` definition, add a Role for it (for example `".github/workflows" / "*.yml".file()`, with `documented = false` as for other tool configuration) and run the check ([run-the-check](../../run-the-check/README.md)) to confirm it passes.

If the existing CI is a single matrix job that runs emulators or several OSes, **add the job separately, outside the matrix**, so `:architecture-test:test` does not run for every matrix entry. Do not create a new yaml file; add the `architecture` job above to `jobs:` in the same workflow file.

If you committed the generated documentation in 6-C, add the following line to the same job. `--arg mode=check` writes nothing; it compares against the pages on disk and fails if they differ from the definition (it catches "changed the definition, forgot to regenerate the docs").

```yaml
      - run: ./gradlew :architecture-test:katachiDocs --arg mode=check
```

### If using something other than GitHub Actions

Modify the workflow so that `./gradlew :architecture-test:test` runs and reports at an appropriate, non-intrusive time, such as on pull request creation, merge request creation, or a pre-push hook.

When done, mark `6-2` done.

> **The session may end here.** The next session finds the working directory (the directory with `check-list.html` below the project root), reads the checklist's JSON, and goes on from there: from the first item that is not done, or, once step 5 is done, from what the user asks for (6-0, or one of 6-A to 6-D). The sections are in the table in `references/setup/README.md`. The previous session's results are in the checklist and the report; if `tmp/katachi-command.txt` is there, run katachi the way it says, and put a `JAVA_HOME=<path>` it starts with in front of `./gradlew` too (0-1). Do not rely on the conversation's memory.
