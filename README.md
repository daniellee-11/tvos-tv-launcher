# TV Launcher — tvOS 风格安卓电视启动器

复刻 tvOS 界面的 Android TV 启动器（Jetpack Compose 编写）。**所有界面调整全部收进右上角「设置」按钮**，主界面只保留纯内容展示，调整即时生效并持久化保存。

## ✨ 特性

- **tvOS 风格视觉**：深色壁纸 + 焦点应用"氛围光"（磨砂玻璃感）、Top Shelf 横幅、大圆角卡片、焦点放大动画、白边高亮
- **真实启动器**：自动扫描系统内全部电视应用（LEANBACK_LAUNCHER），显示真实图标，按 OK 键直接启动；也兼容普通应用回退
- **遥控器导航**：方向键移动焦点、OK 键打开应用，全界面无触屏也可操作
- **顶栏时钟 + 设置按钮**

## ⚙ 设置面板（全部界面调整入口）

| 调整项 | 范围 |
|---|---|
| 卡片宽度 / 高度 | 110–230 dp / 160–290 dp |
| 圆角半径 | 0–36 dp |
| 焦点缩放 | 100%–120% |
| 背景氛围光 | 0–100% |
| 强调色 | 铂金白 / 经典蓝 / 活力橙 / 神秘紫 / 翡翠绿 / 玫瑰红 |
| 壁纸 | 深空黑 / 午夜蓝 / 暗夜紫 / 松石绿 / 熔岩红 |
| 动画速度 | 快 / 标准 / 慢 |
| 开关 | 显示时钟、显示 Top Shelf、显示内容推荐行、24 小时制 |
| 其他 | 恢复默认设置、返回主界面 |

所有参数存于 `SharedPreferences`，重启保留。

## 📦 构建安装

1. 用 Android Studio（Hedgehog 及以上）打开本目录，等待 Gradle 同步；
2. `Build → Build APK(s)`，或命令行：

```bash
./gradlew assembleDebug   # 产物：app/build/outputs/apk/debug/app-debug.apk
```

3. 通过 ADB 侧载到电视 / 盒子：

```bash
adb connect <电视IP>:5555
adb install -r app-debug.apk
```

4. 按 HOME 键 → 在弹出的"选择桌面"中选择 **TV Launcher** 并设为默认。

> 无自定义 `gradlew` wrapper 脚本时，直接用 Android Studio 打开即可自动下载 Gradle 8.4。

## 🗂 工程结构

```
app/src/main/java/com/workbuddy/tvlauncher/
├── MainActivity.kt        # 入口，注册为 HOME / LEANBACK_LAUNCHER
├── LauncherScreen.kt      # 主界面：氛围光、顶栏、Top Shelf、应用行、内容行
├── SettingsPanel.kt       # 设置面板：全部界面调整 + 焦点行组件
├── SettingsRepository.kt  # 设置模型 + SharedPreferences 持久化
├── Data.kt                # 应用扫描 / 占位示例数据
└── Theme.kt               # 强调色与壁纸预设
```

## 🖥 浏览器预览

`preview/index.html` 是一份可直接在浏览器体验的高保真交互原型：

- `← → ↑ ↓` 模拟遥控器移动焦点，`Enter` 打开应用 / 打开设置
- 设置面板内 `← →` 调整数值，效果实时反映到界面（卡片大小、圆角、氛围光、强调色、壁纸等）
- 使用 `localStorage` 保存，刷新后保留
