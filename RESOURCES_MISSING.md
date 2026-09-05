# Trainees Mod — 缺失资源清单

> 记录当前缺失的资源文件（不影响编译，仅影响游戏内显示/音效）。
> 后续补充时按此清单逐一补齐。ogg 音效可用 [Minecraft 音效工具] 或自制；贴图可用简易占位图。

## 1. 实体纹理（`assets/trainees/textures/entity/`）

| 纹理 | 对应实体 | 备注 |
|---|---|---|
| `called_sword.png` | CalledSwordEntity（万剑归宗召唤剑） | 渲染器为空 EntityRenderer，无贴图仅不显示模型 |
| `particle_entity.png` | ParticleEntity（贝塞尔弧线粒子实体） | 同上 |
| `hydrogen.png` | GasEntities.HydrogenEntity | 同上 |
| `oxygen.png` | GasEntities.OxygenEntity | 同上 |

## 2. 物品贴图（`assets/trainees/textures/item/`，model layer0 引用缺失）

| 贴图 | 物品 | 备注 |
|---|---|---|
| `ion_membrane.png` | 离子交换膜（电解槽隔膜） | 2026-08 新增物品 |
| `banalium_ingot.png` | 錋锭 | 8 种金属锭中仅此 4 种缺贴图 |
| `crucium_ingot.png` | 鋴锭 | |
| `nivtium_ingot.png` | 金宁锭 | |
| `placium_ingot.png` | 鈚锭 | |
| `selenaurite_ingot.png` | 鈅锭 | |
| `selenaurite_nugget.png` | 鈅粒 | |
| `terapium_ingot.png` | 金寺锭 | |
| `nyctium_ingot.png` | 鍢锭 | |
| `imperfection.png` | 杂质 | |
| `che_bpo2_solid.png` / `che_bpo3_solid.png` | 二/三氧化黑粉末 | 化学粉末 |
| `che_hbpo_powder.png` / `che_hbpo3_powder.png` / `che_hbpo4_powder.png` | 次黑酸/黑酸/高黑酸粉末 | |
| `che_ji2o2_ingot.png` / `che_ji2o2_nugget.png` | 过氧化鸡锭/粒 | |
| `che_jibpo_crystallization.png` / `che_jibpo3_crystallization.png` / `che_jibpo4_crystallization.png` | 次黑酸鸡/黑酸鸡/高黑酸鸡粉末 | |

> 注：`custom_book` 的 layer0 指向 `minecraft:item/book`（原版贴图），不算缺失。

## 3. 音效 ogg（`assets/trainees/sounds/`）

| 文件 | 事件 | 备注 |
|---|---|---|
| `releasing_sword_wind_1.ogg` ~ `releasing_sword_wind_4.ogg` | 万剑归宗剑风 | sounds.json 条目已补（2026-08 修改），ogg 待补；当前未被代码调用 |

> 另有计划的专属鸡类音效（ambient/hurt/death/step）尚未设计，见代码内 TODO 注释。

## 4. GUI 纹理

| 文件 | 用途 | 备注 |
|---|---|---|
| `textures/gui/config_button.png` | ChemConfigScreen 暂停菜单入口按钮（20x20） | 缺失时按钮显示异常但不会崩溃 |

## 5. 方块模型/贴图（2026-08 新增方块）

| 文件 | 方块 | 备注 |
|---|---|---|
| `blockstates/electrolysis_cell.json`、`models/block/electrolysis_cell.json`、`textures/block/electrolysis_cell*.png` | 电解槽 | 尚未制作，物品暂无方块模型（需 blockstates + 模型 + 贴图，或用父模型替代） |

## 6. 其他已知缺失/待定

- `block/black_hole.png` 等黑洞相关贴图已随功能删除（P0-10），无需补。
- `purification_station*` / `reacting_furnace*` 资源已随功能删除（P2-7），无需补。

---
*维护：新增/补齐资源后请从此清单移除对应行。*
