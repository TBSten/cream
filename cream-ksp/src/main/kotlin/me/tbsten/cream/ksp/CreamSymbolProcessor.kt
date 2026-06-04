package me.tbsten.cream.ksp

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import me.tbsten.cream.ksp.options.CreamOptions
import me.tbsten.cream.ksp.options.toCreamOptions
import me.tbsten.cream.ksp.process.processCombineFrom
import me.tbsten.cream.ksp.process.processCombineMapping
import me.tbsten.cream.ksp.process.processCombineTo
import me.tbsten.cream.ksp.process.processCopyFrom
import me.tbsten.cream.ksp.process.processCopyMapping
import me.tbsten.cream.ksp.process.processCopyTo
import me.tbsten.cream.ksp.process.processCopyToChildren
import me.tbsten.cream.ksp.process.processSealedCopy

class CreamSymbolProcessor(
    private val rawOptions: Map<String, String>,
    internal val codeGenerator: CodeGenerator,
    internal val logger: KSPLogger,
) : SymbolProcessor {
    // Parsed lazily so an invalid KSP option value surfaces as a clean COMPILATION_ERROR from
    // process() (where the logger is used), rather than as a constructor crash that KSP reports as
    // an INTERNAL_ERROR. Backing field is set once on first successful parse.
    private var parsedOptions: CreamOptions? = null
    internal val options: CreamOptions
        get() = parsedOptions ?: rawOptions.toCreamOptions().also { parsedOptions = it }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        // Validate options up front. An invalid value is a build-script mistake with no source
        // location, so it is reported via logger.error(message) without a KSNode, and processing is
        // skipped entirely for this round (no partial files).
        try {
            options
        } catch (e: InvalidCreamOptionException) {
            logger.error(e.message.orEmpty())
            return emptyList()
        }

        val invalidTargets = mutableListOf<KSAnnotated>()

        processCopyFrom(resolver)
            .also { invalidTargets.addAll(it) }

        processCopyTo(resolver)
            .also { invalidTargets.addAll(it) }

        processCopyToChildren(resolver)
            .also { invalidTargets.addAll(it) }

        processSealedCopy(resolver)
            .also { invalidTargets.addAll(it) }

        processCombineTo(resolver)
            .also { invalidTargets.addAll(it) }

        processCombineFrom(resolver)
            .also { invalidTargets.addAll(it) }

        processCopyMapping(resolver)
            .also { invalidTargets.addAll(it) }

        processCombineMapping(resolver)
            .also { invalidTargets.addAll(it) }

        return invalidTargets
    }
}
