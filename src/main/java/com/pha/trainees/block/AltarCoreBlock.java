package com.pha.trainees.block;

import net.minecraft.world.level.block.Block;

public class AltarCoreBlock extends Block {
    private static Boolean Activation = false;

    public AltarCoreBlock(Properties p_49795_) {
        super(p_49795_);
    }

    public static void setActivation(Boolean b){
        Activation = b;
    }
    
    public static Boolean getActivation() {
        return Activation;
    }
}
