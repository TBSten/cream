package me.tbsten.cream.ksp.core.copyFun

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import me.tbsten.cream.ksp.core.common.FindMappedSourceProperty
import me.tbsten.cream.ksp.core.common.GenerateSourceAnnotation
import me.tbsten.cream.ksp.core.common.IsExcluded
import me.tbsten.cream.ksp.options.CreamOptions

context(options: CreamOptions, logger: KSPLogger)
internal fun Appendable.appendCopyToSealedClassFunction(
    source: KSClassDeclaration,
    targetClass: KSClassDeclaration,
    generateSourceAnnotation: GenerateSourceAnnotation,
    findMappedSourceProperty: FindMappedSourceProperty,
    isExcluded: IsExcluded,
    skipsObjectTarget: Boolean,
    omitPackages: List<String>,
) {
    // A sealed target only ever produces copy functions whose receiver is the original [source]
    // (the receiver class threaded through the recursion), reaching every transitive concrete
    // leaf. Intermediate sealed nodes are never used as a receiver. Each leaf re-enters
    // appendCopyFunction with the same [skipsObjectTarget], so object leaves are skipped/kept
    // consistently across the whole hierarchy.
    //
    // The leaf KDoc is attributed to the declaration carrying the triggering annotation — the
    // source for @CopyTo / @CopyToChildren but the target/holder for @CopyFrom / @CopyMapping
    // (issue #144) — which the KDoc generator reads from [generateSourceAnnotation] itself
    // ([GenerateSourceAnnotation.annotatedDeclaration]).
    targetClass.getSealedSubclasses().forEach { subclass ->
        appendCopyFunction(
            source,
            subclass,
            generateSourceAnnotation,
            findMappedSourceProperty,
            isExcluded,
            skipsObjectTarget,
            omitPackages,
        )
    }
}
