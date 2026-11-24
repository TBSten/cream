@file:OptIn(ExperimentalKatachiApi::class)

package me.tbsten.cream.test.architecture

import me.tbsten.cream.test.architecture.buildSystem.buildSystemGroup
import me.tbsten.cream.test.architecture.doc.docGroup
import me.tbsten.cream.test.architecture.kmp.kmpGroup
import me.tbsten.cream.test.architecture.library.libraryGroup
import me.tbsten.cream.test.architecture.test.testGroup
import me.tbsten.cream.test.architecture.tool.toolGroup
import me.tbsten.katachi.ExperimentalKatachiApi
import me.tbsten.katachi.dsl.architecture

val projectArchitecture =
    architecture {
        title = "cream.kt のアーキテクチャ"
        description =
            """
            cream.kt は、似たクラス間のコピー関数を生成する KSP プラグイン。
            利用者が依存するアノテーション（cream-runtime）、KSP プロセッサ（cream-ksp：feature → core → util の 3 層）、
            生成コードを呼んで確かめる結合テスト（test）でできている。
            """.trimIndent()

        libraryGroup()
        testGroup()
        docGroup()
        buildSystemGroup()
        kmpGroup()
        toolGroup()
        skillsRole()
    }
