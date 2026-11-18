package com.workbuddy.tvlauncher

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 「设置」面板 —— 启动器的全部界面调整都集中在这里：
 * 卡片尺寸 / 圆角 / 焦点缩放 / 氛围光 / 强调色 / 壁纸 / 时钟 / Top Shelf / 内容行 / 时间制式 / 动画速度。
 * 所有调整即时生效并持久化保存。
 */
@Composable
fun SettingsPanel(
    settings: LauncherSettings,
    prefs: SettingsRepository,
    accent: Color,
    onClose: () -> Unit
) {
    val firstRowFocus = remember { FocusRequester() }
    LaunchedEffect(Unit) { firstRowFocus.requestFocus() }

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.55f))
    ) {
        Column(
            Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight()
                .width(560.dp)
                .background(Color(0xF21C1C22))
                .border(1.dp, Color.White.copy(alpha = 0.10f), RoundedCornerShape(0.dp))
                .padding(horizontal = 40.dp, vertical = 28.dp)
        ) {
            Text("设置", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
            Text(
                "全部界面调整都在这里 · 左右方向键调整数值",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 14.sp
            )
            Spacer(Modifier.height(20.dp))

            Column(
                Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // —— 卡片尺寸 ——
                StepperRow(
                    modifier = Modifier.focusRequester(firstRowFocus),
                    label = "卡片宽度",
                    value = "${settings.cardWidthDp} dp",
                    onMinus = { if (settings.cardWidthDp > 110) prefs.update { it.copy(cardWidthDp = it.cardWidthDp - 10) } },
                    onPlus = { if (settings.cardWidthDp < 230) prefs.update { it.copy(cardWidthDp = it.cardWidthDp + 10) } },
                    accent = accent
                )
                StepperRow(
                    label = "卡片高度",
                    value = "${settings.cardHeightDp} dp",
                    onMinus = { if (settings.cardHeightDp > 160) prefs.update { it.copy(cardHeightDp = it.cardHeightDp - 10) } },
                    onPlus = { if (settings.cardHeightDp < 290) prefs.update { it.copy(cardHeightDp = it.cardHeightDp + 10) } },
                    accent = accent
                )
                StepperRow(
                    label = "圆角半径",
                    value = "${settings.cornerRadiusDp} dp",
                    onMinus = { if (settings.cornerRadiusDp > 0) prefs.update { it.copy(cornerRadiusDp = it.cornerRadiusDp - 3) } },
                    onPlus = { if (settings.cornerRadiusDp < 36) prefs.update { it.copy(cornerRadiusDp = it.cornerRadiusDp + 3) } },
                    accent = accent
                )
                StepperRow(
                    label = "焦点缩放",
                    value = "${"%.0f".format(settings.focusScale * 100)} %",
                    onMinus = { if (settings.focusScale > 1.00f) prefs.update { it.copy(focusScale = (it.focusScale - 0.02f).coerceAtLeast(1.00f)) } },
                    onPlus = { if (settings.focusScale < 1.20f) prefs.update { it.copy(focusScale = (it.focusScale + 0.02f).coerceAtMost(1.20f)) } },
                    accent = accent
                )
                StepperRow(
                    label = "背景氛围光",
                    value = "${"%.0f".format(settings.glowStrength * 100)} %",
                    onMinus = { if (settings.glowStrength > 0f) prefs.update { it.copy(glowStrength = (it.glowStrength - 0.1f).coerceAtLeast(0f)) } },
                    onPlus = { if (settings.glowStrength < 1f) prefs.update { it.copy(glowStrength = (it.glowStrength + 0.1f).coerceAtMost(1f)) } },
                    accent = accent
                )

                OptionRow(
                    label = "强调色",
                    options = ACCENT_OPTIONS.map { it.name },
                    selectedIndex = settings.accentColorIndex,
                    onSelect = { next -> prefs.update { s -> s.copy(accentColorIndex = next) } },
                    accent = accent
                )
                OptionRow(
                    label = "壁纸",
                    options = WALLPAPER_OPTIONS.map { it.name },
                    selectedIndex = settings.wallpaperIndex,
                    onSelect = { next -> prefs.update { s -> s.copy(wallpaperIndex = next) } },
                    accent = accent
                )
                OptionRow(
                    label = "动画速度",
                    options = listOf("快", "标准", "慢"),
                    selectedIndex = settings.animSpeedIndex,
                    onSelect = { next -> prefs.update { s -> s.copy(animSpeedIndex = next) } },
                    accent = accent
                )

                ToggleRow(
                    label = "显示时钟",
                    checked = settings.showClock,
                    onToggle = { prefs.update { it.copy(showClock = !it.showClock) } },
                    accent = accent
                )
                ToggleRow(
                    label = "显示 Top Shelf",
                    checked = settings.showShelf,
                    onToggle = { prefs.update { it.copy(showShelf = !it.showShelf) } },
                    accent = accent
                )
                ToggleRow(
                    label = "显示内容推荐行",
                    checked = settings.showContentRows,
                    onToggle = { prefs.update { it.copy(showContentRows = !it.showContentRows) } },
                    accent = accent
                )
                ToggleRow(
                    label = "24 小时制",
                    checked = settings.time24h,
                    onToggle = { prefs.update { it.copy(time24h = !it.time24h) } },
                    accent = accent
                )

                Spacer(Modifier.height(8.dp))
                ActionRow("恢复默认设置", accent, Color.White.copy(alpha = 0.10f)) { prefs.reset() }
                ActionRow("返回主界面", accent, accent.copy(alpha = 0.25f), onClose)
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

/* ----------------------- 设置面板通用行组件 ----------------------- */

@Composable
fun SettingRow(
    modifier: Modifier = Modifier,
    accent: Color,
    content: @Composable RowScope.() -> Unit
) {
    var f by remember { mutableStateOf(false) }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                val s = if (f) 1.03f else 1f
                scaleX = s; scaleY = s
            }
            .clip(RoundedCornerShape(14.dp))
            .background(if (f) Color.White.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.05f))
            .border(
                width = if (f) 2.dp else 0.dp,
                color = if (f) accent else Color.Transparent,
                shape = RoundedCornerShape(14.dp)
            )
            .focusable()
            .onFocusChanged { f = it.isFocused }
            .padding(horizontal = 22.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}

@Composable
fun StepperRow(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    onMinus: () -> Unit,
    onPlus: () -> Unit,
    accent: Color
) {
    SettingRow(modifier, accent) {
        Text(label, color = Color.White, fontSize = 19.sp, modifier = Modifier.weight(1f))
        PanelButton("−", accent, onMinus)
        Spacer(Modifier.width(12.dp))
        Text(value, color = accent, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.width(12.dp))
        PanelButton("+", accent, onPlus)
    }
}

@Composable
fun OptionRow(
    label: String,
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    accent: Color
) {
    SettingRow {
        Text(label, color = Color.White, fontSize = 19.sp, modifier = Modifier.weight(1f))
        PanelButton("‹", accent) { onSelect((selectedIndex - 1 + options.size) % options.size) }
        Spacer(Modifier.width(12.dp))
        Text(
            "${options[selectedIndex.coerceIn(0, options.lastIndex)]}  (${selectedIndex + 1}/${options.size})",
            color = accent,
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.width(12.dp))
        PanelButton("›", accent) { onSelect((selectedIndex + 1) % options.size) }
    }
}

@Composable
fun ToggleRow(label: String, checked: Boolean, onToggle: () -> Unit, accent: Color) {
    SettingRow(modifier = Modifier.clickable(onClick = onToggle)) {
        Text(label, color = Color.White, fontSize = 19.sp, modifier = Modifier.weight(1f))
        Text(
            if (checked) "开" else "关",
            color = if (checked) accent else Color.White.copy(alpha = 0.45f),
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.width(14.dp))
        Box(
            Modifier
                .width(56.dp)
                .height(30.dp)
                .clip(RoundedCornerShape(15.dp))
                .background(if (checked) accent else Color.White.copy(alpha = 0.15f))
                .padding(4.dp)
        ) {
            Box(
                Modifier
                    .size(22.dp)
                    .align(if (checked) Alignment.CenterEnd else Alignment.CenterStart)
                    .clip(RoundedCornerShape(11.dp))
                    .background(Color.White)
            )
        }
    }
}

@Composable
fun ActionRow(label: String, accent: Color, bg: Color, onClick: () -> Unit) {
    var f by remember { mutableStateOf(false) }
    Text(
        label,
        color = Color.White,
        fontSize = 19.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                val s = if (f) 1.03f else 1f
                scaleX = s; scaleY = s
            }
            .clip(RoundedCornerShape(14.dp))
            .background(if (f) bg.copy(alpha = 0.9f) else bg)
            .border(
                width = if (f) 2.dp else 0.dp,
                color = if (f) accent else Color.Transparent,
                shape = RoundedCornerShape(14.dp)
            )
            .focusable()
            .onFocusChanged { f = it.isFocused }
            .clickable(onClick = onClick)
            .padding(horizontal = 22.dp, vertical = 14.dp)
    )
}

@Composable
fun PanelButton(symbol: String, accent: Color, onClick: () -> Unit) {
    var f by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(if (f) accent else Color.White.copy(alpha = 0.10f))
            .focusable()
            .onFocusChanged { f = it.isFocused }
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            symbol,
            color = if (f) Color.Black else accent,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
