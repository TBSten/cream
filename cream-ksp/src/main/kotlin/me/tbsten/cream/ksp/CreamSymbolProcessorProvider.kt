package me.tbsten.cream.ksp

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

public class CreamSymbolProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor =
        CreamSymbolProcessor(
            rawOptions = environment.options,
            codeGenerator = environment.codeGenerator,
            logger = environment.logger,
        )
}
