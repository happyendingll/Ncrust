# Android 5.1 兼容适配

2026-11-06：主版声明最低系统 Android 5.1（API 22），保留现有 Compose / Media3 架构。本轮只调整通用系统兼容，不增加车机方控、点火续播或厂商 MCU 功能。不涉及 Android 4.x / Walkman 独立客户端。

## 源码调整

- `minSdk` 从 24 降至 22，target / compile SDK 与依赖版本保持原值。
- Wi-Fi 判断：API 23+ 使用活动 Network；API 22 使用已连接的 `activeNetworkInfo`。
- Android 5.1 无 Doze 白名单：跳过首次白名单提示，手动设置入口回退到应用详情。
- 通知刷新、剪贴板读取改用字符串服务名，避免 API 23 的类参数查询。
- eapi 原始响应直接按字节解密；Base64 字符串入口复用已有纯 Kotlin 解码器，去掉 API 26 的 `java.util.Base64` 依赖。补充加密响应与失败回退单测。
- 保持原有低版本音质降级策略，不在本轮启用旧设备 FLAC / 高阶音质。

## 验证状态和构建前提

本轮尚未证明可安装或稳定运行，不能据此发布为已验收的 Android 5.1 版本。

当前环境缺少同级 `../Kanesumi-sec-a`、Android SDK / `local.properties`，Gradle 9.3.1 下载也因 `services.gradle.org` DNS 解析失败而中断。因而 assemble、JVM 单测、lint 均未能通过 Gradle 执行；源码差异的空白检查通过。

外部 Kanesumi 的各 Android 模块及其传递依赖必须支持 API 22；不能用 `tools:overrideLibrary` 强行绕过最低版本。若其声明最低版本高于 22，需要在该库中另行完成兼容工作。此工作区未提供该库，无法核对或修改。`benchmark/` 保持 API 24，不属于 API 22 验收工具。

环境齐备后运行：

```bash
./gradlew :app:assembleDebug :app:testDebugUnitTest :app:lintDebug
```

检查合并 manifest 的最终最低版本与依赖冲突，修复 lint 的 NewApi 问题。正式签名构建需保留 APK v1 签名供 Android 5.1 校验，并在 API 22 真机或模拟器安装确认。

## API 22 验收清单

- 首次启动、冷启动、前后台切换、剪贴板链接读取与横屏布局。
- 显示二维码并完成登录、会话恢复、读取资料与收藏。
- Wi-Fi / 移动网络音质选择，断网重连、TLS 与音频 CDN 访问。
- 连续切歌、暂停恢复、通知更新与通知按钮、熄屏后台播放。
- 收藏写入的加密响应解析，以及普通 JSON 错误响应回退。
- 更新下载、旧系统签名检查与系统安装器打开。

厂商导航混音、休眠唤醒、方控另行实测，不属于本轮通用兼容改动的验收结论。
