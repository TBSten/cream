package me.tbsten.cream.test.architecture

import io.kotest.core.spec.style.FreeSpec
import me.tbsten.katachi.ExperimentalKatachiApi
import me.tbsten.katachi.check.FileConstraintCheck
import me.tbsten.katachi.check.assert

/**
 * The one test that checks the whole repository against [projectArchitecture]: every file must belong to a
 * declared role (deny by default), and every `konsist { }` / `fileConstraint { }` of that role must hold.
 * All violations land in this single failure message.
 */
@OptIn(ExperimentalKatachiApi::class)
internal class ProjectArchitectureTest :
    FreeSpec({
        "the project matches its declaration" {
            projectArchitecture.assert(FileConstraintCheck())
        }
    })
