package com.pha.trainees.client.gui;

import com.pha.trainees.Main;
import com.pha.trainees.config.ChemConfig;
import com.pha.trainees.config.ChemConfig.DifficultyLevel;
import com.pha.trainees.util.interfaces.IHoverText;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec;

import java.text.DecimalFormat;
import java.util.*;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;

public class ChemConfigScreen extends Screen implements IHoverText {

    private static final DecimalFormat DF = new DecimalFormat("#0.00");
    private static final int SLIDER_WIDTH = 220;
    private static final int SLIDER_HEIGHT = 20;
    private static final int ROW_HEIGHT = 28;
    private static final int GROUP_PADDING = 8;
    private static final int INDENT = 20;

    private final Screen parentScreen;
    private final Map<String, ConfigSlider> sliders = new LinkedHashMap<>();
    private final List<AbstractWidget> widgets = new ArrayList<>();

    private DifficultyLevel selectedDifficulty = DifficultyLevel.NORMAL;
    private boolean isUpdatingFromPreset = false;
    private boolean configLoaded = false;

    // 滚动相关
    private int scrollOffset = 0;
    private int maxScroll = 0;
    private int contentHeight = 0;

    public ChemConfigScreen(Screen parentScreen) {
        super(Component.translatable("gui.trainees.config.title"));
        this.parentScreen = parentScreen;

        // 检查配置是否已加载（正常情况下，在游戏内打开时为 true）
        try {
            ChemConfig.ORE_VALUABLE_RATIO_MULTIPLIER.get();
            configLoaded = true;
        } catch (IllegalStateException e) {
            configLoaded = false;
        }
    }

    @Override
    protected void init() {
        super.init();
        this.widgets.clear();
        this.sliders.clear();

        // ============================================================
        // 如果配置未加载（安全防护）
        // ============================================================
        if (!configLoaded) {
            StringWidget warning = new StringWidget(
                    width / 2 - 150, height / 2 - 10, 300, 20,
                    Component.translatable("gui.trainees.config.error.not_loaded").withStyle(ChatFormatting.RED),
                    Minecraft.getInstance().font
            );
            this.widgets.add(warning);

            Button closeBtn = Button.builder(
                    Component.translatable("gui.trainees.config.button.close"),
                    button -> onClose()
            ).bounds(width / 2 - 40, height / 2 + 30, 80, 20).build();
            this.widgets.add(closeBtn);

            for (AbstractWidget widget : widgets) {
                this.addRenderableWidget(widget);
            }
            return;
        }

        // ============================================================
        // 正常初始化（配置已加载）
        // ============================================================
        int y = 20;

        // 1. 难度指标
        double score = ChemConfig.DifficultyScorer.calculateScore();
        StringWidget scoreWidget = new StringWidget(
                width / 2 - 100, y, 200, 20,
                Component.translatable("gui.trainees.config.score", DF.format(score)).withStyle(ChatFormatting.GOLD),
                Minecraft.getInstance().font
        );
        this.widgets.add(scoreWidget);
        y += 28;

        // 2. 难度按钮
        int buttonWidth = 60;
        int buttonSpacing = 10;
        int totalWidth = buttonWidth * 4 + buttonSpacing * 3;
        int startX = (width - totalWidth) / 2;

        for (int i = 0; i < DifficultyLevel.values().length; i++) {
            DifficultyLevel level = DifficultyLevel.values()[i];
            int x = startX + i * (buttonWidth + buttonSpacing);
            Button btn = Button.builder(
                    Component.translatable("gui.trainees.config.difficulty." + level.name().toLowerCase()),
                    button -> onDifficultySelected(level)
            ).bounds(x, y, buttonWidth, 20).build();
            if (level == selectedDifficulty) {
                btn.active = false;
            }
            this.widgets.add(btn);
        }
        y += 30;
        y += 5;

        // 3. 滑动条组
        y = addSliderGroup(y, String.valueOf(Component.translatable("gui.trainees.config.group.resource_getting")
                        .withStyle(ChatFormatting.GRAY)),
                new SliderEntry(tr("gui","ore_valuable","config", "slider"),
                        "ore_valuable", ChemConfig.ORE_VALUABLE_RATIO_MULTIPLIER, 0.1, 3.0),
                new SliderEntry(tr("gui","ore_gangue","config", "slider"),
                        "ore_gangue", ChemConfig.ORE_GANGUE_RATIO_MULTIPLIER, 0.1, 3.0)
        );

        y = addSliderGroup(y, String.valueOf(Component.translatable("gui.trainees.config.group.pollution")
                        .withStyle(ChatFormatting.GRAY)),
                new SliderEntry(tr("gui","pollution_speed","config", "slider"),
                        "pollution_speed", ChemConfig.POLLUTION_DIFFUSION_SPEED, 0.0, 10.0),
                new SliderEntry(tr("gui","pollution_toxicity","config", "slider"),
                        "pollution_toxicity", ChemConfig.POLLUTION_TOXICITY_THRESHOLD, 0.01, 10.0),
                new SliderEntry(tr("gui","plant_penalty","config", "slider"),
                        "plant_penalty", ChemConfig.POLLUTION_PLANT_GROWTH_PENALTY, 0.0, 1.0)
        );

        y = addSliderGroup(y, String.valueOf(Component.translatable("gui.trainees.config.group.energy")
                        .withStyle(ChatFormatting.GRAY)),
                new SliderEntry(tr("gui","energy_consumption","config", "slider"),
                        "energy_consumption", ChemConfig.ENERGY_CONSUMPTION_MULTIPLIER, 0.1, 10.0),
                new SliderEntry(tr("gui","energy_generation","config", "slider"),
                        "energy_generation", ChemConfig.ENERGY_GENERATION_MULTIPLIER, 0.1, 10.0)
        );

        y = addSliderGroup(y, String.valueOf(Component.translatable("gui.trainees.config.group.mapping")
                        .withStyle(ChatFormatting.GRAY)),
                new SliderEntry(tr("gui","bucket_mapping","config", "slider"),
                        "bucket_mapping", ChemConfig.BUCKET_TO_MOL_WATER, 0.0, 640.0),
                new SliderEntry(tr("gui","ingot_mapping","config", "slider"),
                        "ingot_mapping", ChemConfig.SOLID_INGOT_TO_MOL, 0.0, 640.0),
                new SliderEntry(tr("gui","nugget_mapping","config", "slider"),
                        "nugget_mapping", ChemConfig.SOLID_NUGGET_TO_MOL, 0.0, 640.0),
                new SliderEntry(tr("gui","max_per_component","config", "slider"),
                        "max_per_component", ChemConfig.MAX_MOLES_PER_COMPONENT, 0.1, 640.0),
                new SliderEntry(tr("gui","max_total","config", "slider"),
                        "max_total", ChemConfig.MAX_MOLES_PER_COMPONENT, 0.1, 640.0)
        );

        contentHeight = y + 30;

        // 4. 底部按钮
        int bottomY = height - 30;

        Button saveBtn = Button.builder(
                Component.translatable("gui.trainees.config.button.save")
                        .withStyle(ChatFormatting.GREEN),
                button -> onSave()
        ).bounds(width / 2 - 120, bottomY, 70, 20).build();

        Button resetBtn = Button.builder(
                Component.translatable("gui.trainees.config.button.reset"),
                button -> onResetToDefault()
        ).bounds(width / 2 - 40, bottomY, 80, 20).build();

        Button closeBtn = Button.builder(
                Component.translatable("gui.trainees.config.button.close"),
                button -> onClose()
        ).bounds(width / 2 + 50, bottomY, 70, 20).build();

        this.widgets.add(saveBtn);
        this.widgets.add(resetBtn);
        this.widgets.add(closeBtn);

        // 注册所有组件
        for (AbstractWidget widget : widgets) {
            this.addRenderableWidget(widget);
        }

        // 计算最大滚动距离
        maxScroll = Math.max(0, contentHeight - height + 80);

        // 加载当前值
        loadValuesFromFile();
        updateDifficultyMatching();
    }

    // ==================== 辅助方法 ====================

    private int addSliderGroup(int y, String groupName, SliderEntry... entries) {
        StringWidget groupLabel = new StringWidget(
                INDENT, y, 200, 15,
                Component.literal(groupName),
                Minecraft.getInstance().font
        );
        this.widgets.add(groupLabel);
        y += 18;

        for (SliderEntry entry : entries) {
            double currentValue = entry.supplier.getAsDouble();
            ConfigSlider slider = new ConfigSlider(
                    entry.displayName, entry.key,
                    INDENT + 20, y, SLIDER_WIDTH, SLIDER_HEIGHT,
                    entry.min, entry.max, currentValue,
                    entry.consumer
            );
            this.widgets.add(slider);
            this.sliders.put(entry.key, slider);
            y += ROW_HEIGHT;
        }
        return y + GROUP_PADDING;
    }

    private int addReadOnlyGroup(int y, String groupName, String... lines) {
        StringWidget groupLabel = new StringWidget(
                INDENT, y, 200, 15,
                Component.literal(groupName),
                Minecraft.getInstance().font
        );
        this.widgets.add(groupLabel);
        y += 18;

        for (String line : lines) {
            StringWidget label = new StringWidget(
                    INDENT + 20, y, 300, 12,
                    Component.literal("§8" + line),
                    Minecraft.getInstance().font
            );
            this.widgets.add(label);
            y += 14;
        }
        return y + GROUP_PADDING;
    }

    private void onDifficultySelected(DifficultyLevel level) {
        this.selectedDifficulty = level;
        this.isUpdatingFromPreset = true;

        ChemConfig.DifficultyPresets.ConfigValues preset = ChemConfig.DifficultyPresets.get(level);

        for (Map.Entry<String, ConfigSlider> entry : sliders.entrySet()) {
            String key = entry.getKey();
            ConfigSlider slider = entry.getValue();
            double value = getPresetValue(preset, key);
            if (!Double.isNaN(value)) {
                slider.setValue(value);
            }
        }

        this.isUpdatingFromPreset = false;

        refreshButtons();
        refreshScoreDisplay();
        updateDifficultyMatching();
    }

    private double getPresetValue(ChemConfig.DifficultyPresets.ConfigValues preset, String key) {
        switch (key) {
            case "oreValuable": return preset.oreValuableMultiplier;
            case "oreGangue": return preset.oreGangueMultiplier;
            case "pollutionSpeed": return preset.pollutionDiffusionSpeed;
            case "pollutionToxicity": return preset.pollutionToxicityThreshold;
            case "plantPenalty": return ChemConfig.POLLUTION_PLANT_GROWTH_PENALTY.get();
            case "energyConsumption": return preset.energyConsumptionMultiplier;
            case "energyGeneration": return preset.energyGenerationMultiplier;
            default: return Double.NaN;
        }
    }

    private void refreshButtons() {
        for (AbstractWidget widget : widgets) {
            if (widget instanceof Button btn) {
                for (DifficultyLevel level : DifficultyLevel.values()) {
                    if (btn.getMessage().getString().equals(level.displayName)) {
                        btn.active = level != selectedDifficulty;
                    }
                }
            }
        }
    }

    private void refreshScoreDisplay() {
        double score = ChemConfig.DifficultyScorer.calculateScore();
        for (AbstractWidget widget : widgets) {
            if (widget instanceof StringWidget sw) {
                String msg = sw.getMessage().getString();
                if (msg.startsWith("难度指标")) {
                    sw.setMessage(Component.translatable("gui.trainees.config.score", DF.format(score))
                            .withStyle(ChatFormatting.GOLD));
                }
            }
        }
    }

    private void updateDifficultyMatching() {
        Map<String, Double> currentValues = new HashMap<>();
        for (Map.Entry<String, ConfigSlider> entry : sliders.entrySet()) {
            currentValues.put(entry.getKey(), entry.getValue().getValue());
        }

        boolean matched = false;
        for (DifficultyLevel level : DifficultyLevel.values()) {
            if (level == DifficultyLevel.CUSTOM) continue;
            ChemConfig.DifficultyPresets.ConfigValues preset = ChemConfig.DifficultyPresets.get(level);
            if (matchesPreset(currentValues, preset)) {
                this.selectedDifficulty = level;
                matched = true;
                break;
            }
        }

        if (!matched) {
            this.selectedDifficulty = DifficultyLevel.CUSTOM;
        }

        refreshButtons();
        refreshScoreDisplay();
    }

    private boolean matchesPreset(Map<String, Double> current, ChemConfig.DifficultyPresets.ConfigValues preset) {
        double epsilon = 0.001;
        for (Map.Entry<String, Double> entry : current.entrySet()) {
            String key = entry.getKey();
            double value = entry.getValue();
            double presetValue = getPresetValue(preset, key);
            if (Double.isNaN(presetValue)) continue;
            if (Math.abs(value - presetValue) > epsilon) {
                return false;
            }
        }
        return true;
    }

    private void loadValuesFromFile() {
        // 滑块初始化时已经从 supplier 读取值，无需额外操作
    }

    // ==================== 保存与关闭 ====================

    // 关键修正：保存当前存档配置
    private void onSave() {
        // 1. 确保所有滑块的值已提交
        for (Map.Entry<String, ConfigSlider> entry : sliders.entrySet()) {
            entry.getValue().commitValue();
        }

        // 2. 保存配置到当前存档（SERVER_SPEC）
        ChemConfig.SERVER_SPEC.save();

        // 3. 提示玩家
        if (Minecraft.getInstance().player != null) {
            Minecraft.getInstance().player.displayClientMessage(
                    Component.translatable("gui.trainees.config.save.success")
                            .withStyle(ChatFormatting.GREEN),
                    true
            );
        }

        Main.LOGGER.info("Chemical configuration saved to current world.");
    }

    private void onResetToDefault() {
        onDifficultySelected(DifficultyLevel.NORMAL);
    }

    public void onClose() {
        Minecraft.getInstance().setScreen(parentScreen);
    }

    // ==================== 渲染与交互（含滚动） ====================

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);

        // 应用滚动偏移
        var poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.translate(0, -scrollOffset, 0);

        for (AbstractWidget widget : widgets) {
            widget.render(guiGraphics, mouseX, mouseY + scrollOffset, partialTick);
        }

        poseStack.popPose();

        // 标题（不滚动）
        String title = String.valueOf(Component.translatable("gui.trainees.config.title"));
        guiGraphics.drawString(
                Minecraft.getInstance().font,
                title,
                (width - Minecraft.getInstance().font.width(title)) / 2,
                5,
                0xFFFFFF,
                false
        );

        // 存档名提示（不滚动）
        if (Minecraft.getInstance().getSingleplayerServer() != null) {
            String worldName = Minecraft.getInstance().getSingleplayerServer().getWorldData().getLevelName();
            guiGraphics.drawString(
                    Minecraft.getInstance().font,
                    Component.translatable("gui.trainees.config.save") + worldName,
                    10, height - 10,
                    0x888888,
                    false
            );
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // 鼠标点击需要加上滚动偏移
        return super.mouseClicked(mouseX, mouseY + scrollOffset, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (delta != 0) {
            int newOffset = (int) (scrollOffset - delta * 15);
            scrollOffset = Math.max(0, Math.min(maxScroll, newOffset));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    // ==================== 内部类：滑动条 ====================

    private static class SliderEntry {
        final String displayName;
        final String key;
        final DoubleSupplier supplier;
        final DoubleConsumer consumer;
        final double min;
        final double max;

        SliderEntry(String displayName, String key,
                    ForgeConfigSpec.DoubleValue configValue,
                    double min, double max) {
            this.displayName = displayName;
            this.key = key;
            this.supplier = configValue::get;
            this.consumer = configValue::set;
            this.min = min;
            this.max = max;
        }
    }

    private class ConfigSlider extends AbstractSliderButton {
        private final String displayName;
        private final String key;
        private final DoubleConsumer consumer;
        private final double min;
        private final double max;
        private double currentValue;

        public ConfigSlider(String displayName, String key,
                            int x, int y, int width, int height,
                            double min, double max, double currentValue,
                            DoubleConsumer consumer) {
            super(x, y, width, height, Component.translatable(key), 0);
            this.displayName = displayName;
            this.key = key;
            this.consumer = consumer;
            this.min = min;
            this.max = max;
            this.currentValue = currentValue;
            this.value = (currentValue - min) / (max - min);
            updateMessage();
        }

        @Override
        protected void updateMessage() {
            String display = Component.translatable(displayName).getString();
            this.setMessage(Component.literal(
                    display + ": §f" + DF.format(currentValue) + " §8[" + DF.format(min) + " - " + DF.format(max) + "]"
            ));
        }

        @Override
        protected void applyValue() {
            currentValue = min + (max - min) * value;
            consumer.accept(currentValue);

            if (!isUpdatingFromPreset) {
                updateDifficultyMatching();
                refreshScoreDisplay();
            }
        }

        public double getValue() {
            return currentValue;
        }

        public void setValue(double value) {
            this.currentValue = Math.max(min, Math.min(max, value));
            this.value = (this.currentValue - min) / (max - min);
            updateMessage();
        }

        public void commitValue() {
            consumer.accept(currentValue);
        }
    }
}