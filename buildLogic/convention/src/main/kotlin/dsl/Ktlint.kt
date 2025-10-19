package dsl

import org.gradle.api.Project
import org.jlleitschuh.gradle.ktlint.KtlintExtension

internal fun Project.ktlint(actions: KtlintExtension.() -> Unit) =
    extensions.configure(KtlintExtension::class.java, actions)
