package me.tbsten.cream.test.copyTo

import me.tbsten.cream.CopyTo
import me.tbsten.cream.KDoc

@CopyTo(
    KDocTarget::class,
    kdoc =
        KDoc(
            description = "This function should not be used in the case of ~.",
            examples = [
                """
                # Sample

                ```kt
                val target = source.copyToKDocTarget(extra = 42)
                ```
                """,
            ],
        ),
)
data class KDocSource(
    val shared: String,
    val onlyOnSource: Int,
)

data class KDocTarget(
    val shared: String,
    val extra: Int,
)
