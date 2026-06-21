@file:OptIn(ExperimentalUnsignedTypes::class)

package me.tbsten.cream.ksp.testing.kotlincodelikestring

import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.isAccessible
import kotlin.time.Duration

/**
 * デフォルトでサポートする値を文字列化する。
 * 標準ライブラリのよく使用する型・任意の data, value class をサポートしている。
 *
 * @see me.tbsten.cream.ksp.testing.kotlincodelikestring.buildKotlinCodeLikeString
 */
internal fun transformBuiltIns(
    value: Any?,
    indent: String,
    className: (Any) -> String,
    fallback: Fallback,
    transform: Transform,
): String? =
    when (value) {
        null -> "null"
        is Unit -> "Unit"
        is Boolean -> value.toString()
        is Char -> "'${escapeChar(value)}'"
        is String -> "\"${escapeString(value)}\""
        is Int -> formatInt(value)
        is Long -> formatLong(value)
        is Short -> formatShort(value)
        is Byte -> formatByte(value)
        is Float -> formatFloat(value)
        is Double -> formatDouble(value)
        is BigDecimal -> "BigDecimal(${buildKotlinCodeLikeString(value.toString(), indent, className, fallback, transform)})"
        is BigInteger -> "BigInteger(${buildKotlinCodeLikeString(value.toString(), indent, className, fallback, transform)})"
        is UInt -> "${buildKotlinCodeLikeString(value, indent, className, fallback, transform)}u"
        is ULong -> "${buildKotlinCodeLikeString(value.toLong(), indent, className, fallback, transform)}uL"
        is UShort -> "${buildKotlinCodeLikeString(value.toShort(), indent, className, fallback, transform)}.toUShort()"
        is UByte -> "${buildKotlinCodeLikeString(value.toByte(), indent, className, fallback, transform)}.toUByte()"
        is Number -> value.toString()
        is Enum<*> -> "${className(value)}.${value.name.escapeKotlinIdentifierIfNeeded()}"
        is LocalDateTime -> "LocalDateTime.parse(${buildKotlinCodeLikeString(value.toString(), indent, className, fallback, transform)})"
        is LocalDate -> "LocalDate.parse(${buildKotlinCodeLikeString(value.toString(), indent, className, fallback, transform)})"
        is LocalTime -> "LocalTime.parse(${buildKotlinCodeLikeString(value.toString(), indent, className, fallback, transform)})"
        is Duration -> "Duration.parseIsoString(${buildKotlinCodeLikeString(value.toIsoString(), indent, className, fallback, transform)})"
        is Throwable -> formatThrowable(value, indent, className, fallback, transform)
        is Pair<*, *> -> {
            val first = buildKotlinCodeLikeString(value.first, indent, className, fallback, transform)
            val second = buildKotlinCodeLikeString(value.second, indent, className, fallback, transform)
            if ('\n' in first || '\n' in second) {
                buildMultiLineCall("Pair", listOf(first, second), indent)
            } else {
                "$first to $second"
            }
        }

        is Triple<*, *, *> ->
            buildMultiLineCall(
                funcName = "Triple",
                items =
                    listOf(
                        buildKotlinCodeLikeString(value.first, indent, className, fallback, transform),
                        buildKotlinCodeLikeString(value.second, indent, className, fallback, transform),
                        buildKotlinCodeLikeString(value.third, indent, className, fallback, transform),
                    ),
                indent = indent,
            )

        is Map<*, *> ->
            buildMultiLineCall(
                funcName = "mapOf",
                items =
                    value.entries.map { (k, v) ->
                        buildKotlinCodeLikeString(k, indent, className, fallback, transform) +
                            " to " +
                            buildKotlinCodeLikeString(v, indent, className, fallback, transform)
                    },
                indent = indent,
            )

        is Set<*> ->
            buildMultiLineCall(
                funcName = "setOf",
                items = value.map { buildKotlinCodeLikeString(it, indent, className, fallback, transform) },
                indent = indent,
            )

        is List<*> ->
            buildMultiLineCall(
                funcName = "listOf",
                items = value.map { buildKotlinCodeLikeString(it, indent, className, fallback, transform) },
                indent = indent,
            )

        is Array<*> ->
            buildMultiLineCall(
                funcName = "arrayOf",
                items = value.map { buildKotlinCodeLikeString(it, indent, className, fallback, transform) },
                indent = indent,
            )

        is IntArray ->
            buildMultiLineCall(
                funcName = "intArrayOf",
                items = value.map { buildKotlinCodeLikeString(it, indent, className, fallback, transform) },
                indent = indent,
            )

        is LongArray ->
            buildMultiLineCall(
                funcName = "longArrayOf",
                items = value.map { buildKotlinCodeLikeString(it, indent, className, fallback, transform) },
                indent = indent,
            )

        is ShortArray ->
            buildMultiLineCall(
                funcName = "shortArrayOf",
                items = value.map { buildKotlinCodeLikeString(it, indent, className, fallback, transform) },
                indent = indent,
            )

        is ByteArray ->
            buildMultiLineCall(
                funcName = "byteArrayOf",
                items = value.map { buildKotlinCodeLikeString(it, indent, className, fallback, transform) },
                indent = indent,
            )

        is FloatArray ->
            buildMultiLineCall(
                funcName = "floatArrayOf",
                items = value.map { buildKotlinCodeLikeString(it, indent, className, fallback, transform) },
                indent = indent,
            )

        is DoubleArray ->
            buildMultiLineCall(
                funcName = "doubleArrayOf",
                items = value.map { buildKotlinCodeLikeString(it, indent, className, fallback, transform) },
                indent = indent,
            )

        is CharArray ->
            buildMultiLineCall(
                funcName = "charArrayOf",
                items = value.map { buildKotlinCodeLikeString(it, indent, className, fallback, transform) },
                indent = indent,
            )

        is UIntArray ->
            buildMultiLineCall(
                funcName = "uIntArrayOf",
                items = value.map { "${buildKotlinCodeLikeString(it, indent, className, fallback, transform)}u" },
                indent = indent,
            )

        is ULongArray ->
            buildMultiLineCall(
                funcName = "uLongArrayOf",
                items = value.map { "${buildKotlinCodeLikeString(it.toLong(), indent, className, fallback, transform)}uL" },
                indent = indent,
            )

        is UShortArray ->
            buildMultiLineCall(
                funcName = "uShortArrayOf",
                items = value.map { "${buildKotlinCodeLikeString(it.toShort(), indent, className, fallback, transform)}.toUShort()" },
                indent = indent,
            )

        is UByteArray ->
            buildMultiLineCall(
                funcName = "uByteArrayOf",
                items = value.map { "${buildKotlinCodeLikeString(it.toByte(), indent, className, fallback, transform)}.toUByte()" },
                indent = indent,
            )

        is KClass<*> -> formatKClass(value, className)
        else -> {
            val kClass = value::class

            when {
                kClass.objectInstance != null -> className(value)
                kClass.isData || kClass.isValue ->
                    buildClassExpression(value, indent, className, fallback, transform)

                else -> transform(value)
            }
        }
    }

private fun buildMultiLineCall(
    funcName: String,
    items: List<String>,
    indent: String,
): String {
    if (items.isEmpty()) return "${emptyFuncFor(funcName)}()"

    val body =
        items
            .joinToString(separator = "\n") {
                "${it.withPrefixEachLines(indent)},"
            }
    return "$funcName(\n$body\n)"
}

private fun emptyFuncFor(funcName: String): String =
    when (funcName) {
        "listOf" -> "emptyList"
        "setOf" -> "emptySet"
        "mapOf" -> "emptyMap"
        "arrayOf" -> "emptyArray"
        else -> funcName
    }

private fun formatInt(value: Int): String =
    when (value) {
        Int.MAX_VALUE -> "Int.MAX_VALUE"
        Int.MIN_VALUE -> "Int.MIN_VALUE"
        else -> value.toString()
    }

private fun formatLong(value: Long): String =
    when (value) {
        Long.MAX_VALUE -> "Long.MAX_VALUE"
        Long.MIN_VALUE -> "Long.MIN_VALUE"
        else -> "${value}L"
    }

private fun formatShort(value: Short): String =
    when (value) {
        Short.MAX_VALUE -> "Short.MAX_VALUE"
        Short.MIN_VALUE -> "Short.MIN_VALUE"
        else -> value.toString()
    }

private fun formatByte(value: Byte): String =
    when (value) {
        Byte.MAX_VALUE -> "Byte.MAX_VALUE"
        Byte.MIN_VALUE -> "Byte.MIN_VALUE"
        else -> value.toString()
    }

private fun formatFloat(value: Float): String =
    when {
        value.isNaN() -> "Float.NaN"
        value == Float.POSITIVE_INFINITY -> "Float.POSITIVE_INFINITY"
        value == Float.NEGATIVE_INFINITY -> "Float.NEGATIVE_INFINITY"
        value == Float.MAX_VALUE -> "Float.MAX_VALUE"
        value == Float.MIN_VALUE -> "Float.MIN_VALUE"
        else -> "${value}f"
    }

private fun formatDouble(value: Double): String =
    when {
        value.isNaN() -> "Double.NaN"
        value == Double.POSITIVE_INFINITY -> "Double.POSITIVE_INFINITY"
        value == Double.NEGATIVE_INFINITY -> "Double.NEGATIVE_INFINITY"
        value == Double.MAX_VALUE -> "Double.MAX_VALUE"
        value == Double.MIN_VALUE -> "Double.MIN_VALUE"
        else -> value.toString()
    }

private fun buildClassExpression(
    value: Any,
    indent: String,
    className: (Any) -> String,
    fallback: Fallback,
    transform: Transform,
): String {
    val kClass = value::class
    val params = kClass.primaryConstructor?.parameters ?: emptyList()
    val properties = kClass.memberProperties.associateBy { it.name }
    val parts =
        params.mapNotNull { parameter ->
            val name = parameter.name ?: return@mapNotNull null
            val property = properties[name] ?: return@mapNotNull null
            property.isAccessible = true

            @Suppress("UNCHECKED_CAST")
            val propValue = (property as KProperty1<Any, *>).get(value)
            "${name.escapeKotlinIdentifierIfNeeded()} = ${buildKotlinCodeLikeString(propValue, indent, className, fallback, transform)}"
        }
    if (parts.isEmpty()) return "${className(value)}()"
    val body =
        parts
            .joinToString(separator = "\n") {
                "${it.withPrefixEachLines(indent)},"
            }
    return "${className(value)}(\n$body\n)"
}

private fun formatThrowable(
    value: Throwable,
    indent: String,
    className: (Any) -> String,
    fallback: Fallback,
    transform: Transform,
): String {
    val cls = className(value)
    val msg = value.message
    return if (msg == null) {
        "$cls()"
    } else {
        "$cls(${buildKotlinCodeLikeString(msg, indent, className, fallback, transform)})"
    }
}

private fun formatKClass(
    value: KClass<*>,
    className: (KClass<*>) -> String,
): String = "${className(value)}::class"
