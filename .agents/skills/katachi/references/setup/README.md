# Setting up katachi

These are the steps to install [katachi](https://tbsten.github.io/katachi) in a Gradle project. You will create an architecture definition that fits the user's codebase, check it, and report back.

**Read one section at a time, in the order of the table below.** Open a section's file only when you reach it. Most sections end by telling you to mark their items done in the checklist (0-3 says how); then open the next row of the table.

The steps use the `katachi` command (`brew install tbsten/tap/katachi`) for `doctor`, `scaffold` and `lint`. The check itself is run with `./gradlew` ([run-the-check](../run-the-check/README.md)). Section 0-1 checks that it is there, and says how to run the same thing through the Kotlin command line compiler where there is no Homebrew. The checklist and the report you fill in are copied from this skill's `assets/` (0-2).

## Sections

| ID | Section | File |
|---|---|---|
| 0 | Preparation | [step0/README.md](step0/README.md) |
| 0-1 | Get all permissions up front | [step0/0-1-permissions.md](step0/0-1-permissions.md) |
| 0-2 | Set up the working directory | [step0/0-2-working-directory.md](step0/0-2-working-directory.md) |
| 0-3 | Reading and writing the checklist and report | [step0/0-3-checklist-and-report.md](step0/0-3-checklist-and-report.md) |
| 1 | Analyze and infer the current project codebase's structure and each component's constraints | [step1/README.md](step1/README.md) |
| 1-1 | Read the project's own documentation first | [step1/1-1-read-project-docs.md](step1/1-1-read-project-docs.md) |
| 1-2 | Scan the codebase and fill in the report | [step1/1-2-fill-the-report.md](step1/1-2-fill-the-report.md) |
| 1-3 | Decide the writing preferences | [step1/1-3-writing-preferences.md](step1/1-3-writing-preferences.md) |
| 2 | Create the verification module | [step2/README.md](step2/README.md) |
| 2-1 | Run scaffold | [step2/2-1-scaffold.md](step2/2-1-scaffold.md) |
| 2-2 | Run once with an empty definition | [step2/2-2-empty-definition.md](step2/2-2-empty-definition.md) |
| 3 | Write the architecture { } definition | [step3/README.md](step3/README.md) |
| 3-1 | Read the documentation by topic | [step3/3-1-fetch-docs.md](step3/3-1-fetch-docs.md) |
| 3-2 | Write the definition | [step3/3-2-write-the-definition.md](step3/3-2-write-the-definition.md) |
| 3-3 | Refactor the definition | [step3/3-3-refactor.md](step3/3-3-refactor.md) |
| 4 | Verify | [step4/README.md](step4/README.md) |
| 4-1 | Run the test | [step4/4-1-run-the-test.md](step4/4-1-run-the-test.md) |
| 4-2 | How to handle errors | [step4/4-2-handle-errors.md](step4/4-2-handle-errors.md) |
| 4-3 | Generate the documentation | [step4/4-3-generate-docs.md](step4/4-3-generate-docs.md) |
| 5 | Review the checklist | [step5/README.md](step5/README.md) |
| 6 | Report the result to the user | [step6/README.md](step6/README.md) |
| 6-0 | Fix the definition after review | [step6/6-0-fix-after-review.md](step6/6-0-fix-after-review.md) |
| 6-A | (After reporting in 6) Confirm the installation worked | [step6/6-A-confirm-the-installation.md](step6/6-A-confirm-the-installation.md) |
| 6-B | (After reporting in 6) If asked to set up CI | [step6/6-B-ci.md](step6/6-B-ci.md) |
| 6-C | (After reporting in 6) Set up documentation generation | [step6/6-C-documentation-generation.md](step6/6-C-documentation-generation.md) |
| 6-D | (After reporting in 6) Set up code generation from templates | [step6/6-D-templates.md](step6/6-D-templates.md) |

In the checklist, 6-A to 6-D are 6-1 to 6-4.

## Dividing the work between the main agent and subagents

Where subagents are available, split the work as in the table below. Only the main agent writes the checklist and the report; subagents never do. The one place a subagent writes in the working directory is its own `<workdir>/tmp/verify/<name>/` in 6-A.

| Section | Main agent | Delegated to subagents | What the main agent receives |
|---|---|---|---|
| 1-1, 1-2 | Writing the report's JSON (one writer) | Reading, scanning, drafting Role candidates | JSON per section (subagents do not write) |
| 1-3, 2 | Everything | — | — |
| 3-1 | Reading the topic pages | Finding the relevant API in the API reference | Signatures and examples only |
| 3-2, 3-3, 4 | Editing the definition and running the check | — | — |
| 6-A | Choosing the task and judging the result | Implementation and verification (one output directory per subagent) | Violation counts and the difference between the logs of the runs |
| 6-B to 6-D | Everything | — | — |
