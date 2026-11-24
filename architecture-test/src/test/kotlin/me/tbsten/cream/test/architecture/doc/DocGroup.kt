package me.tbsten.cream.test.architecture.doc

import me.tbsten.katachi.dsl.DeclarationContainerScope

fun DeclarationContainerScope.docGroup() =
    "Doc".group {
        summary = "ドキュメント（README・LICENSE と doc/ の利用者向け・内部向け文書）"

        projectReadmeRole()
        licenseRole()
        userDocRole()
        internalDesignDocRole()
    }
