package com.pha.trainees.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.pha.trainees.Main;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class PurificationStationScreen extends AbstractContainerScreen<PurificationStationMenu> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(Main.MODID, "textures/gui/container/purification_station.png");

    public PurificationStationScreen(PurificationStationMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();

        // 添加按钮（按钮ID，x，y，宽，高，文本，点击事件）
        this.addRenderableWidget(Button.builder(Component.literal("Craft"), button -> {
                    menu.craft(); // 调用容器菜单的craft方法
                })
                .bounds(leftPos + 79, topPos + 35, 30, 20)
                .build());
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}