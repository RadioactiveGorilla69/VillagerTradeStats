package com.radioactivegorilla.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MerchantScreen.class)
public abstract class VillagerTradeStatsMixin extends Screen {

    @Shadow int indexStartOffset;

    protected VillagerTradeStatsMixin(Text title) {
        super(title);
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void renderBackground(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        MerchantScreen merchantScreen = (MerchantScreen) (Object) this;
        MerchantScreenHandler handler = merchantScreen.getScreenHandler();
        TradeOfferList offers = handler.getRecipes();

        if (!isWanderingTrader(offers)) {
            int x = this.width/2 - 153;
            drawThinBackground(context, x - 8, this.height/2 - 83, 26, 256);
        }
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void renderXpAndTradeLevelAndTradeUses(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci){
        MerchantScreen merchantScreen = (MerchantScreen) (Object) this;
        MerchantScreenHandler handler = merchantScreen.getScreenHandler();
        TradeOfferList offers = handler.getRecipes();
        TextRenderer font = MinecraftClient.getInstance().textRenderer;
        int startIndex = ((MerchantScreenAccessor) merchantScreen).getIndexStartOffset();

        int x = this.width/2 - 154;

        if (!isWanderingTrader(offers)) {
            int highestXpPerTrade = getHighestXpPerTrade(offers, startIndex);

            drawXpAndNextLevel(context, font, x, this.height/2 - 58, offers, indexStartOffset, highestXpPerTrade);
            context.drawText(font, "XP", x, this.height/2 - 77, 0xFF404040, false);
            context.drawText(font, "Villager XP: " + handler.getExperience(), (this.width - font.getWidth("Villager XP: " + handler.getExperience()))/2, this.height/4 + 35, 0xFFFFFF, true);
        }
    }

    @Unique
    private int getHighestXpPerTrade(TradeOfferList offers, int startIndex){
        int highestXp = 0;
        for (int i = startIndex; i < Math.min(offers.size(), startIndex + 7); i++) {
            int offerXp = offers.get(i).getMerchantExperience();
            if (offerXp > highestXp) {
                highestXp = offerXp;
            }
        }
        return highestXp;
    }

    @Unique
    private boolean isWanderingTrader(TradeOfferList offers){
        if (offers.size() <= 4) return false;
        for (TradeOffer offer : offers) {
            if (offer.getMerchantExperience() != 1) {
                return false;
            }
        }
        return true;
    }

    @Unique
    private void drawThinBackground(DrawContext context, int x, int y, int width, int height){
        Identifier thin_background = Identifier.of("villagertradestats", "textures/gui/thin_background.png");
        context.drawTexture(thin_background, x, y, 0, 0, width, height, width, 256);
    }

    @Unique
    private void drawTradesUntilSoldOut(DrawContext context, TextRenderer font, int x, int y, TradeOfferList offers, int i){
        context.drawText(font, String.valueOf(offers.get(i).getMaxUses() - offers.get(i).getUses()), x, y, 0xFFFFFF, false);
    }

    @Unique
    private void drawXpAndNextLevel(DrawContext context, TextRenderer font, int x, int y, TradeOfferList offers, int startIndex, int highestXpPerTrade){
        for (int i = 0; i < Math.min(offers.size() - startIndex, 7); i++) {
            TradeOffer offer = offers.get(i + startIndex);
            if(i != 0) y += 20;

            int color = (offer.getMerchantExperience() == highestXpPerTrade) ? 0x09eb10 : 0xFF404040;
            boolean bold = (offer.getMerchantExperience() == highestXpPerTrade);
            drawTradesUntilSoldOut(context, font, this.width/2 - 79, y + 5, offers, i);
            context.drawText(font, String.valueOf(offer.getMerchantExperience()), x + (font.getWidth("XP") - font.getWidth(String.valueOf(offer.getMerchantExperience())))/2, y, color, bold);
        }
    }
}