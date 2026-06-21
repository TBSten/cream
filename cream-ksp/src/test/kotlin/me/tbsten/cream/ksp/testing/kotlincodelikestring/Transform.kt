package me.tbsten.cream.ksp.testing.kotlincodelikestring

fun interface Transform : (Any?) -> String? {
    companion object None : Transform {
        override fun invoke(p1: Any?): String? = null
    }
}

fun Transform.transform(next: Transform): Transform = Transform { this(it) ?: next(it) }

inline fun <reified T : Any> Transform.transform(crossinline transform: (T) -> String?): Transform = transform { value -> if (value is T) transform(value) else null }
