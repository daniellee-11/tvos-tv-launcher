package com.workbuddy.tvlauncher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

/**
 * 电视启动器主入口 —— 注册为 HOME / LEANBACK_LAUNCHER。
 */
class MainActivity : ComponentActivity() {

    private lateinit var prefs: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = SettingsRepository(this)
        setContent {
            val settings by prefs.settings.collectAsState()
            TVLauncherTheme(accent = accentOf(settings.accentColorIndex).color) {
                LauncherScreen(settings, prefs)
            }
        }
    }
}
