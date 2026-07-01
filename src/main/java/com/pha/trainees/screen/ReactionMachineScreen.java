package com.pha.trainees.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.pha.trainees.Main;
import com.pha.trainees.menu.ReactionMachineMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class ReactionMachineScreen extends AbstractContainerScreen<ReactionMachineMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(
            Main.MODID, "textures/gui/reaction_machine.png");

    public ReactionMachineScreen(ReactionMachineMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }


    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        guiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        // 显示反应进度条
        int progress = (int) (menu.getData().get(1) / (float) 100 * 24); // 假设最大100 tick，这里简单示例
        guiGraphics.blit(TEXTURE, leftPos + 79, topPos + 34, 176, 0, progress, 17);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
