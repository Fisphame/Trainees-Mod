package com.pha.trainees.thetwice;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class PlayerTick {

    @SubscribeEvent
    public static void onPlayerAttack(AttackEntityEvent event) {
        Player player = event.getEntity();

        ItemStack mainHandItem = player.getMainHandItem();

        if (mainHandItem.getItem() == Object.SIJIAN.get()&&!SiJian.IsInAirAttact) {
            SiJian.IsInAirAttact=true;
            SiJian.playe1 = event.getEntity();
        }
    }
}