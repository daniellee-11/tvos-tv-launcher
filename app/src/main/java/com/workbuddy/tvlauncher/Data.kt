package com.workbuddy.tvlauncher

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlin.math.absoluteValue

/** 桌面上的一个应用条目 */
@Stable
data class AppItem(
    val label: String,
    val packageName: String,
    val icon: ImageBitmap?,
    val color: Color      // 由包名派生的颜色，用于氛围光与占位底色
)

/** 内容推荐行 */
data class ContentItem(val title: String, val subtitle: String, val color: Color)
data class ContentRow(val title: String, val items: List<ContentItem>)

/** 根据包名稳定地生成一个中性色 */
fun colorForPackage(pkg: String): Color {
    val hue = (pkg.hashCode().absoluteValue % 360) / 360f
    val argb = android.graphics.Color.HSVToColor(floatArrayOf(hue, 0.50f, 0.52f))
    return Color(argb)
}

fun Drawable.toImageBitmap(size: Int = 128): ImageBitmap? = try {
    val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bmp)
    setBounds(0, 0, size, size)
    draw(canvas)
    bmp.asImageBitmap()
} catch (e: Exception) {
    null
}

/**
 * 加载系统内全部电视应用（LEANBACK_LAUNCHER），
 * 兼容部分设备回退到普通 LAUNCHER 类别。
 */
fun loadInstalledApps(context: Context): List<AppItem> {
    val pm = context.packageManager
    val self = context.packageName
    val apps = LinkedHashMap<String, AppItem>()

    fun query(category: String) {
        val intent = Intent(Intent.ACTION_MAIN).addCategory(category)
        val infos = if (Build.VERSION.SDK_INT >= 33) {
            pm.queryIntentActivities(intent, android.content.pm.PackageManager.ResolveInfoFlags.of(0))
        } else {
            pm.queryIntentActivities(intent, 0)
        }
        for (ri in infos) {
            val pkg = ri.activityInfo?.packageName ?: continue
            if (pkg == self) continue
            if (apps.containsKey(pkg)) continue
            val icon = try { ri.loadIcon(pm).toImageBitmap() } catch (e: Exception) { null }
            apps[pkg] = AppItem(
                label = ri.loadLabel(pm)?.toString() ?: pkg,
                packageName = pkg,
                icon = icon,
                color = colorForPackage(pkg)
            )
        }
    }

    query(Intent.CATEGORY_LEANBACK_LAUNCHER)
    if (apps.isEmpty()) query(Intent.CATEGORY_LAUNCHER)

    val list = apps.values.sortedBy { it.label }
    // 真机上没有任何第三方应用时给出占位示例，避免空白界面
    if (list.isEmpty()) return sampleApps()
    return list
}

/** 无应用时的占位演示数据 */
fun sampleApps(): List<AppItem> = listOf(
    AppItem("影视中心", "demo.video", null, Color(0xFF1F6FEB)),
    AppItem("音乐", "demo.music", null, Color(0xFFE5484D)),
    AppItem("游戏大厅", "demo.games", null, Color(0xFF8E4EC6)),
    AppItem("应用商店", "demo.store", null, Color(0xFF30A46C)),
    AppItem("浏览器", "demo.browser", null, Color(0xFFF76B15)),
    AppItem("相册", "demo.photos", null, Color(0xFF0EA5E9)),
    AppItem("直播", "demo.live", null, Color(0xFFD6409F)),
    AppItem("天气", "demo.weather", null, Color(0xFF12A594))
)

fun sampleContentRows(): List<ContentRow> = listOf(
    ContentRow(
        "热门推荐", listOf(
            ContentItem("星际远航", "科幻 · 2024", Color(0xFF3D5AFE)),
            ContentItem("山海食肆", "美食 · 纪录", Color(0xFFF76B15)),
            ContentItem("极速狂飙", "动作 · 2023", Color(0xFFE5484D)),
            ContentItem("静谧森林", "自然 · 4K", Color(0xFF30A46C)),
            ContentItem("城市之光", "剧情 · 2024", Color(0xFF8E4EC6)),
            ContentItem("雪国列车", "悬疑 · 2022", Color(0xFF0EA5E9))
        )
    ),
    ContentRow(
        "继续观看", listOf(
            ContentItem("大江大河 第2集", "上次看到 35:10", Color(0xFF12A594)),
            ContentItem("世界美食之旅", "上次看到 12:40", Color(0xFFD6409F)),
            ContentItem("宇宙时空之旅", "上次看到 51:02", Color(0xFF3D5AFE))
        )
    )
)
