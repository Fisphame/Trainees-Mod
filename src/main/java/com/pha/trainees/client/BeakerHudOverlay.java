package com.pha.trainees.client;

import com.pha.trainees.chemistry.block.BeakerBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class BeakerHudOverlay implements IGuiOverlay {

    @Override
    public void render(ForgeGui forgeGui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        if (mc.hitResult instanceof net.minecraft.world.phys.BlockHitResult blockHit) {
            BlockPos pos = blockHit.getBlockPos();
            BlockState state = mc.level.getBlockState(pos);

            if (state.getBlock() instanceof BeakerBlock) {
                String text = "烧杯";
                int x = (screenWidth - mc.font.width(text)) / 2;
                int y = screenHeight / 2 + 30;

                int bgWidth = mc.font.width(text) + 10;
                int bgHeight = mc.font.lineHeight + 6;
                int bgX = x - 5;
                int bgY = y - 3;

                guiGraphics.fill(bgX, bgY, bgX + bgWidth, bgY + bgHeight, 0x88000000);
                guiGraphics.drawString(mc.font, text, x, y, 0xFFFFFF, true);
            }
        }
    }
}