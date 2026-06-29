package me.tbsten.cream.test.copyMapping

import me.tbsten.cream.CopyMapping
import me.tbsten.cream.test.copyMapping.lib.CrossSource
import me.tbsten.cream.test.copyMapping.lib.CrossTarget

/**
 * The `@CopyMapping` holder is declared here, in a different package than `CrossSource` /
 * `CrossTarget` (which live in `...copyMapping.lib`). issue #145: the generated
 * `CrossSource.copyToCrossTarget(...)` must be emitted into THIS (the holder's) package, so the
 * test can call it without importing it from the library package.
 */
@CopyMapping(CrossSource::class, CrossTarget::class)
private object CrossPackageMapping
