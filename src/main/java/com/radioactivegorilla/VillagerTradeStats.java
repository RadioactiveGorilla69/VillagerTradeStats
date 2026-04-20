package com.radioactivegorilla;

import com.radioactivegorilla.config.VillagerTradeStatsConfig;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VillagerTradeStats implements ModInitializer {
	public static final String MOD_ID = "villagertradestats";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("VillagerTradeStats initialized.");
		VillagerTradeStatsConfig.HANDLER.load();
	}
}
