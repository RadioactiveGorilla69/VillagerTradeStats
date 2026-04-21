package com.radioactivegorilla;

import net.minecraft.world.entity.Entity;

public class MerchantContext {
    private static Entity merchant;

    public static void set(Entity e) {
        merchant = e;
    }

    public static Entity get() {
        return merchant;
    }
}