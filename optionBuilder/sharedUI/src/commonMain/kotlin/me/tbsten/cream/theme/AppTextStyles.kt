package me.tbsten.cream.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import me.tbsten.cream.util.adaptive

object AppTextStyles {
    val appTitle
        @Composable
        get() =
            adaptive(
                small =
                    TextStyle(
                        brush = MainGradient,
                        fontSize = 40.sp,
                        lineHeight = 40.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                large =
                    TextStyle(
                        brush = MainGradient,
                        fontSize = 48.sp,
                        lineHeight = 48.sp,
                        fontWeight = FontWeight.Bold,
                    ),
            )

    val heading =
        TextStyle(
            fontSize = 24.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Bold,
        )

    val subHeading =
        TextStyle(
            fontSize = 18.sp,
            lineHeight = 18.sp,
            fontWeight = FontWeight.Bold,
        )

    val body =
        TextStyle(
            fontSize = 16.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Normal,
        )

    val description =
        TextStyle(
            fontSize = 14.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight.Normal,
        )
}
