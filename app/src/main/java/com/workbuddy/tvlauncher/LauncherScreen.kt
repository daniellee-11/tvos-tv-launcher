package com.workbuddy.tvlauncher

import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 启动器主界面 —— tvOS 风格：
 * 深色壁纸 + 氛围光、顶部时钟与设置入口、Top Shelf 横幅、应用行、内容推荐行。
 * 所有界面调整都收进右上角「设置」按钮。
 */
@Composable
fun LauncherScreen(settings: LauncherSettings, prefs: SettingsRepository) {
    val context = LocalContext.current
    val accent = accentOf(settings.accentColorIndex).color
    val wallpaper = wallpaperOf(settings.wallpaperIndex)

    val apps = remember { mutableStateOf<List<AppItem>?>(null) }
    LaunchedEffect(Unit) { apps.value = loadInstalledApps(context) }
    val appList = apps.value ?: sampleApps()

    var selectedAppIndex by remember { mutableStateOf(0) }
    var showSettings by remember { mutableStateOf(false) }
    val safeIndex = selectedAppIndex.coerceIn(0, appList.lastIndex)
    val focusedApp = appList[safeIndex]

    Box(
        Modifier
            .fillMaxSize()
            .background(wallpaper.brush)
    ) {
        // 焦点应用的"氛围光"——模拟 tvOS 的磨砂玻璃背景
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            focusedApp.color.copy(alpha = settings.glowStrength * 0.45f),
                            Color.Transparent
                        ),
                        radius = 1100f
                    )
                )
        )

        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 56.dp, vertical = 28.dp)
        ) {
            TopBar(settings, accent) { showSettings = true }
            Spacer(Modifier.height(20.dp))

            if (settings.showShelf) {
                TopShelf(focusedApp, accent)
                Spacer(Modifier.height(28.dp))
            }

            Text("我的应用", color = Color.White.copy(alpha = 0.75f), fontSize = 20.sp, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(14.dp))
            AppRow(
                apps = appList,
                settings = settings,
                accent = accent,
                onFocused = { selectedAppIndex = it },
                onOpen = { app ->
                    val intent = context.packageManager.getLaunchIntentForPackage(app.packageName)
                    if (intent != null) {
                        context.startActivity(intent)
                    } else {
                        Toast.makeText(context, "打开「${app.label}」", Toast.LENGTH_SHORT).show()
                    }
                }
            )

            if (settings.showContentRows) {
                Spacer(Modifier.height(28.dp))
                ContentRows(settings, accent)
            }
        }

        if (showSettings) {
            SettingsPanel(settings, prefs, accent, onClose = { showSettings = false })
        }
    }
}

/* ------------------------------- 顶栏 ------------------------------- */

@Composable
fun TopBar(settings: LauncherSettings, accent: Color, onSettingsClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "TV Launcher",
            color = accent,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            if (settings.showClock) ClockText(settings.time24h, accent)
            var f by remember { mutableStateOf(false) }
            Text(
                "⚙",
                fontSize = 26.sp,
                color = if (f) Color.Black else accent,
                modifier = Modifier
                    .size(52.dp)
                    .graphicsLayer {
                        val s = if (f) settings.focusScale else 1f
                        scaleX = s; scaleY = s
                    }
                    .clip(CircleShape)
                    .background(if (f) accent else Color.White.copy(alpha = 0.10f))
                    .focusable()
                    .onFocusChanged { f = it.isFocused }
                    .clickable(onClick = onSettingsClick)
            )
        }
    }
}

@Composable
fun ClockText(time24h: Boolean, accent: Color) {
    var now by remember { mutableStateOf(System.currentTimeMillis()) }
    LaunchedEffect(time24h) {
        while (true) {
            delay(10_000)
            now = System.currentTimeMillis()
        }
    }
    val pattern = if (time24h) "HH:mm" else "hh:mm a"
    val text = remember(now, time24h) {
        SimpleDateFormat(pattern, Locale.getDefault()).format(Date(now))
    }
    Text(text, color = Color.White.copy(alpha = 0.85f), fontSize = 22.sp, fontWeight = FontWeight.Light)
}

/* ---------------------------- Top Shelf 横幅 ---------------------------- */

@Composable
fun TopShelf(app: AppItem, accent: Color) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(170.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(app.color.copy(alpha = 0.85f), app.color.copy(alpha = 0.25f))
                )
            )
            .padding(28.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (app.icon != null) {
            Image(
                bitmap = app.icon,
                contentDescription = app.label,
                modifier = Modifier.size(96.dp).clip(RoundedCornerShape(18.dp)),
                contentScale = ContentScale.Fit
            )
            Spacer(Modifier.width(24.dp))
        }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(app.label, color = Color.White, fontSize = 34.sp, fontWeight = FontWeight.Bold)
            Text(
                "最近使用 · 按下 OK 键打开",
                color = Color.White.copy(alpha = 0.75f),
                fontSize = 16.sp
            )
        }
    }
}

/* ------------------------------- 应用行 ------------------------------- */

@Composable
fun AppRow(
    apps: List<AppItem>,
    settings: LauncherSettings,
    accent: Color,
    onFocused: (Int) -> Unit,
    onOpen: (AppItem) -> Unit
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(26.dp)) {
        itemsIndexed(apps) { index, app ->
            FocusableCard(
                width = settings.cardWidthDp.dp,
                height = settings.cardHeightDp.dp,
                radius = settings.cornerRadiusDp.dp,
                scale = settings.focusScale,
                durationMs = settings.animDurationMs,
                baseColor = app.color,
                accent = accent,
                onFocus = { onFocused(index) },
                onClick = { onOpen(app) }
            ) {
                if (app.icon != null) {
                    Image(
                        bitmap = app.icon,
                        contentDescription = app.label,
                        modifier = Modifier.size(72.dp),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Text(app.label.take(2), color = Color.White, fontSize = 34.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/* ------------------------------ 内容推荐行 ------------------------------ */

@Composable
fun ContentRows(settings: LauncherSettings, accent: Color) {
    val rows = remember { sampleContentRows() }
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        rows.forEach { row ->
            Text(row.title, color = Color.White.copy(alpha = 0.75f), fontSize = 18.sp, fontWeight = FontWeight.Medium)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                itemsIndexed(row.items) { _, item ->
                    FocusableCard(
                        width = 210.dp,
                        height = 132.dp,
                        radius = settings.cornerRadiusDp.dp,
                        scale = settings.focusScale,
                        durationMs = settings.animDurationMs,
                        baseColor = item.color,
                        accent = accent,
                        onFocus = {},
                        onClick = {}
                    ) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(item.title, color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                            Text(item.subtitle, color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}

/* ---------------------------- 通用焦点卡片 ---------------------------- */

@Composable
fun FocusableCard(
    width: Dp,
    height: Dp,
    radius: Dp,
    scale: Float,
    durationMs: Int,
    baseColor: Color,
    accent: Color,
    onFocus: () -> Unit,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }
    val animatedScale by animateFloatAsState(
        targetValue = if (isFocused) scale else 1f,
        animationSpec = tween(durationMs),
        label = "cardScale"
    )
    val shape = RoundedCornerShape(radius)

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(bottom = 4.dp)) {
        Box(
            modifier = Modifier
                .width(width)
                .height(height)
                .graphicsLayer { scaleX = animatedScale; scaleY = animatedScale }
                .clip(shape)
                .background(
                    Brush.verticalGradient(
                        listOf(baseColor.copy(alpha = 0.9f), baseColor.copy(alpha = 0.5f))
                    )
                )
                .border(
                    width = if (isFocused) 3.dp else 0.dp,
                    color = if (isFocused) Color.White else Color.Transparent,
                    shape = shape
                )
                .focusable()
                .onFocusChanged {
                    isFocused = it.isFocused
                    if (it.isFocused) onFocus()
                }
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}
