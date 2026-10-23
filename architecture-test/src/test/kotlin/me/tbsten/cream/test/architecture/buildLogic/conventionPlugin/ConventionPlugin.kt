package me.tbsten.cream.test.architecture.buildLogic.conventionPlugin

import me.tbsten.cream.test.architecture.support.CONVENTION_SOURCE_DIR
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.kotlin.ktFile
import me.tbsten.katachi.konsist.konsist

internal fun DeclarationContainerScope.conventionPlugin() =
    "ConventionPlugin" {
        summary = "A convention plugin class, applied with `id(\"buildLogic.xxx\")`"
        example("LintPlugin", "`buildLogic.lint`: ktlint for every module")
        layout {
            CONVENTION_SOURCE_DIR / "*Plugin".ktFile()
        }
        "implements Plugin<Project>".konsist {
            classes().must { klass -> klass.parents().any { it.name.substringBefore('<') == "Plugin" } }
        }
    }
