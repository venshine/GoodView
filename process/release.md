# 封板参数（release-seal）

通用步骤见全局 skill `release-seal`，本文件只写本项目参数。

## 版本号与 tag

- 版本号所在文件：`goodview/build.gradle` 的 `publishing` 段 `version`，`sample/build.gradle` 的 `versionName`。两处保持一致，sample 首页的版本号读取 `versionName`。
- tag：直接用版本号（如 `3.0.0`），annotated tag，说明用英文写版本号和要点，如 `3.0.0: rewrite the library in Kotlin`。
- JitPack 按 tag 构建发布：`com.github.venshine:GoodView:<tag>`。

## 封板前的发版提交

- `README.md` 与 `README-CN.md` 的安装片段（Groovy 与 Kotlin DSL 各一处）改成新版本号。
- README 截图里能看到版本号的（`screenshot/home.png`、`screenshot/home_dark.png`）在真机上重拍：原图裁掉顶部状态栏 130px，再缩放到 400×826。界面有变化时重拍全部截图和 `screenshot/demo.gif`。
- 验证：`./gradlew :goodview:testDebugUnitTest :goodview:assembleRelease :sample:assembleDebug :sample:testDebugUnitTest`。

## 本项目例外

- 封板后不提前把版本号升到下一版：版本号在下一次发版提交时再改，避免 README 安装片段指向未发布的版本。
- 没有飞书产品文档和 `ROADMAP.md`，跳过文档同步。

## GitHub Release

- 推送 tag 后创建同名 Release（标题为版本号），说明用英文，写改动要点和安装片段。
- 附件：`./gradlew :sample:assembleDebug` 产出的 debug 签名 APK，重命名为 `GoodView-sample-<版本号>.apk`。
