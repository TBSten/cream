# Follow files that moved

A package was renamed, a module was split or merged, a directory was reorganized. The check now reports the moved files as `[UnexpectedFile]` / `[UnexpectedDirectory]` at their new place, and `[MissingFile]` where a Role declared a single file that is gone.

## First, make sure the move is the intended structure

The check failing after a refactor is katachi doing its job: it noticed that the structure changed. Before you follow the move, confirm that the new place is what the team wants (the user asked for the refactor, or the PR says so). If files moved somewhere that contradicts the architecture, the move is what should be fixed; say so to the user instead of changing the definition.

## Change the layout, not the meaning

- **Rewrite the paths in the Roles that describe the moved files.** Keep their names, `summary`, constraints and grouping; only where the files live changed. If the definition pulls a shared directory out into a `val` or a function, change it there once.
- **A module split in two** usually means one group becomes two groups (or a group with two sub-groups), each holding the Roles of its own files. Do not leave one Role whose layout now lists both modules if the two hold different kinds of files.
- **Modules merged into one**: the Roles stay one kind of file each. Merging their layouts is right only when the files really are the same kind.
- **A wildcard that now matches more than before** (for example `"feature" / "*" / …` after new feature modules appeared) is fine when every match is the same kind of file. Check that the Role's `summary` and constraints still hold for all of them.
- **Package directory and module directory with different names** (module `appConfig`, package `appconfig`): when a layout names the same `*` twice with `capture("feature")`, katachi requires the two places to have the same value and reports `[CaptureMismatch]` otherwise. When the package spells the module's name in another case (`appconfig` for `appConfig`, `userprofile` for `user-profile`), write that conversion at the package level (`capture("feature").flatCase`, `.camelCase`), as the block suggests. Give each its own name if they can differ in other ways.
- **Renamed group or Role**: if the definition is split one Role per file, rename the file and the function too, so the files keep lining up with the architecture.

## Then

Run the loop in [README.md](README.md): compare before and after. The violations that went away should be exactly the moved files; anything else that changed means the layout now says something different from before. Put a stray file in the new location and confirm it still fails.

If the project commits the generated documentation, regenerate it: the pages carry the paths.
