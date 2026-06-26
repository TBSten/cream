package me.tbsten.cream.ksp

import com.lemonappdev.konsist.api.verify.assertFalse
import com.lemonappdev.konsist.api.verify.assertTrue
import io.kotest.core.spec.style.FunSpec
import me.tbsten.cream.ksp.testing.konsist.FILE_LINE_LIMIT_OVERRIDES
import me.tbsten.cream.ksp.testing.konsist.KSP_ROOT
import me.tbsten.cream.ksp.testing.konsist.MAX_FILE_LINES
import me.tbsten.cream.ksp.testing.konsist.ROOT_ALLOWED_FILES
import me.tbsten.cream.ksp.testing.konsist.creamKspMain

/**
 * Module-wide Konsist guardrails (issue #130) that apply across every production file in `cream-ksp`,
 * regardless of layer:
 *
 * - the composition root (`me.tbsten.cream.ksp`) holds ONLY the approved infra files, and
 * - every file stays within its line budget (default 300, with a few justified overrides).
 *
 * Layer-specific boundaries live next to the layer they constrain
 * ([me.tbsten.cream.ksp.feature.ArchTest], [me.tbsten.cream.ksp.core.ArchTest]). The authoritative
 * dependency-direction table is in `.claude/rules/ksp-architecture.md`; update these specs whenever
 * it changes. Checks are import-based, matching the project convention of importing referenced
 * symbols rather than using wildcard or fully-qualified inline references.
 */
internal class AllKotlinFilesTest :
    FunSpec(
        {
            context("root レイヤ（composition root）") {
                test("直下には承認済みファイル（CreamSymbolProcessor / Provider / ProcessContext）以外を置かない") {
                    // The root package is the composition root only. Generation logic, helpers, exceptions
                    // (which live in :cream-ksp:shared), etc. must NOT be added here.
                    creamKspMain
                        .filter { it.packagee?.name == KSP_ROOT }
                        .assertTrue { file -> file.nameWithExtension in ROOT_ALLOWED_FILES }
                }
            }

            context("ファイル全般（モジュール共通）") {
                test("1 ファイル原則 $MAX_FILE_LINES 行以内（FILE_LINE_LIMIT_OVERRIDES のファイルは個別上限）") {
                    creamKspMain.assertFalse { file ->
                        val limit = FILE_LINE_LIMIT_OVERRIDES[file.nameWithExtension] ?: MAX_FILE_LINES
                        file.text.lines().size > limit
                    }
                }
            }
        },
    )
