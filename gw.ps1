# 让 gradle 在受限沙箱（workspace-write）下也能运行，无需提权。
#
# 背景：gradle 默认要写 ~/.gradle，而受限模式只允许「工作区根 + 会话私有 temp」。
# 实测：本机上 *后建* 的 temp 目录（%TEMP%\dsh-XXXX，DSH 启动时若不存在、事后补建）
# 上没有写 ACE，仍然会被拒；所以这里把 GRADLE_USER_HOME 放进**工作区内**（必可写）。
#
# 用法：  .\gw.ps1 compileJava test --console=plain
# 注意：① 首次运行会把 Gradle 发行包与 Forge/Minecraft 依赖下载到 `.gradle-home/`（约 1~2 GB，一次性）；
#       ② 该目录已在 .gitignore 中排除，不会被提交。

$env:GRADLE_USER_HOME = Join-Path $PSScriptRoot '.gradle-home'
& "$PSScriptRoot\gradlew.bat" @args
exit $LASTEXITCODE
