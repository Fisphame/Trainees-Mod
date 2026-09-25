# 发版清单（Release Checklist）

> 给作者用。当前状态：**代码侧已就绪，只差贴图与版本号决定**（`B` 节全部需要你执行）。

## A. 代码侧（已完成 ✅）

- [x] ① 规格底座、② 含氯消毒液化学、③ 相变 / 热源分级 / 容器材料分级 / 恒温浴、④ 打包机与日化品 —— 全部落地
- [x] `gradlew test` → **77 条单元测试全绿**（ReactionMath 17 / SpecMatcher 27 / IonDisplay 2 / PhaseChangeTable 8 / ContainerMaterial 5 / PackerService 18）
- [x] 中英文语言键 **555 : 555** 零差异（每次构建自动对拍）
- [x] `gradlew build` 出包 → `build/libs/trainees-<版本>.jar`（当前 8.1 MB）
- [x] **jar 打包完整性核对**：新增 blockstate / 模型 / 战利品表 / 语言键 / 相变与规格类 / `forge:` 公共标签 65 条 / 发布元数据 —— 均在包内且为正确 UTF-8
- [x] **专用服务器实测**：`Done`、`Reaction graph ready: 33 nodes, 63 edges`、`Loaded 1715 advancements`、无 ERROR
- [x] 更新日志 `CHANGELOG.md`（注意：根目录 `changelog.txt` 是 **Forge 自带的**）
- [x] 发布元数据：`META-INF/mods.toml`（issue 追踪 / 主页 / credits）、`pack.mcmeta`、`README.md`

## B. 需要作者执行（发布阻塞）

- [ ] **贴图**：51 处缺失 + 本次新增方块/物品用的是**原版占位贴图**，清单见 `RESOURCES_MISSING.md`
  - 最小可行路径：先补**本次新增的 12 张**（打包机、金属/石英/陶瓷坩埚、恒温浴、创造热源、合格/优质消毒液、4 类危险次品），其余沿用占位也不影响功能
- [ ] **决定版本号**：改 `gradle.properties` 的 `mod_version`（例：`1.5.0`）
- [ ] **重新出包**：`gradlew build` —— ‼ 改完版本号**必须重出**，jar 内 `mods.toml` 的 `version` 来自它
- [ ] **提交**：把工作树改动提交（建议按语义拆：规格底座 / 含氯化学 / 相变 / 材料与热源 / 打包机与日化品 / 发版元数据）
- [ ] **推送**：`git push` —— 本地已领先 `origin/master`，其中 `a9eb661` 是**旧存档死锁 + 专服崩溃**的修复，务必一起推
- [ ] **打标签**：`git tag v<版本>` → `git push --tags`
- [ ] **上传**：CurseForge / Modrinth / 网盘；发布说明可直接抄 `CHANGELOG.md` 的「未发布」一节

## C. 发布前自测（进游戏，约 10 分钟）

**化学（②）**
1. 烧杯灌水 → `/chemtester add trainees:naoh 4` → `info` 出现 `oh_minus`（碱线可从零开始）
2. `/chemtester add trainees:cl2 2` → 出现 `clo_minus`（冷路径）
3. **成败线**：热源 293 K → **不应**出现 `clo3_minus`；改 353 K → 开始出现

**相变与材料（③）**
4. 加 `h2o_solid` + 热源 300 K → 熔化出 `h2o`，温度在 **273 K 附近卡一下**；260 K 冻回；380 K 出 `h2o_gas`
5. 玻璃烧杯设 900 K 也只到 **773 K**；熔盐实验换石英/陶瓷坩埚才上得去 1074 K
6. 坩埚加 `nacl` → 热源 1100 K → `nacl_molten` → `/chemtester power true` → 出 `na` + `cl2`

**日化品（④）**
7. 配出有效氯 5–8% 的溶液 → 下方放打包机、上方放坩埚 → 右键打包 → 得合格/优质品；浓度不合适则自动拒收
8. 潜行右键**强制灌装** → 得次品 → **拿次品右键自己** → 应看到中毒 / 烧伤 / 自毁的对应反馈

## D. 已知但不阻塞发布

- 打包机暂无 GUI（当前交互：右键自动打包 / 潜行右键强制灌装 / 手持物品不参与）
- 相变升华/凝华 V1 靠动力学压制，未上分压判据（V2）
- AI 助教仍是 S0（`/ask`）；市场条目与科技树节点（⑤⑥）未开始
- 打包机未接方块实体 → 目标产品选择等 ⑥ 多产品落地时一起做
