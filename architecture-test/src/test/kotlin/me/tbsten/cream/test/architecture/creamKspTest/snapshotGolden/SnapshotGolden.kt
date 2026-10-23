package me.tbsten.cream.test.architecture.creamKspTest.snapshotGolden

import me.tbsten.katachi.dsl.DeclarationContainerScope

internal fun DeclarationContainerScope.snapshotGolden() =
    "SnapshotGolden" {
        title = "Snapshot golden file"
        summary = "A golden Markdown file compared by `assertMatchesSnapshot`, under one directory per test class"
        description = "Regenerate them with `./gradlew :cream-ksp:test -Dcream.snapshot.update=true`."
        example("CopyToSnapshotTest/UseCase/<topic>.md")
        layout {
            "cream-ksp/src/test/resources/snapshots" / "*Test" / "**" / "*.md".file()
        }
    }
