### 6-0. Fix the definition after review

This section is for when the user has looked at your report from step 6 and the definition and sent back comments. It has no checklist item.

There is one thing to do. Check the user's comments against the questions (`questions` and `answers` of both blocks) and the violations left in place (the checklist's `violations`), fix the definition, and confirm the counts by running the check ([run-the-check](../../run-the-check/README.md)). Keep the log of the run before the fix and compare it with the run after, to see that the fixed definition has not added any `[UnexpectedFile]`. Remove the entries of `violations` that the log no longer shows, and clear the warning on step 4 once none is left (0-3). If the user asks for the report again, write it from the checklist as it is now. If a fix moved existing sources or tests, run the project's own tests too (4-2).

If the user's reply answers a question, write it into `answers` before fixing (the id decides the block: `cq…` the report, `q…` the checklist; 0-3). If a comment raises a new question, add it to the checklist's `questions`.

> **The session may end here.** The next session finds the working directory (the directory with `check-list.html` below the project root), reads the checklist's JSON, and goes on from there: from the first item that is not done, or, once step 5 is done, from what the user asks for (6-0, or one of 6-A to 6-D). The sections are in the table in `references/setup/README.md`. The previous session's results are in the checklist and the report; if `tmp/katachi-command.txt` is there, run katachi the way it says, and put a `JAVA_HOME=<path>` it starts with in front of `./gradlew` too (0-1). Do not rely on the conversation's memory.
