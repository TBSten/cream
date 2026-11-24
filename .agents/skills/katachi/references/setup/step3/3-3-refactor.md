### 3-3. Refactor the definition

Tidy up the 3-2 definition **without changing the check result.** When unsure about an API, read the llms.txt in "When unsure about an API" in 3-1.

**Refactoring checklist** — go from the top; skip anything that does not apply.

- [ ] **Files and packages are split.** If `oneRolePerFile` from 1-3 is `yes`: one Role per file (`<group>/<Role>Role.kt`), one group per file (`<group>/<Group>Group.kt`), and `ProjectArchitecture.kt` only calls the groups. The naming is in "Where the definition files go" below. If it is `no`, skip this item
- [ ] **No group or directory name matches `.gitignore`.** Do not name a group `Build` or `Out`: its directory becomes `build/` or `out/` (use e.g. `BuildLogic`). After splitting, run `git ls-files --others --ignored --exclude-standard <verification module>/src` and confirm it prints nothing (no definition file is ignored). An ignored file never shows up in `gitTracked()`, so a gap in the definition goes unnoticed
- [ ] **Role and group names are UpperCamelCase.** `"Feature".group { }`, not `"feature".group { }` ("One Role = one kind of file" in 3-2). `katachi lint` lists the names that start lowercase
- [ ] **The functions used for splitting are not `inline`.** katachi reads a violation's declaration site off the stack trace, and `inline` shifts the line numbers
- [ ] **The split definition files cause no violations.** The definition files are checked too. The `layout { }` of the Role that covers the verification module accepts files as deep as the deepest definition file (e.g. `<group>/<sub>/<Role>Role.kt`: the depth of the nested groups + 1)
- [ ] **One Role = one kind of file.** Every Role has been gone over with the three questions of "One Role = one kind of file" in 3-2. `katachi lint` prints no `Roles that seem to hold files of different kinds` line (ja: 「種類の違うファイルを1つの Role に抱えていそうなもの」), or the reason for keeping each Role it lists is in the warning on step 3 (0-3, "Progress")
- [ ] **Gradle is the one line `gradle()`.** No Gradle files are declared in hand-written Roles (except `buildSrc` and builds added with `includeBuild`). Constraints on build files (the ones marked `constraintOnly` in the report) were written in a hook such as `gradle { buildScript { } }` (a constraint that looks at one module only narrows `subject.files` by path)
- [ ] **No catch-all Roles are left.** Roles that gather `anyFile()` / `ignore()`, or whose `layout` means "anything, as long as it is here", have a file name pattern or a constraint on the contents (`ktFileContent { }`) ("Do not stop at placement" in 3-2)
- [ ] **The descriptions are complete.** `title` / `summary` / `description` on Roles and groups, and `example()` (the name of a real file) on Roles. Take the content from `roles` in the step 1 report. The Roles you decided to give `documented = false` by the 1-2 guidelines say so. A group whose Roles are all `documented = false` says so too; otherwise the generated documentation gets a page for it with only its heading and summary
- [ ] **Duplication is shared.** The same `layout` is not copied into several Roles (pull it out into a function). A value that keeps coming up, like a package directory, is pulled out into a `val`
- [ ] **The check result is the same before and after.** "Compare before and after" below ends with "has not changed"

<details>
<summary>Where the definition files go (naming)</summary>

**Unless the user says otherwise, follow this naming.** The files then line up the way the architecture does, so finding where a Role is written takes no searching.

```
<package>/test/architecture/
├── ProjectArchitecture.kt            the architecture { } itself; only calls the group functions
├── <Role>Role.kt                     a Role outside any group   e.g. ProjectReadmeRole.kt
├── <group>/<Group>Group.kt           a group's definition       e.g. app/AppGroup.kt
├── <group>/<Role>Role.kt             a Role the group calls     e.g. app/EntrypointRole.kt
└── <group>/<sub>/<Sub>Group.kt       a nested group's definition  e.g. domain/model/ModelGroup.kt
```

- Directory names are the group name with a lowercase first letter (group `Domain` → `domain/`; the group name itself stays UpperCamelCase). Roles get no directory of their own. File names are `<Group>Group.kt` for a group (`AppGroup.kt`) and `<Role>Role.kt` for a Role (`EntrypointRole.kt`). Function names start lowercase (`fun DeclarationContainerScope.appGroup()`, `entrypoint()`). The package declaration follows the directory
- A group inside a group goes one level deeper the same way (`domain/model/ModelGroup.kt`, and a Role inside it is `domain/model/EntityRole.kt`). If `nestedGroups` from 1-3 is `no`, keep groups to one level and tell them apart by name instead of nesting
- A Role outside any group is `<Role>Role.kt` directly under the definition root (e.g. `ProjectReadmeRole.kt`). Any other Role goes in the directory of the group that calls it
- Each file holds **exactly one** group or Role. A group's file only calls the functions of the Roles under it
- Make the split functions extension functions on `DeclarationContainerScope`, and import them where they are called (an extension function cannot be called by its fully qualified name)

</details>

#### Compare before and after

Once tidied up, run it again the same way as in 3-2 and compare.

```sh
./gradlew :architecture-test:test --console=plain -Dkatachi.maxViolations=all > build/katachi/test-after-refactor.log 2>&1; echo "gradle exit status: $?"
```

Then compare the two logs as "Compare two runs" in [run-the-check](../../run-the-check/README.md) says. It compares the check result: `[Kind] path` for each violation. It does not compare the line numbers of declaration sites. It says nothing about `summary` or `description`: a Role without them is not reported, so check the descriptions item above by reading the definition. **If there is a difference, fix the definition rather than taking the earlier log again.** The refactoring has changed the behavior.

If either log shows only the first few violations (it has a `Showing first ...` line), the comparison is not valid, because violations missing from the log cannot be compared. `-Dkatachi.maxViolations=all` makes the log hold them all, so this does not normally happen. If it does, capture the cut-off log again.

**If the earlier log was green (0 violations), this is 0 against 0, and it cannot tell you that the split loosened the definition (a catch-all crept in, say).** In that case, also make one known violation after the split and confirm it fails: put a file that no Role covers somewhere temporarily, run the check, and once it fails with `[UnexpectedFile]`, delete that file.

Record the files you created or changed in `changedFiles` (the split files can be one entry by directory; files already recorded need not be recorded again), and run `katachi lint` again. `lint` does not fail on what it finds (it exits 0; 1 only when the definition directory is missing) and prints a count per inspection, so read its lines rather than its exit code:

- **Fix these, or write the reason for each one you keep in the warning on step 3**: `Roles that seem to hold files of different kinds`, `Gradle files are declared in hand-written Roles`, `Role and group names starting with a lowercase letter`, and, against the 1-3 preferences, `Flat groups` and `Files not split` (ja: 「種類の違うファイルを1つの Role に抱えていそうなもの」, 「Gradle のファイルを手書きの Role で宣言しています」 / 「Gradle のファイルを手書きの Role で宣言し、その Role に制約を付けています」, 「小文字で始まる Role・group の名前」, 「平たい group」, 「分けていないファイル」). An inspection with nothing to report prints no line; when none has anything, `lint` prints only `Role check: no role seems to hold files of different kinds` (ja: 「種類の違うファイルを抱えていそうな Role は見つかりませんでした」)
- **Review, not a count to bring to 0**: `Candidates for documented = false` (ja: 「documented = false の候補」). Set `documented = false` on the ones the 1-2 guidelines name, and leave the rest

Once each line is dealt with, mark `3-3` done.
