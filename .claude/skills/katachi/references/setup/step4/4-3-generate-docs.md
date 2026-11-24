### 4-3. Generate the documentation

The same definition can generate a page (Markdown) per Role. Running it also confirms that the Gradle plugin `scaffold` wired in (`katachi { architecture = ... }`) works, so run it once here.

```sh
./gradlew :architecture-test:katachiDocs
```

The output goes to `architecture-test/build/katachi/docs/`. Check that the index `README.md` and a page per Role are there. It is under `build/`, so nothing gets committed. Committing it is handled in 6-C; do not change the output directory here.

Once done, mark `4-1`, `4-2` and `4-3` done, and go on to step 5.
