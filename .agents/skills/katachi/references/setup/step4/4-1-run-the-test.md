### 4-1. Run the test

Run the check and read its result as [run-the-check](../../run-the-check/README.md) says (`./gradlew :architecture-test:test` with the log kept). With 0 violations the log has `BUILD SUCCESSFUL` and no `Katachi check failed`. If the check passes with warnings (`[AmbiguousLayout]` and the like, which never fail the check), the log has a `Katachi check found N warnings` line and the warnings' headings. Decide whether to fix or keep warnings by the policy in 4-2 as well, and if you keep them, say so in the warning on step 4 (0-3, "Progress").
