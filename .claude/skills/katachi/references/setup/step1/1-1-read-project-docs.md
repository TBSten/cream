### 1-1. Read the project's own documentation first

**Read it before the code.** The code shows what the project *is*; the documentation shows what it is *meant to be*, and `architecture { }` declares the latter. Write the definition from the code alone and you get **a definition that rubber-stamps the present state**, freezing existing drift in place as if it were the rule.

Look in the following places (whatever exists).

- `README` / `CONTRIBUTING` / `ARCHITECTURE.md`
- `docs/`, `doc/`, `documentation/`, and any documentation site (`docs-site/` and the like)
- ADRs (`docs/adr/`, `doc/architecture/decisions/`, ...)
- `CLAUDE.md` / `AGENTS.md` / `.cursor/rules` - conventions written for AI agents
- a `README` inside each module

Take the following from them.

| What | Where it goes |
|---|---|
| **The vocabulary the team actually uses** (`UseCase` or `Interactor`, `Screen` or `Page`) | `roles[].name`. **Do not invent names of your own** |
| Deliberate rules ("the UI never calls a repository directly") | `roles[].allowed` / `forbidden` |
| The name of the architecture and why it was chosen | `architecture.name` / `rationale` / `characteristics` |
| Checks of their own (a Kotlin compiler plugin, custom detekt rules) | `architecture.characteristics` |

Write inconsistencies into **the report's** `questions` (ids `cq1`, `cq2`, …; the checklist's `questions` are for something else, 4-2). The fields are in 0-3.

**Where the documentation and the code disagree, write it into `questions`.** Do not quietly side with the code. Whether the documentation is stale or the code has drifted is something only the user can say - and that disagreement is exactly what belongs in front of them before step 3.

If there is no documentation at all, skip this section and infer from the code. Leave one line in `questions` saying so: that everything below is inferred, because the project documents nothing.

Once you've read it all, mark `1-1` done.
