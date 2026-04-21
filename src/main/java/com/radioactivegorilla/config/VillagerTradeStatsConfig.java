package com.radioactivegorilla.config;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.ColorControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import dev.isxander.yacl3.platform.YACLPlatform;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.awt.*;

public class VillagerTradeStatsConfig {
    public static ConfigClassHandler<VillagerTradeStatsConfig> HANDLER = ConfigClassHandler.createBuilder(VillagerTradeStatsConfig.class)
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(YACLPlatform.getConfigDir().resolve("villagertradestats.json"))
                    .build())
            .build();

    @SerialEntry
    public boolean showTradeXp = true;

    @SerialEntry
    public boolean showUses = true;

    @SerialEntry
    public boolean showVillagerXp = true;

    @SerialEntry
    public Color highestXpColor = new Color(0xFF09eb10);

    public static Screen configScreen(Screen parent) {
        return YetAnotherConfigLib.create(HANDLER, (defaults, config, builder) -> builder
                .title(Component.literal("Villager Trade Stats"))
                .category(ConfigCategory.createBuilder()
                        .name(Component.literal("Villager Trade Stats"))
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.literal("Show Trade XP"))
                                .description(OptionDescription.of(Component.literal("Displays XP gained by the villager per trade")))
                                .binding(defaults.showTradeXp, () -> config.showTradeXp, val -> config.showTradeXp = val)
                                .controller(BooleanControllerBuilder::create)
                                .build())
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.literal("Show Trades Left"))
                                .description(OptionDescription.of(Component.literal("Displays number of trades left until trade disabled")))
                                .binding(defaults.showUses, () -> config.showUses, val -> config.showUses = val)
                                .controller(BooleanControllerBuilder::create)
                                .build())
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.literal("Show Villager XP"))
                                .description(OptionDescription.of(Component.literal("Displays the current villager XP")))
                                .binding(defaults.showVillagerXp, () -> config.showVillagerXp, val -> config.showVillagerXp = val)
                                .controller(BooleanControllerBuilder::create)
                                .build())
                        .option(Option.<Color>createBuilder()
                                .name(Component.literal("Change Highest XP Color"))
                                .description(OptionDescription.of(Component.literal("Change the color of the Highest XP trade")))
                                .binding(defaults.highestXpColor, () -> config.highestXpColor, val -> config.highestXpColor = val)
                                .controller(ColorControllerBuilder::create)
                                .build())
                        .build())
                )
                .generateScreen(parent);
    }
}