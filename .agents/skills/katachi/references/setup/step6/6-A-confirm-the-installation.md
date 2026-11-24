### 6-A. (After reporting in 6) Confirm the installation worked

Confirm a sample implementation task and carry it out.

**Pick a task that adds new files.** What katachi looks at is where files go and what Role they play, so a task that only edits files that already exist — a bug fix, say — exercises nothing. In a real adoption, 4 of 7 tasks touched only existing files and never gave katachi anything to say. A new screen, a new module or a new test is what you want.

- If the user has specified a task, judge whether it's reasonable. If it seems unreasonable, ask the user to decide whether to actually run it.
- If the user hasn't specified a task, **pick one from this repository's issues first.** A task you make up tends to come out in a convenient shape. A real issue tries katachi against a change this project would actually make.

### Pick a task from the issues

1. Check with `git remote -v` that this is a GitHub repository, and look at the open issues with `gh issue list --state open --limit 50`. If `gh` is missing, not authenticated, or the repository is not on GitHub, show the user the URL of the issue list and have them pick candidates.
2. Pick **1 to 5** that fit these conditions.
   - **It adds new files** (a new screen, module, use case, test, and so on)
   - It is small, and its spec is contained in the issue (labels such as `good first issue` are a hint)
   - You can say which Role of `architecture { }` the new files belong to
3. Show the user each candidate with "the issue number and title", "the files it will likely add", and "the Role they belong to", and have them choose which to do.
4. Only when no issue fits, or you cannot see the issues, come up with 1 to 5 tasks from different categories, tell the user, and then carry them out.

**Do not comment on, close, or open a PR for the issue.** Unless the user asks, the task ends at implementing it locally and running the check.

### Run it and check

Before you start, **write down which Role you expect each new file to belong to.** After running the task, run the check ([run-the-check](../../run-the-check/README.md)), compare the result with your expectation, and decide whether `architecture { }` needs fixing. You will find out one of three things: it was accepted as expected, it came out as an unexpected `Unexpected`, or a catch-all Role quietly accepted it.

When using a past PR as the task, check how it was merged with `gh pr view <number> --json mergeCommit,commits`; "before" is **the parent of the PR's first commit** (`git rev-parse <first commit>^`) and "after" is the merge commit. With a rebase merge, `mergeCommit^1` is "just before the last commit", which does not compare the whole PR.

When verifying in parallel, give each subagent its own output and temporary files under `<workdir>/tmp/verify/<name>/`, and let only the parent write the checklist and the report. A worktree cannot reach the working directory, so pass absolute paths, and run `./gradlew` **from inside the worktree**, with the log in that worktree's `build/katachi/` ([run-the-check](../../run-the-check/README.md)). It checks the files of the worktree it runs in, and each worktree has its own log, so parallel runs do not overwrite each other's. Do not run it from the main work tree: a result of 0 violations there says nothing about the new files.

When done, mark `6-1` done.

> **The session may end here.** The next session finds the working directory (the directory with `check-list.html` below the project root), reads the checklist's JSON, and goes on from there: from the first item that is not done, or, once step 5 is done, from what the user asks for (6-0, or one of 6-A to 6-D). The sections are in the table in `references/setup/README.md`. The previous session's results are in the checklist and the report; if `tmp/katachi-command.txt` is there, run katachi the way it says, and put a `JAVA_HOME=<path>` it starts with in front of `./gradlew` too (0-1). Do not rely on the conversation's memory.
