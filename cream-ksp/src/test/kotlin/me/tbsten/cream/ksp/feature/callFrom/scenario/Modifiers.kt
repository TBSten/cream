package me.tbsten.cream.ksp.feature.callFrom.scenario

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.KModifier.INFIX
import com.squareup.kotlinpoet.KModifier.INLINE
import com.squareup.kotlinpoet.KModifier.OPERATOR
import com.squareup.kotlinpoet.KModifier.TAILREC
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.STRING
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.classNameOf
import me.tbsten.cream.ksp.testing.poet.clazz
import me.tbsten.cream.ksp.testing.poet.dataClass
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

/**
 * `operator` / `infix` / `inline` / `tailrec` on the annotated function. None of them is
 * transcribed onto the bridge: the bridge's shape (an extra leading source parameter) no longer
 * satisfies the operator / infix conventions, and the bridge is an ordinary call site of the
 * original, so `inline` / `tailrec` still apply inside the original. The `Output:ExitCode OK`
 * facet proves each generated bridge (including the plain-call delegation to the modified
 * function) compiles.
 */
internal fun modifiersScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        "operatorFunction" to
            SnapshotScenario(
                clazz("Counter", Prop("value", INT))
                    .toBuilder()
                    .addFunction(
                        FunSpec
                            .builder("plus")
                            .addModifiers(OPERATOR)
                            .addParameter("other", INT)
                            .returns(classNameOf("Counter"))
                            .addStatement("return Counter(value + other)")
                            .build()
                            .withCallFrom(classNameOf("AddArgs")),
                    ).build(),
                dataClass("AddArgs", Prop("other", INT)),
            ),
        "infixFunction" to
            SnapshotScenario(
                clazz("Joiner", Prop("value"))
                    .toBuilder()
                    .addFunction(
                        FunSpec
                            .builder("join")
                            .addModifiers(INFIX)
                            .addParameter("other", STRING)
                            .returns(STRING)
                            .addStatement("return value + other")
                            .build()
                            .withCallFrom(classNameOf("JoinArgs")),
                    ).build(),
                dataClass("JoinArgs", Prop("other")),
            ),
        // The bridge passes its (non-literal) lambda parameter into the inline function's lambda
        // parameter, which kotlinc accepts; `reified` is the only inline feature a bridge cannot
        // forward (diagnostic, covered in CallFromInvalidUsageTest).
        "inlineFunction" to
            callFrom(
                FunSpec
                    .builder("runTagged")
                    .addModifiers(INLINE)
                    .addParameter("tag", STRING)
                    .addParameter("block", LambdaTypeName.get(returnType = STRING))
                    .returns(STRING)
                    .addStatement("return tag + block()")
                    .build(),
                dataClass("RunArgs", Prop("tag")),
            ),
        "tailrecFunction" to
            callFrom(
                FunSpec
                    .builder("countDown")
                    .addModifiers(TAILREC)
                    .addParameter("n", INT)
                    .returns(INT)
                    .addStatement("return if (n <= 0) 0 else countDown(n - 1)")
                    .build(),
                dataClass("CountArgs", Prop("n", INT)),
            ),
    )
