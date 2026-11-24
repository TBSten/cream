### 0-2. Set up the working directory

The working directory holds the checklist and the report that people open, plus `tmp/` for your notes and logs. You make it yourself by copying one file from this skill. Run everything **from the Gradle root directory**.

**Pick the language of the file.** It is for the user to read: use `ja` when the user works in Japanese, `en` otherwise. These steps stay in English either way. The `katachi` command answers in the language of the checklist you copy (it reads its `<html lang>`).

**Pick the place.** Prefer a place `.gitignore` already ignores, so that the working directory never becomes an `[UnexpectedFile]` in step 4:

```sh
git check-ignore -q .local/ && echo .local    # try .local/, .tmp/, tmp/, .scratch/ in this order. Keep the slash: `.local` misses a `.local/` line until the directory exists
```

Use `<the first ignored one>/install-katachi`. If none is ignored, use `tmp/install-katachi` and **tell the user it is visible to git**: katachi checks files that are untracked but not ignored, so as it is, the working directory itself fails step 4 (and the checklist can end up in their commits). Suggest the line for `.gitignore`: `/tmp/install-katachi/` ignores the working directory and nothing else (the leading `/` ties it to the project root; a bare `tmp/` would match every `tmp` directory in the project). Do not edit `.gitignore` yourself. If the user named a place, use it. If 0-1 already made `<workdir>/tmp/` for `katachi.main.kts` (no Homebrew), keep that place.

**Copy the file.** It holds both the checklist and the codebase report (0-3). `<skill>` is the directory of this skill (the one with `SKILL.md`), `<lang>` is `en` or `ja`:

```sh
mkdir -p <workdir>/tmp
cp <skill>/assets/<lang>/check-list.html <workdir>/check-list.html
```

If you read these steps without the skill installed (from GitHub, say), there is no `<skill>` directory. Download the file instead: `curl -fsSL -o <workdir>/check-list.html https://raw.githubusercontent.com/TBSten/katachi/main/skills/katachi/assets/<lang>/check-list.html`.

**If the file is already there, do not copy over it.** It holds what an earlier session filled in. Read it and go on from where it stops (0-3 says how to read it). A working directory from an older version (one with `project-code-base-report.html` next to the checklist) is the exception: make a new working directory as the step 0 README says, and move the report's JSON from `project-code-base-report.html` into the `report` block of the new `check-list.html`.

The top level of the working directory holds only `check-list.html` and `tmp/`. Notes, logs and anything you fetch go inside `<workdir>/tmp/`. If 0-1 had you put `JAVA_HOME=<path>` in front of the commands, write the prefixed command into `<workdir>/tmp/katachi-command.txt` now (0-1).

**Fill in `meta` of both blocks**, the checklist and the report (0-3 says how to write the JSON). Find the values with these commands:

| Field | How to find it |
|---|---|
| checklist `meta.workdir` | The working directory, relative to the project root (`.local/install-katachi`) |
| checklist `meta.projectRoot` | The absolute path of the Gradle root (`pwd`) |
| checklist `meta.version` | The katachi version: `katachi --version` prints `katachi <version>` |
| checklist `meta.kotlin` | The Kotlin version `katachi doctor` printed in 0-1 (`Kotlin: <version>`, followed by the file it was read from): the version only |
| checklist `meta.git` | `yes` when `git rev-parse --is-inside-work-tree` succeeds, otherwise `no` |
| checklist `meta.moduleDir` | Where the verification module goes, relative to the Gradle root: `architecture-test` unless the user named another place |
| checklist `meta.ranAt` | Now, as `YYYY-MM-DD HH:MM <zone>` |
| report `meta.projectName` | `rootProject.name` in `settings.gradle(.kts)` |
| report `meta.analyzedAt` | Now, as ISO 8601 |
| report `meta.fileCount` | `git ls-files \| wc -l \| tr -d ' '` (a number; macOS's `wc` pads it with spaces) |
| report `meta.gradleRoot` / `meta.gitRoot` | The Gradle root / `git rev-parse --show-toplevel` |

`meta.jvmToolchain` is filled in 2-1 from `scaffold`'s output.

**When Kotlin is too old.** If `doctor` warned something like "This project uses Kotlin 1.9.10 (read from file:///…/build.gradle.kts ). katachi needs Kotlin 2.2 or later" (ja: "このプロジェクトの Kotlin は 1.9.10 です（file:///…/build.gradle.kts から読みました）。katachi は Kotlin 2.2 以降が要ります"), first look at the file it names. If that file does not set the Kotlin version the project builds with (an included build or buildSrc that pins a Kotlin of its own, say), find the version the project's own modules use; when it is 2.2 or later, upgrade nothing: put it in `meta.kotlin` and pass it to `scaffold` in 2-1 with `--kotlin <version>`. Otherwise, ask the user before starting the analysis in step 1. As it is, `scaffold` in 2-1 will stop, and the analysis up to that point is wasted. Upgrading Kotlin changes the existing build, so do not do it without the user's consent. Once they agree, upgrade Kotlin to 2.2 or later. Kotlin Gradle Plugin 2.2 needs Gradle 7.6.3 or later, so if the warning says "Gradle needs upgrading too" (ja: "Gradle も上げる必要があります"), upgrade Gradle as well. Do that with `./gradlew wrapper --gradle-version <version>`, before upgrading Kotlin (the task runs in the existing build), rather than by editing `gradle/wrapper/gradle-wrapper.properties`: the task writes all four wrapper files for that version, as `gradle()` in 3-2 expects, while an edited file leaves a missing `gradlew.bat` missing, and the definition then needs `gradle(requireWrapper = false)`. Fixing build errors caused by the upgrade is also a change to the existing build, so tell the user what you will change before fixing them. Once `./gradlew` builds, run `katachi doctor` again, put the new version in `meta.kotlin`, and record the files you changed in `changedFiles` (0-3). If the user decides not to upgrade, tell them that katachi cannot be installed in this project, and stop there.

`katachi scaffold` and `katachi lint` read `meta.moduleDir`, `meta.projectRoot` and `preferences` from this checklist, so keep them correct. Run those commands in the project root: they find the working directory below the current directory by themselves (from somewhere else, prefix them with `KATACHI_WORKDIR=<workdir>`; from a `git worktree` of the project, they check that worktree instead of `meta.projectRoot`; from a copy of the project made together with its working directory, they check the copy and say so). If they find two working directories, they stop and list both: pick one with `KATACHI_WORKDIR`, or ask the user to move the other one out of the project. Read `:architecture-test:` in these steps as the Gradle path of `meta.moduleDir` (`static-analysis/katachi` is `:static-analysis:katachi`), and `architecture-test/` as `meta.moduleDir`. `--module-dir <path>` on `scaffold` and `lint` overrides `meta.moduleDir` for that run; with `meta.moduleDir` recorded, they do not need it.

**If git is not there (`meta.git` is `no`), tell the user** that the changes cannot be undone if something goes wrong, and suggest committing or backing up first.

**Look for files git sees but does not track.** They become `[UnexpectedFile]` in step 4 too:

```sh
git ls-files --others --exclude-standard --directory --no-empty-directory
```

Leave out the working directory itself. Ask the user, for each of the rest, whether to declare it in the definition or add it to `.gitignore`. For a file the OS writes on its own (`.DS_Store`, `Thumbs.db`, `desktop.ini`), recommend `.gitignore` and do not offer to declare it: a Role for it fails on a checkout without it, such as CI.

Once this is done, mark `0-1` and `0-2` done in the checklist (0-3 says how).
