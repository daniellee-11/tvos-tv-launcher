package com.workbuddy.tvlauncher

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/** tvOS 风格强调色（可在设置中切换） */
data class AccentOption(val name: String, val color: Color)

val ACCENT_OPTIONS = listOf(
    AccentOption("铂金白", Color(0xFFE8EAED)),
    AccentOption("经典蓝", Color(0xFF2F6FED)),
    AccentOption("活力橙", Color(0xFFF76B15)),
    AccentOption("神秘紫", Color(0xFF8E4EC6)),
    AccentOption("翡翠绿", Color(0xFF30A46C)),
    AccentOption("玫瑰红", Color(0xFFE5484D))
)

fun accentOf(index: Int): AccentOption =
    ACCENT_OPTIONS[index.coerceIn(0, ACCENT_OPTIONS.lastIndex)]

/** tvOS 风格深色壁纸（可在设置中切换） */
data class WallpaperOption(val name: String, val brush: Brush)

val WALLPAPER_OPTIONS = listOf(
    WallpaperOption(
        "深空黑", Brush.verticalGradient(
            listOf(Color(0xFF16181D), Color(0xFF0B0C0F))
        )
    ),
    WallpaperOption(
        "午夜蓝", Brush.verticalGradient(
            listOf(Color(0xFF0D1B3E), Color(0xFF060B18))
        )
    ),
    WallpaperOption(
        "暗夜紫", Brush.verticalGradient(
            listOf(Color(0xFF2B1B4D), Color(0xFF120A24))
        )
    ),
    WallpaperOption(
        "松石绿", Brush.verticalGradient(
            listOf(Color(0xFF0C3B3E), Color(0xFF071A1C))
        )
    ),
    WallpaperOption(
        "熔岩红", Brush.verticalGradient(
            listOf(Color(0xFF3B0D0D), Color(0xFF170404))
        )
    )
)

fun wallpaperOf(index: Int): WallpaperOption =
    WALLPAPER_OPTIONS[index.coerceIn(0, WALLPAPER_OPTIONS.lastIndex)]

@Composable
fun TVLauncherTheme(accent: Color, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(primary = accent, background = Color(0xFF0B0C0F)),
        content = content
    )
}
