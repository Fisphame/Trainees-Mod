//package com.pha.trainees.screens;
//
//import com.pha.trainees.Main;
//import com.pha.trainees.menu.PurificationMenu;
//import net.minecraft.client.gui.GuiGraphics;
//import net.minecraft.client.gui.components.Button;
//import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
//import net.minecraft.network.chat.Component;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.world.entity.player.Inventory;
//
//public class PurificationScreen extends AbstractContainerScreen<PurificationMenu> {
//    // GUI纹理位置
//    private static final ResourceLocation TEXTURE =
//            new ResourceLocation(Main.MODID, "textures/gui/container/purification_station.png");
//
////    // 进度条位置和尺寸
////    private static final int PROGRESS_WIDTH = 24;
////    private static final int PROGRESS_HEIGHT = 17;
////    private static final int PROGRESS_X = 79;
////    private static final int PROGRESS_Y = 35;
////
////    // 火焰图标位置和尺寸
////    private static final int FLAME_WIDTH = 14;
////    private static final int FLAME_HEIGHT = 14;
////    private static final int FLAME_X = 56;
////    private static final int FLAME_Y = 37;
//
//    // 按钮位置和尺寸
//    private static final int BUTTON_X = 120;
//    private static final int BUTTON_Y = 60;
//    private static final int BUTTON_WIDTH = 40;
//    private static final int BUTTON_HEIGHT = 20;
//
//    public PurificationScreen(PurificationMenu menu, Inventory playerInventory, Component title) {
//        super(menu, playerInventory, title);
//        this.imageWidth = 176; // GUI宽度
//        this.imageHeight = 166; // GUI高度
//        this.inventoryLabelY = this.imageHeight - 94; // 玩家物品栏标签位置
//    }
//
//    @Override
//    protected void init() {
//        super.init();
//        Main.LOGGER.info("初始化净化界面");
//        // 添加按钮
//        this.addRenderableWidget(new Button.Builder(
//                Component.translatable("button.trainees.purify"),
//                button -> {
//                    Main.LOGGER.info("按钮点击事件触发");
//                    this.menu.triggerCrafting();
//                }
//        )
//                .bounds(
//                        this.leftPos + BUTTON_X,
//                        this.topPos + BUTTON_Y,
//                        BUTTON_WIDTH,
//                        BUTTON_HEIGHT
//                )
//                .build());
//    }
//
//    @Override
//    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
//        int leftPos = this.leftPos;
//        int topPos = this.topPos;
//
//        // 绘制背景
//        guiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, this.imageWidth, this.imageHeight);
//
////        // 绘制进度条
////        int progress = this.menu.getProgress();
////        int maxProgress = this.menu.getMaxProgress();
////        if (progress > 0) {
////            int progressWidth = (int) (PROGRESS_WIDTH * ((float) progress / maxProgress));
////            guiGraphics.blit(TEXTURE,
////                    leftPos + PROGRESS_X, topPos + PROGRESS_Y,
////                    176, 0,
////                    progressWidth, PROGRESS_HEIGHT);
////        }
////
////        // 绘制火焰动画
////        if (menu.isProcessing()) {
////            int flameHeight = (int) (FLAME_HEIGHT * (System.currentTimeMillis() % 1000) / 1000f);
////            guiGraphics.blit(TEXTURE,
////                    leftPos + FLAME_X, topPos + FLAME_Y + (FLAME_HEIGHT - flameHeight),
////                    176, 17,
////                    FLAME_WIDTH, flameHeight);
////        }
//    }
//
//    @Override
//    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
//        super.renderLabels(guiGraphics, mouseX, mouseY);
//        // 绘制标题
//        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0x404040, false);
//
//        // 绘制玩家物品栏标签
//        guiGraphics.drawString(this.font, this.playerInventoryTitle,
//                this.inventoryLabelX, this.inventoryLabelY, 0x404040, false);
//
//        // 绘制槽位标签
//        guiGraphics.drawString(this.font,
//                Component.translatable("container.trainees.purification_station.input_slot"),
//                56 - this.leftPos, 6, 0x404040, false);
//
//        guiGraphics.drawString(this.font,
//                Component.translatable("container.trainees.purification_station.fuel_slot"),
//                56 - this.leftPos, 60, 0x404040, false);
//    }
//
//    @Override
//    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
//        this.renderBackground(guiGraphics);
//        super.render(guiGraphics, mouseX, mouseY, partialTicks);
//        this.renderTooltip(guiGraphics, mouseX, mouseY);
//    }
//
//}