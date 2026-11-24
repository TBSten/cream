package me.tbsten.cream.test.architecture

/**
 * cream-ksp の feature/<name>/ に置けるアノテーション名の一覧。アノテーションを足したらここにも足す。
 * FeatureProcess（main）と FeatureTest（test）の layout が、この一覧のディレクトリだけを許可する。
 */
val features =
    listOf(
        "copyTo",
        "copyFrom",
        "copyToChildren",
        "copyMapping",
        "combineTo",
        "combineFrom",
        "combineMapping",
        "parentOptional",
        "childOptionals",
        "sealedCopy",
    )
