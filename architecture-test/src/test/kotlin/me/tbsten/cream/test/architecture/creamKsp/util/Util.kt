package me.tbsten.cream.test.architecture.creamKsp.util

import me.tbsten.cream.test.architecture.creamKsp.util.genericUtil.genericUtil
import me.tbsten.cream.test.architecture.creamKsp.util.kspUtil.kspUtil
import me.tbsten.katachi.dsl.DeclarationContainerScope

internal fun DeclarationContainerScope.util() =
    "util".group {
        title = "util layer"
        summary = "Generic helpers that hold nothing cream-specific"

        genericUtil()
        kspUtil()
    }
