package com.radioactivegorilla.mixin;

import com.radioactivegorilla.MerchantContext;
import com.radioactivegorilla.config.VillagerTradeStatsConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MerchantScreen.class)
public abstract class VillagerTradeStatsMixin extends Screen {

    @Shadow int scrollOff;

    @Unique private static final ResourceLocation THIN_BACKGROUND = ResourceLocation.fromNamespaceAndPath("villagertradestats", "textures/gui/thin_background.png");

    @Unique private static final int MAX_VISIBLE_TRADES = 7;

    @Unique private static final int ROW_HEIGHT = 20;

    @Unique private static final int BACKGROUND_OFFSET_X = -160;
    @Unique private static final int BACKGROUND_OFFSET_Y = -83;

    @Unique private static final int XP_COLUMN_OFFSET_X = -153;
    @Unique private static final int XP_COLUMN_OFFSET_Y = -58;
    @Unique private static final int XP_HEADER_OFFSET_Y = -19;

    @Unique private static final int TRADES_LEFT_OFFSET_X = -79;

    @Unique private static final int VILLAGER_XP_OFFSET_Y = -100;

    protected VillagerTradeStatsMixin(Component title) {
        super(title);
    }

    @Inject(method = "renderContents", at = @At("RETURN"))
    private void renderOverlay(GuiGraphics context, int i, int j, float f, CallbackInfo ci) {
        if(!isVillager()) {
            return;
        }

        VillagerTradeStatsConfig cfg = config();

        MerchantScreen merchantScreen = (MerchantScreen) (Object) this;
        MerchantMenu handler = merchantScreen.getMenu();
        MerchantOffers offers = handler.getOffers();
        Font font = Minecraft.getInstance().font;
        int highestXpPerTrade = getHighestXpPerTrade(offers, scrollOff);

        int centerX = this.width/2;
        int centerY = this.height/2;

        int backgroundX = centerX + BACKGROUND_OFFSET_X;
        int backgroundY = centerY + BACKGROUND_OFFSET_Y;

        int xpValueX = centerX + XP_COLUMN_OFFSET_X;
        int xpValueY = centerY + XP_COLUMN_OFFSET_Y;

        renderBackground(context, backgroundX, backgroundY, cfg);
        drawTradeStatsColumn(context, font, xpValueX, xpValueY, offers, scrollOff, highestXpPerTrade, cfg);
        drawVillagerXp(context, font, handler, cfg);
    }

    @Unique
    private void renderBackground(GuiGraphics context, int x, int y, VillagerTradeStatsConfig cfg) {
        if (cfg.showTradeXp) {
            context.blit(RenderPipelines.GUI_TEXTURED, THIN_BACKGROUND, x, y, 0, 0, 25, 256, 25, 256);
        }
    }

    @Unique
    private void drawTradeStatsColumn(GuiGraphics context, Font font, int x, int y, MerchantOffers offers, int startIndex, int highestXpPerTrade, VillagerTradeStatsConfig cfg) {
        if(cfg.showTradeXp) {
            int xpTextY = y + XP_HEADER_OFFSET_Y; // 19 pixel offset
            context.drawString(font, "XP", x, xpTextY, 0xFF404040, false);
        }

        int xpTextWidth = font.width("XP");
        int tradesLeftX = this.width/2 + TRADES_LEFT_OFFSET_X;
        int highestXpColor = cfg.highestXpColor.getRGB() | 0xFF000000;

        for (int i = 0; i < Math.min(Math.max(offers.size() - startIndex, 0), MAX_VISIBLE_TRADES); i++) { //Math.max in case startIndex is ever greater than offer size (shouldn't happen in vanilla)
            int rowY = y + i * ROW_HEIGHT;
            MerchantOffer offer = offers.get(i + startIndex);
            drawTradeXp(context, font, x, rowY, offer, highestXpPerTrade, highestXpColor, xpTextWidth, cfg);
            drawUsesLeft(context, font, tradesLeftX, rowY + 5, offer, cfg);
        }
    }

    @Unique
    private void drawVillagerXp(GuiGraphics context, Font font, MerchantMenu handler, VillagerTradeStatsConfig cfg) {
        if(cfg.showVillagerXp) {
            String villagerXpText = "Villager XP: " + handler.getTraderXp();
            int villagerXpTextY = this.height/2 + VILLAGER_XP_OFFSET_Y; //half of screen and 100 pixels up
            int villagerXpTextX = (this.width - font.width(villagerXpText))/2;

            context.drawString(font, villagerXpText, villagerXpTextX, villagerXpTextY, 0xFFFFFFFF, true);
        }
    }

    @Unique
    private int getHighestXpPerTrade(MerchantOffers offers, int startIndex) {
        int highestXp = 0;

        for (int i = startIndex; i < Math.min(offers.size(), startIndex + MAX_VISIBLE_TRADES); i++) {
            int offerXp = offers.get(i).getXp();
            if (offerXp > highestXp) {
                highestXp = offerXp;
            }
        }

        return highestXp;
    }

    @Unique
    private void drawTradeXp(GuiGraphics context, Font font, int x, int y, MerchantOffer offer, int highestXpPerTrade, int highestXpColor, int xpTextWidth, VillagerTradeStatsConfig cfg) {
        if (cfg.showTradeXp) {
            int xp = offer.getXp();
            String xpText = String.valueOf(xp);
            int xpValueWidth = font.width(xpText);
            boolean isHighest = (xp == highestXpPerTrade);
            int color = (isHighest) ? highestXpColor : 0xFF404040;

            context.drawString(font, xpText, x + (xpTextWidth - xpValueWidth) / 2, y, color, isHighest); //isHighest for bold text
        }
    }

    @Unique
    private void drawUsesLeft(GuiGraphics context, Font font, int x, int y, MerchantOffer offer, VillagerTradeStatsConfig cfg) {
        if(cfg.showUses) {
            context.drawString(font, String.valueOf(offer.getMaxUses() - offer.getUses()), x, y, 0xFFFFFFFF, false);
        }
    }

    @Unique
    private boolean isVillager() {
        Entity entity = MerchantContext.get();
        return entity instanceof Villager;
    }

    @Unique
    private VillagerTradeStatsConfig config() {
        return VillagerTradeStatsConfig.HANDLER.instance();
    }
}