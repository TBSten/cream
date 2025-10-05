package me.tbsten.cream.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

object AppTextStyles {
    val heading = TextStyle(
        fontSize = 24.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight.Bold,
    )

    val subHeading = TextStyle(
        fontSize = 18.sp,
        lineHeight = 18.sp,
        fontWeight = FontWeight.Bold,
    )

    val body = TextStyle(
        fontSize = 16.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.Normal,
    )

    val description = TextStyle(
        fontSize = 14.sp,
        lineHeight = 16.sp,
        fontWeight = FontWeight.Normal,
    )
}
