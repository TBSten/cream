package me.tbsten.cream.ksp

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import me.tbsten.cream.ksp.options.toCreamOptions
import me.tbsten.cream.ksp.process.processCombineFrom
import me.tbsten.cream.ksp.process.processCombineMapping
import me.tbsten.cream.ksp.process.processCombineTo
import me.tbsten.cream.ksp.process.processCopyFrom
import me.tbsten.cream.ksp.process.processCopyMapping
import me.tbsten.cream.ksp.process.processCopyTo
import me.tbsten.cream.ksp.process.processCopyToChildren

class CreamSymbolProcessor(
    options: Map<String, String>,
    internal val codeGenerator: CodeGenerator,
) : SymbolProcessor {
    internal val options = options.toCreamOptions()

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val invalidTargets = mutableListOf<KSAnnotated>()

        processCopyFrom(resolver)
            .also { invalidTargets.addAll(it) }

        processCopyTo(resolver)
            .also { invalidTargets.addAll(it) }

        processCopyToChildren(resolver)
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
