@file:OptIn(ExperimentalKatachiApi::class)

package me.tbsten.cream.test.architecture.support

import me.tbsten.katachi.ExperimentalKatachiApi
import me.tbsten.katachi.dsl.FileConstraintFailure
import me.tbsten.katachi.dsl.FileConstraintFix
import me.tbsten.katachi.dsl.FileConstraintReason
import me.tbsten.katachi.dsl.FileConstraintScope

/**
 * Role の各ファイルに、対になるファイルがプロジェクト内にあることを求める fileConstraint。
 *
 * @param name 制約の名前（違反の表示に出る）
 * @param howToFix 対がないときの直し方
 * @param pairOf ファイルのパスから、対になるファイルのパスを返す。null を返したファイルは確かめない
 */
fun FileConstraintScope.`対になるファイルがある`(
    name: String,
    howToFix: String,
    pairOf: (file: String) -> String?,
) = fileConstraint(name) { subject ->
    val projectFiles = subject.projectFiles.toSet()
    subject.files.mapNotNull { file ->
        val pair = pairOf(file) ?: return@mapNotNull null
        if (pair in projectFiles) {
            null
        } else {
            FileConstraintFailure(
                file = file,
                reasons = listOf(FileConstraintReason("Missing pair", pair)),
                howToFix = listOf(FileConstraintFix(howToFix)),
            )
        }
    }
}
