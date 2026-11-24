## 6. Report the result to the user

Write your message to the user from the two blocks of `check-list.html` (the checklist and the codebase report), in the user's language, in the shape below. Take every number from the JSON; do not count from memory. A number is the count of entries in that array: one entry in `violations` that covers two files is 1 (its `location` says which files).

In the links, `<project root>` is the checklist's `meta.projectRoot`, an absolute path, so a link starts with three slashes (`file:///Users/me/dev/example-app/.local/install-katachi/check-list.html`). `meta.workdir` alone is relative to it: `file://.local/install-katachi/…` is a broken link (`.local` is read as a host name).

#### First — record template proposals

**This is a judgment call for you.** In katachi a `.template { }` can be attached to any file declaration in `layout { }`, and `:architecture-test:katachiTemplate` generates it **exactly where that declaration says it goes**. As "what you can do next" after installation, propose which Roles to give a template, and what it would contain.

Work from `roles` in the step 1 report. **The good candidates are Roles with many files of the same shape** (UseCase, ViewModel, Screen, Repository, and so on). Do not propose one for a Role with a small `count`, or one whose files each take a different shape.

**Roles whose destination directory is a wildcard, like `feature/*/...`, are candidates too** (ViewModel and Screen are usually shaped this way). Name that `*` with `capture("feature")` and you can pick the destination with `--arg feature=home` (step 2 of 6-D). **Every `*` left in the destination needs a name, both the module directory's and the package directory's.** If the two get the same value, give both one name (write `capture("feature")` in two places). The only thing that can't be named is `**` (a partial match can be named with `capture()`); don't propose one for a Role whose destination is only that.

For each Role, decide the following and add it to the report's `templates`.

| Field | Content |
|---|---|
| `role` | The Role's name (as written in `architecture { }`) |
| `files` | The files it creates. An array. Write them with the parameter embedded, like `${name}UseCase.kt` (only when one kind of file has several name shapes; files of different kinds, like an interface and its implementation, get separate Roles and one entry each) |
| `params` | The parameter names taken with `stringParameter()` or another `*Parameter()`, plus the name given to a wildcard in the destination (for example, `feature`). An array. **List as many as the names you gave to the `*` in the destination** (one, if you gave the module directory and the package directory the same name) |
| `basedOn` | The existing file the skeleton is based on (one of `roles[].examples`) |
| `reason` | Why this Role gets one (the count, and that the files share a shape) |

```json
"templates": [
  { "role": "UseCase", "files": ["${name}UseCase.kt"], "params": ["name"], "basedOn": "domain/src/main/kotlin/com/example/app/domain/useCase/GetUserUseCase.kt", "reason": "All 18 are interfaces and share one shape" },
  { "role": "UseCaseImplementation", "files": ["${name}UseCaseImplementation.kt"], "params": ["name"], "basedOn": "data/src/main/kotlin/com/example/app/data/useCase/GetUserUseCaseImplementation.kt", "reason": "All 18 implement a UseCase and share one shape" },
  { "role": "ViewModel", "files": ["${name}ViewModel.kt"], "params": ["feature", "name"], "basedOn": "feature/home/src/main/kotlin/com/example/app/feature/home/HomeViewModel.kt", "reason": "12 are one each in every feature/* module, and share one shape. The * in feature/* and the * of the package directory take the same name, so one name, feature, picks the destination" }
]
```

If you judge that no Role suits a template, record nothing. What you record goes into the "Next action" of your report below, and the page shows it under "5. Proposed templates" of the report. **Do not write `.template { }` here.** That happens in 6-D, after the user agrees.

#### The report

**If an item of steps 0 to 5 is not done**, say so and stop there:

```
❌ Could not set up katachi

Unfinished items:
- <id> <label>            (every item that is not done)

Resume:
  Working directory: file://<project root>/<meta.workdir>
  Done: <the ids that are done>
  Next section: <the section of the first item not done, from the table in references/setup/README.md>
  Definition: file://<project root>/<meta.moduleDir>/src/test/kotlin (or "not yet" before scaffold)
  Questions: report answered <n> / unanswered <n>, checklist answered <n> / unanswered <n>

Check list:
file://<project root>/<meta.workdir>/check-list.html
```

**Otherwise**, report what was done. Start with ⚠️ when the checklist's `violations` is not empty, ✅ when it is:

```
✅ Set up katachi

**The generated code needs your review.** Please check what it contains.

- Check list: file://<project root>/<meta.workdir>/check-list.html
- Codebase report: file://<project root>/<meta.workdir>/check-list.html#codebase-report
- `:architecture-test:test` result: ✅ All green  (or: ⚠️ architecture violations left: <number of entries in violations>)
- Questions for you (check list): answered <n> / unanswered <n>
    - <id> <question> → recommended: <recommendation>        (each unanswered one)
- Inconsistencies in the codebase (report): answered <n> / unanswered <n>
    - <id> <question> → recommended: <recommendation>
- Changed files: <number of entries in changedFiles>
- Next action:
    - **Review** ProjectArchitecture.kt under file://<project root>/<meta.moduleDir>/
    - Run one real implementation task and confirm that the definition works            (6-A, unless 6-1 is done)
    - Add `:architecture-test:test` to CI                                                (6-B, unless 6-2 is done)
    - You can set up documentation generation (`./gradlew :architecture-test:katachiDocs`) (6-C, unless 6-3 is done)
    - You can set up code generation from templates. Suggestions: <n>
        - <role>: <files> (parameters: <params>)                                         (6-D, only when templates is not empty)

Resume:
  Working directory: file://<project root>/<meta.workdir>
  Done: <the ids that are done>
  Next section (pick the one the user asked for):
    - Fix what the review pointed out: 6-0
    - <each of 6-A to 6-D not done yet, with its title>
```

Leave out a "Questions" line when that block has no questions. Mention the steps that carry a `warning` under the result line.

**For the unanswered questions, ask the user to answer them**: show the URL of the page (`file://<project root>/<meta.workdir>/check-list.html`; the report's questions, ids starting with `cq`, are at `#report-questions`, the checklist's, ids starting with `q`, at `#questions`), and have them answered in the browser ("Copy all" or "Save as JSON" in the menu at the bottom right; on a Japanese page 「まとめてコピー」 or 「JSON を保存」), or in chat. Write what comes back into `answers` (0-3, "Answers").

> **The session may end here.** The next session finds the working directory (the directory with `check-list.html` below the project root), reads the checklist's JSON, and goes on from there: from the first item that is not done, or, once step 5 is done, from what the user asks for (6-0, or one of 6-A to 6-D). The sections are in the table in `references/setup/README.md`. The previous session's results are in the checklist and the report; if `tmp/katachi-command.txt` is there, run katachi the way it says, and put a `JAVA_HOME=<path>` it starts with in front of `./gradlew` too (0-1). Do not rely on the conversation's memory.
