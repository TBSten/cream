package me.tbsten.cream.ksp

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import me.tbsten.cream.ksp.feature.callFrom.processCallFrom
import me.tbsten.cream.ksp.feature.combineFrom.processCombineFrom
import me.tbsten.cream.ksp.feature.combineMapping.processCombineMapping
import me.tbsten.cream.ksp.feature.combineTo.processCombineTo
import me.tbsten.cream.ksp.feature.copyFrom.processCopyFrom
import me.tbsten.cream.ksp.feature.copyMapping.processCopyMapping
import me.tbsten.cream.ksp.feature.copyTo.processCopyTo
import me.tbsten.cream.ksp.feature.copyToChildren.processCopyToChildren
import me.tbsten.cream.ksp.feature.sealedCopy.processSealedCopy
import me.tbsten.cream.ksp.options.CreamOptions
import me.tbsten.cream.ksp.options.toCreamOptions

internal class CreamSymbolProcessor(
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
        val parsed =
            try {
                options
            } catch (e: InvalidCreamOptionException) {
                logger.error(e.message.orEmpty())
                return emptyList()
            }

        // The per-round infrastructure every feature shares. `with(ctx)` exposes it as the context
        // parameter each `processXxx` declares (`context(ctx: ProcessContext)`).
        val processContext = ProcessContext(resolver, parsed, codeGenerator, logger)

        return with(processContext) {
            buildList {
                addAll(processCopyFrom())
                addAll(processCopyTo())
                addAll(processCopyToChildren())
                addAll(processSealedCopy())
                addAll(processCombineTo())
                addAll(processCombineFrom())
                addAll(processCopyMapping())
                addAll(processCombineMapping())
                addAll(processCallFrom())
            }
        }
    }
}
