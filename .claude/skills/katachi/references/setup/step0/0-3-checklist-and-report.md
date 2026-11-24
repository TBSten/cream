### 0-3. Reading and writing the checklist and report

`check-list.html` is **a single HTML file that carries its data as JSON inside it.** One page shows the checklist and, below it, the codebase report. The HTML is only a rendering shell: **the only thing you read or write is the JSON**, in two blocks:

```html
<script type="application/json" id="checklist">     the checklist (progress, preferences, violations, questions q…)
{
  ...
}
</script>
...
<script type="application/json" id="report">        the codebase report (step 1; questions cq…)
{
  ...
}
</script>
```

"The checklist" and "the report" in these steps mean these two blocks, not two files.

#### How to edit the JSON

- **Read** the lines between the opening tag of the block and the next `</script>`. There is no need to read the rest of the HTML.
- **Write** by editing those lines in place with your file editing tool. Change only what you mean to change; leave every other key as it is.
- **Make the text you replace match one place only.** Start it at the `"id"` line of the item or step you change (`"id": "5-1",` down to `"done": false` for an item, `"id": "3",` down to `"warning": null,` for a step). Each block is followed by an example in an HTML comment (`<!-- Example: …`; ja: `<!-- 記入例: …`) with the same ids: never edit it. If your tool still finds more than one match, add the lines around it until there is one; never replace every match.
- **Keep it valid JSON**: double quotes, no trailing commas, `null` (not `None`), numbers without quotes where the template has a number.
- **Keep both tags alone on their own lines**, as the template has them. The `katachi` command finds the block by those lines.
- **Never touch the HTML outside the two blocks**, and never replace the file with a fresh copy once it has data in it.
- **Check after every write.** Read the block back. If `jq`, `python3` or `node` is available, parse it to be sure, for example `sed -n '/<script type="application\/json" id="report">/,/<\/script>/p' <workdir>/check-list.html | sed '1d;$d' | jq . > /dev/null` (`id="checklist"` for the other block). Opening the HTML in a browser also shows a parse error at the top of the page.
- Before a large rewrite, copy the file into `<workdir>/tmp/` first, so a broken edit can be undone.

`null` and an empty array mean "not filled in yet". The exceptions are the arrays that can rightly stay empty once you have looked: the report's `excluded` (nothing tracked needs leaving out), `questions` (nothing to ask) and `templates` (no Role suits a template, 6), and the checklist's `violations` and `questions`. When you looked and found nothing, leave them `[]`; the page shows them as "None" (ja: 「なし」).

#### Arrays: add entries, never drop them

Add an entry by appending an object to the array; keep the entries already there. The one exception is `violations`: when 6-0 fixes a violation recorded there (the log of the check no longer shows it), remove its entry, and once the array is empty, clear the warning on step 4. Otherwise the fixed violation stays on the page and in the report from step 6. One entry per thing (one file, one Role, one question). Do not add a second entry for something already recorded (the same `path` or `name`). The fields each array takes:

| Array | Where | Fields |
|---|---|---|
| `changedFiles` | checklist | `path` (relative to the project root), `change` (`New` / `Modified`; on a Japanese page, `新規` / `変更`), `summary` |
| `violations` | checklist | `violation`, `location`, `whyNotFixed`, `suggestion` |
| `questions` | checklist | `id` (`q1`, `q2`, …), `question`, `observed`, `options` (array), `recommendation` |
| `questions` | report | `id` (`cq1`, `cq2`, …), `question`, `observed`, `options` (array), `recommendation` |
| `modules` | report | `path`, `kind`, `responsibility`, `buildFile`, `deviation` (only when it differs from the other modules of its kind) |
| `roles` | report | `importance`, `name`, `group`, `layout`, `naming`, `allowed` / `forbidden` / `examples` (arrays), `count`, `note`; `constraintOnly: true` for a constraint on a Gradle build file (1-2) |
| `excluded` | report | `path`, `reason` |
| `templates` | report | `role`, `files` / `params` (arrays), `basedOn`, `reason` |

In `questions`, `options` are shown lettered A, B, C, … in their order. **Start `recommendation` with the letter of the option you recommend**, then the reason (`"B. The modules already follow it"`). The page marks that option as recommended and offers "Use the recommendation" only when the text starts with the letter. Without it the user sees only the text.

Number question ids on from the last one in the same block: the report's are `cq…` and the checklist's `q…`, so they never collide.

Two rules the arrays keep:

- **No Gradle build file as a Role.** `build.gradle.kts`, `settings.gradle.kts`, `gradle.properties` and the version catalog are covered by `gradle()`, so they are not Role candidates (except `buildSrc` and builds added with `includeBuild`). For a constraint on a build file, add a `roles` entry with `constraintOnly: true` and the reason in `note`.
- **Nothing in `excluded` that `.gitignore` already leaves out.** katachi never looks at ignored files in the first place (`git check-ignore -q <path>` tells you).

`changedFiles` takes one entry per file, but when the definition grows to dozens of files, record them as **a single entry with a directory or a wildcard** (`"path": "architecture-test/src/test/kotlin/**", "change": "New", "summary": "the whole architecture { } definition (12 files)"`). A file changed again later needs no second entry.

#### Answers

Answers to `questions` go into `answers` of the same block, keyed by the question's id (`q…` is the checklist, `cq…` the report):

```json
"answers": {
  "cq1": { "answer": "Subpackages are allowed", "note": null, "answeredAt": "2026-11-10T09:30:00+09:00" }
}
```

The user can answer in the browser too, in "Questions for you" of the checklist and "4. Questions for you" of the report. Once there is an answer or a feedback comment, a small menu appears at the bottom right of the page, and the user hands things back in one of two ways. Either way, copy the entries into `answers` of the block they belong to:

- **"Copy all"** copies text to paste into the chat: one line saying what the answers are for, a line listing the ids still open (if any), and a ```` ```json ```` block with `{"checklist": {"answers": {"q1": {…}}}, "report": {"answers": {"cq1": {"answer": "…", "note": "…"}}}}`. If there is feedback (below), it follows in the same paste. Only what was not copied before goes in; when everything was, all of it is copied again.
- **"Save as JSON"** saves the same JSON as a file (`katachi-answers.json`).

The page is in the user's language, so tell the user the names they see. On a Japanese page: "Questions for you" is 「ユーザに確認したいこと」, "Copy all" is 「まとめてコピー」, and "Save as JSON" is 「JSON を保存」.

The outermost keys are the ids of the blocks (`checklist` / `report`); a block with no answers is left out. An entry whose `note` is `null` was a plain pick.

#### Feedback

The page also has a **Feedback mode** (the button at the bottom right). The user clicks any item on the page (Shift-click adds more items to the same comment), writes a comment, and copies all comments at once with "Copy all" in the same menu (together with any answers). What you receive looks like this:

```text
Feedback on the katachi installation checklist (check-list.html), 2 item(s). "Target" is the place in that file's JSON; its head, checklist. or report., names the JSON block (id). …

1. Codebase report › 2. Elements (candidate roles) › App › Ui › ViewModel
   Target: report.roles[name=ViewModel]
   > feature modules have ViewModels too (feature/*/src/...)

2. Codebase report › 1. Architecture findings › Main frameworks and 1 more
   Target: report.overview.frameworks (Codebase report › 1. Architecture findings › Main frameworks)
   Target: report.overview.targets (Codebase report › 1. Architecture findings › Targets)
   > add the versions
```

An entry with several `Target` lines is one comment the user put on several places at once (Shift-click); the name in parentheses is what that place is called on the page. Apply the comment to every one of them.

Under each `Target` there may be one more line. `Quote: "…"` is the part of the text the user selected before writing, so the comment is about those words. `Shows: "…"` is what that place said when the comment was saved (cut to about 120 characters); if your JSON no longer says that, you may have changed it since.

`Target` starts with the block (`checklist.` or `report.`) and points into its JSON: `report.roles[name=ViewModel]` is the `roles` entry of the report whose `name` is `ViewModel`, `report.roles[name=ViewModel].forbidden[0]` the first entry of its `forbidden`, `report.excluded[path=third-party/].reason` one field of a row, `report.roles[group=App.Ui]` the roles in that group, `checklist.steps[id=3].items[id=3-2]` an item of the checklist, `report.overview.language` a key, `report.directoryTree: app/src/` a line of the directory tree, `report` the whole report, and `page` the whole page. For each comment, fix that entry (check the code again if the comment says the facts are wrong), or tell the user why you did not. Then tell the user which ones you changed. The comments stay in their browser until they delete them.

#### Write down what you learn as you go

**Do not save everything up for the end.** If you get interrupted partway through, the entire investigation is lost. Write each section of the report as soon as it is filled in: for step 1 that means `overview` → `architecture` → `modules` → `directoryTree` → `roles` → `excluded`, so 6 or more writes. **Only the main agent writes**; subagents return their results, and the main agent writes them in order.

#### Progress

The checklist's JSON has `steps`. Progress is the `done` of each item:

```json
{
  "steps": [
    {
      "id": "4",
      "title": "Verify",
      "warning": null,
      "items": [
        { "id": "4-1", "label": "...", "done": false }
      ]
    }
  ]
}
```

The item ids are `0-1` `0-2` / `1-1` `1-2` `1-3` / `2-1` `2-2` / `3-1` `3-2` `3-3` / `4-1` `4-2` `4-3` / `5-1` `5-2`, plus **`6-1` `6-2` `6-3` `6-4` for the optional step 6** (the step has `"optional": true`). Mark 6-1 to 6-4 only if you did 6-A to 6-D.

- **"Mark `<id>` done"** in these steps means: set that item's `done` to `true`. Do not rewrite `label`.
- **"Put a warning on step `<n>`"** means: set that step's `warning` to one line of text (`null` clears it). A step with a warning turns yellow on the page, and stays open even once it is complete.

After marking a section's items done, read the next row of the table in `references/setup/README.md`, including the chapter introductions (`step3/README.md` and so on), which have no items.

`meta` (0-2), `preferences` (1-3) and `answers` sit next to `steps`; `violations`, `questions` and `changedFiles` are the arrays above.
