package me.tbsten.cream.test.architecture.documentation

import me.tbsten.cream.test.architecture.documentation.aiIntegrationGuide.aiIntegrationGuide
import me.tbsten.cream.test.architecture.documentation.context7Config.context7Config
import me.tbsten.cream.test.architecture.documentation.customizationGuide.customizationGuide
import me.tbsten.cream.test.architecture.documentation.designDoc.designDoc
import me.tbsten.cream.test.architecture.documentation.distributedSkill.distributedSkill
import me.tbsten.cream.test.architecture.documentation.docImage.docImage
import me.tbsten.cream.test.architecture.documentation.featureGuide.featureGuide
import me.tbsten.cream.test.architecture.documentation.featureOptionMatrix.featureOptionMatrix
import me.tbsten.cream.test.architecture.documentation.license.license
import me.tbsten.cream.test.architecture.documentation.projectReadme.projectReadme
import me.tbsten.cream.test.architecture.documentation.useCaseGuide.useCaseGuide
import me.tbsten.katachi.dsl.DeclarationContainerScope

internal fun DeclarationContainerScope.documentation() =
    "documentation".group {
        title = "Documentation"
        summary = "User-facing documentation: English, with a Japanese counterpart (`*.ja.md`) next to each page"
        description =
            """
            markdown 付近の対応する `.md` ファイルも合わせて更新する。例えば `README.md` を更新したら `README.ja.md` も必ず更新する
            （対になっていることは `has its Japanese counterpart` 制約が検査する）。

            ただし `skills/` 配下は AI Agent 向けなので英語 1 本とし、`.ja.md` は作らない。SKILL.md と、そこから参照される `.md` が対象
            （`has no Japanese counterpart` 制約が検査する）。
            """.trimIndent()

        projectReadme()
        license()
        featureGuide()
        customizationGuide()
        useCaseGuide()
        aiIntegrationGuide()
        distributedSkill()
        docImage()
        designDoc()
        featureOptionMatrix()
        context7Config()
    }
