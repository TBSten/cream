---
name: cream
description: Set up and apply cream.kt, a KSP plugin that generates cross-class copy functions (e.g. Loading -> Success, data model -> domain model), in a Kotlin or Kotlin Multiplatform project. Use when adding cream.kt to a module for the first time (KSP plugin, cream-runtime / cream-ksp dependencies, the commonMain workaround KMP requires), or when refactoring existing code to use it — handwritten property copies in ViewModel / StateHolder / Presenter state transitions, or domain <-> data model mapping across layers.
license: Apache-2.0
---

# cream.kt skill

[cream.kt](https://github.com/TBSten/cream) generates copy functions across classes, so a state
transition or a model mapping only has to spell out the values that actually change.

This skill covers two workflows. Pick the one that matches the request, then read **only** that
reference file and follow it.

| The user wants to… | Read |
|---|---|
| Add cream.kt to a module that does not use it yet | [references/setup.md](references/setup.md) |
| Replace handwritten property copies in existing code with cream | [references/refactor.md](references/refactor.md) |

How to tell which applies:

- Look for `me.tbsten.cream` in the target module's build script (or the version catalog). If it
  is absent, start with **setup**, even when the user asked for a refactoring — the refactoring
  needs cream to be set up first. Tell the user you are doing setup first.
- If it is present and the user wants existing code changed, go straight to **refactor**.
- If the request is ambiguous (e.g. "use cream in this project"), do setup if needed, then ask
  whether to continue with the refactoring.

The refactor workflow links to two more files, [references/viewmodel.md](references/viewmodel.md)
and [references/model-mapping.md](references/model-mapping.md). Read them only when the
refactoring targets that kind of code.
