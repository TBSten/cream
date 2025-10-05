package me.tbsten.cream.theme

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import me.tbsten.cream.sharedui.generated.resources.NotoSansJP_Bold
import me.tbsten.cream.sharedui.generated.resources.NotoSansJP_Regular
import me.tbsten.cream.sharedui.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.preloadFont


/**
 * デフォルトだと、使用されるフォントの都合で日本語が表示できないワークアラウンド
 * refs: https://github.com/JetBrains/compose-multiplatform/issues/3967#issuecomment-1846879073
 */
@OptIn(ExperimentalResourceApi::class)
@Composable
internal fun FontProvider(
    content: @Composable () -> Unit,
) {
    val regular = preloadFont(Res.font.NotoSansJP_Regular).value
    val bold = preloadFont(Res.font.NotoSansJP_Bold).value

    if (regular != null && bold != null) {
        val fontFamily = FontFamily(regular, bold)

        MaterialTheme(
            typography = Typography(
                displayLarge = MaterialTheme.typography.displayLarge.merge(fontFamily = fontFamily),
                displayMedium = MaterialTheme.typography.displayMedium.merge(fontFamily = fontFamily),
                displaySmall = MaterialTheme.typography.displaySmall.merge(fontFamily = fontFamily),
                headlineLarge = MaterialTheme.typography.headlineLarge.merge(fontFamily = fontFamily),
                headlineMedium = MaterialTheme.typography.headlineMedium.merge(fontFamily = fontFamily),
                headlineSmall = MaterialTheme.typography.headlineSmall.merge(fontFamily = fontFamily),
                titleLarge = MaterialTheme.typography.titleLarge.merge(fontFamily = fontFamily),
                titleMedium = MaterialTheme.typography.titleMedium.merge(fontFamily = fontFamily),
                titleSmall = MaterialTheme.typography.titleSmall.merge(fontFamily = fontFamily),
                bodyLarge = MaterialTheme.typography.bodyLarge.merge(fontFamily = fontFamily),
                bodyMedium = MaterialTheme.typography.bodyMedium.merge(fontFamily = fontFamily),
                bodySmall = MaterialTheme.typography.bodySmall.merge(fontFamily = fontFamily),
                labelLarge = MaterialTheme.typography.labelLarge.merge(fontFamily = fontFamily),
                labelMedium = MaterialTheme.typography.labelMedium.merge(fontFamily = fontFamily),
                labelSmall = MaterialTheme.typography.labelSmall.merge(fontFamily = fontFamily),
            )
        ) {
            content()
        }
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}
