package me.tbsten.cream.test.architecture.buildLogic.conventionDsl

import com.lemonappdev.konsist.api.provider.modifier.KoVisibilityModifierProvider
import me.tbsten.cream.test.architecture.support.CONVENTION_SOURCE_DIR
import me.tbsten.cream.test.architecture.support.packageFollowsDirectory
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.kotlin.ktFile
import me.tbsten.katachi.konsist.konsist

internal fun DeclarationContainerScope.conventionDsl() =
    "ConventionDsl" {
        summary = "Internal helpers the convention plugins share: version catalog and extension accessors"
        example("Gradle")
        layout {
            CONVENTION_SOURCE_DIR / "dsl" / "*".ktFile()
        }
        packageFollowsDirectory()
        "is internal to buildLogic".konsist {
            declarations(includeNested = false, includeLocal = false).must { declaration ->
                (declaration as? KoVisibilityModifierProvider)?.hasInternalModifier ?: true
            }
        }
    }
