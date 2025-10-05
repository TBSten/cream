package me.tbsten.cream.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import com.materialkolor.DynamicMaterialTheme
import com.materialkolor.PaletteStyle
import com.materialkolor.rememberDynamicMaterialThemeState

@Composable
fun AppTheme(
    content: @Composable () -> Unit,
) {
    val dynamicThemeState = rememberDynamicMaterialThemeState(
        isDark = isSystemInDarkTheme(),
        style = PaletteStyle.TonalSpot,
        seedColor = SeedColor,
    )

    MaterialTheme {
        CompositionLocalProvider(
            LocalTextStyle provides AppTextStyles.body,
        ) {
            PlatformAppTheme {
                DynamicMaterialTheme(
                    state = dynamicThemeState,
                    animate = true,
                ) {
                    Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier.fillMaxSize()) {
                        content()
                    }
                }
            }
        }
    }
}

@Composable
internal expect fun PlatformAppTheme(content: @Composable () -> Unit)