//package com.pha.trainees.config;
//
//import net.minecraft.client.gui.GuiGraphics;
//import net.minecraft.client.gui.components.Button;
//import net.minecraft.client.gui.components.EditBox;
//import net.minecraft.client.gui.screens.Screen;
//import net.minecraft.network.chat.CommonComponents;
//import net.minecraft.network.chat.Component;
//import net.minecraftforge.common.ForgeConfigSpec;
//
//public class TraineesConfigScreen extends Screen {
//    private final Screen parent;
//    private EditBox movementMaxField;
//    private int originalMovementMax;
//    private static final Component TITLE = Component.translatable("config.trainees.title");
//    private static final Component MOVEMENT_MAX_LABEL = Component.translatable("config.trainees.movement_max");
//
//    public TraineesConfigScreen(Screen parent) {
//        super(TITLE);
//        this.parent = parent;
//        this.originalMovementMax = TraineesConfig.ENTITY_RANDOM_MOVEMENT_MAX.get();
//    }
//
//    @Override
//    protected void init() {
//        super.init();
//
//        // 计算居中位置
//        int centerX = this.width / 2;
//        int centerY = this.height / 2;
//
////        // 添加标签
////        this.addRenderableWidget(new net.minecraft.client.gui.components.Label(
////                centerX - 100, centerY - 60,
////                200, 20,
////                Component.literal("生成物随机运动上限配置"),
////                this.font
////        ));
////
////        // 添加说明文本
////        this.addRenderableWidget(new net.minecraft.client.gui.components.Label(
////                centerX - 100, centerY - 40,
////                200, 40,
////                Component.literal("这个值会影响物品实体等随机移动的范围 (0-10000)"),
////                this.font
////        ));
//
//        // 创建输入框
//        this.movementMaxField = new EditBox(this.font, centerX - 50, centerY - 10, 100, 20,
//                Component.literal("随机运动上限"));
//        this.movementMaxField.setValue(String.valueOf(originalMovementMax));
//        this.addRenderableWidget(this.movementMaxField);
//
//        // 保存按钮
//        this.addRenderableWidget(Button.builder(Component.literal("保存"),
//                        button -> this.saveConfig())
//                .pos(centerX - 105, centerY + 30)
//                .size(100, 20)
//                .build());
//
//        // 取消按钮
//        this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL,
//                        button -> this.minecraft.setScreen(this.parent))
//                .pos(centerX + 5, centerY + 30)
//                .size(100, 20)
//                .build());
//
//        // 重置按钮
//        this.addRenderableWidget(Button.builder(Component.literal("重置"),
//                        button -> this.resetToDefault())
//                .pos(centerX - 50, centerY + 60)
//                .size(100, 20)
//                .build());
//    }
//
//    private void saveConfig() {
//        try {
//            int newValue = Integer.parseInt(this.movementMaxField.getValue());
//
//            // 验证范围
//            if (newValue < 0 || newValue > 10000) {
//                if (this.minecraft.player != null) {
//                    this.minecraft.player.displayClientMessage(
//                            Component.literal("数值必须在 0-10000 范围内"), false);
//                }
//                return;
//            }
//
//            // 更新配置值
//            TraineesConfig.ENTITY_RANDOM_MOVEMENT_MAX.set(newValue);
//            TraineesConfig.ENTITY_RANDOM_MOVEMENT_MAX.save();
//
//            if (this.minecraft.player != null) {
//                this.minecraft.player.displayClientMessage(
//                        Component.literal("配置已保存！新值: " + newValue), false);
//            }
//
//            // 返回上一屏幕
//            this.minecraft.setScreen(this.parent);
//
//        } catch (NumberFormatException e) {
//            if (this.minecraft.player != null) {
//                this.minecraft.player.displayClientMessage(
//                        Component.literal("请输入有效的数字"), false);
//            }
//        }
//    }
//
//    private void resetToDefault() {
//        this.movementMaxField.setValue("10"); // 默认值
//        if (this.minecraft.player != null) {
//            this.minecraft.player.displayClientMessage(
//                    Component.literal("已重置为默认值 10"), false);
//        }
//    }
//
//    @Override
//    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
//        this.renderBackground(guiGraphics);
//        super.render(guiGraphics, mouseX, mouseY, partialTick);
//
//        // 渲染标题
//        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);
//    }
//
//    @Override
//    public void onClose() {
//        this.minecraft.setScreen(this.parent);
//    }
//}