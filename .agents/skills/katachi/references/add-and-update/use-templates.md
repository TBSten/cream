# Generate files from a template, or add a template

A Role can carry a `.template { }` on a file declaration in its `layout { }`. `katachiTemplate` then writes a new file **exactly where that declaration says files of the Role go**, so a generated file causes no violation from the start.

## Generate a file from an existing template

1. Find the template: search the definition for `.template {`. The Role it is in, and its groups, give the `template=` value: the Role's full name (`Group.Role`, `Outer.Inner.Role` with nested groups, or just `Role` outside any group). The Role name alone also works while no other Role has the same name.
2. Read the parameters it takes. A parameter's name is the name of the property it is declared through: in `val name by stringParameter(default = "Sample")` the name is `name`, and the string in the parentheses is only its default value. The same goes for `booleanParameter()` / `intParameter()` / `enumParameter()`. The names given to wildcards with `capture("…")` in the path are parameters too (read inside the template with `captureValue("…")`).
3. Run the check once, generate, then run the check again into a second log. Compare the two logs ([../run-the-check/README.md](../run-the-check/README.md), "Compare two runs"): the difference is then only what the new file did. Against an older run it would mix in other changes.

   ```sh
   mkdir -p build/katachi
   ./gradlew :architecture-test:test --console=plain -Dkatachi.maxViolations=all > build/katachi/test-before-template.log 2>&1
   ./gradlew :architecture-test:katachiTemplate --arg template=Feature.ViewModel --arg feature=home --arg name=Profile
   ./gradlew :architecture-test:test --console=plain -Dkatachi.maxViolations=all > build/katachi/test-after-template.log 2>&1
   ```

   Read `:architecture-test` as the verification module's path.

   **A captured directory's value becomes part of the path as it is, even if no such directory exists.** A typo creates the file in a directory that is not there. Pass the name of one that exists. An argument the template does not declare fails with `Unknown processor argument(s): …`, which is how a typo in a name shows up.

If the generated file is not accepted by the check, the template's path and the Role's layout disagree: that is a bug in the definition, so tell the user rather than moving the file by hand.

## Add a template to a Role

Only for a Role the user agreed to, and only one with many files of the same shape (UseCase, ViewModel, Screen, Repository, …).

The full procedure, with the rules for naming wildcards, is setup section 6-D: [../setup/step6/6-D-templates.md](../setup/step6/6-D-templates.md). Skip its first step if there is no working directory (the report it records into is gone). In short:

- Read the page first: https://tbsten.github.io/katachi/guides/generate-code-from-template/ .
- `.template` is an extension function (`import me.tbsten.katachi.dsl.template`), and `.template { }`, `capture()` and `captureValue()` are experimental (opt in to `ExperimentalKatachiApi`).
- Base its content on a real file of the Role. Do not write the destination path in the template; it comes from the declaration.
- Name every `*` left in the destination with `capture("…")`; a template on a path with an unnamed `*` or `**` fails when layout is expanded.
- Run the check, generate one file and run the check again (as in step 3 above), then delete the generated file unless the user wants to keep it, and run the check once more.
