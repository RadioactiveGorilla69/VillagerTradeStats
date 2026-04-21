package com.radioactivegorilla;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.InteractionResult;

public class VillagerTradeStatsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (world.isClientSide()) {
                if (entity instanceof Villager || entity instanceof WanderingTrader) {
                    MerchantContext.set(entity);
                }
            }
            return InteractionResult.PASS;
        });
    }
}