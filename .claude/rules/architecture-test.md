---
paths:
  - architecture-test/**
---

# architecture-test (katachi)

`architecture-test/` holds cream's [katachi](https://tbsten.github.io/katachi/) definition and the one test
that checks the repository against it. Docs: <https://tbsten.github.io/katachi/llms-full.txt> (guide) /
<https://tbsten.github.io/katachi/api-docs/katachi/llms-full.txt> (API).

```bash
./gradlew :architecture-test:test          # the check (never UP-TO-DATE: it walks the whole repository)
./gradlew :architecture-test:katachiDocs   # regenerate doc/internal/architecture/ (committed)
./gradlew :architecture-test:katachiDocs --arg mode=check   # what CI runs: fails if the pages are stale
```

This definition is the **single source of truth** for cream's architecture rules: where each kind of file lives,
cream-ksp's dependency table, naming, file-size budgets, test style and documentation rules. The rule files in
`.claude/rules/` only point at the generated pages. When a rule changes, change the Role / group (its constraint
and its `description`), then regenerate the pages in the same commit.

## Layout of the definition

```
src/test/kotlin/me/tbsten/cream/test/architecture/
├── ProjectArchitecture.kt      # `val projectArchitecture = architecture { ... }` — only calls the top-level groups
├── ProjectArchitectureTest.kt  # the single kotest spec: `projectArchitecture.assert(FileConstraintCheck())`
├── <group>/<Group>.kt          # `internal fun DeclarationContainerScope.<group>() = "<group>".group { ... }`
├── <group>/<role>/<Role>.kt    # `internal fun DeclarationContainerScope.<role>() = "<Role>" { ... }`
└── support/
    ├── Paths.kt                # source-root constants (KSP_MAIN_DIR, ...)
    └── Constraints.kt          # constraints several roles share (dependsOnlyOn(), staysWithinLineBudget(), ...)
```

- Each file holds exactly one group or one Role; a group file only calls the functions of what is under it.
- Directory names are the group / Role name with a lowercase first letter; the package follows the directory.
- Do not rename `val projectArchitecture` or move its package: `katachi { architecture = "..." }` in
  `architecture-test/build.gradle.kts` points at it.
- Do not make the split functions `inline` (katachi reads declaration sites off the stack trace).

## When the check fails

- `[UnexpectedFile]` / `[UnexpectedDirectory]`: first ask whether the file is in the right place. If it is a new
  **kind** of file, add a new Role (one Role = one kind of file). Do not widen the `layout { }` of the nearest Role
  unless its `summary` really describes the new file.
- `[UnsatisfiedFileConstraint]`: the file broke the named convention; fix the file, not the constraint.
- A glob `*` matches **one or more** characters (`"Process*"` does not match `Process.kt`); `**` matches zero or more
  directories.
- Two Roles whose patterns select the same file produce an `[AmbiguousLayout]` warning; keep patterns disjoint.
