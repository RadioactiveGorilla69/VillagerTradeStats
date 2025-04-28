package com.radioactivegorilla.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MerchantScreen.class)
public abstract class VillagerTradeStatsMixin extends Screen {
    protected VillagerTradeStatsMixin(Text title) {
        super(title);
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void renderXpAndTradeLevel(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        MerchantScreen merchantScreen = (MerchantScreen) (Object) this;
        MerchantScreenHandler handler = merchantScreen.getScreenHandler();
        TradeOfferList offers = handler.getRecipes();
        TextRenderer font = MinecraftClient.getInstance().textRenderer;
        int startIndex = ((MerchantScreenAccessor) merchantScreen).getIndexStartOffset();

        int x = this.width / 2 - 153;

        int merchantXp = handler.getExperience();

        if (!isWanderingTrader(offers)) {
            int requiredXp;

            if (merchantXp > 250) {
                requiredXp = 0;
            } else if (merchantXp >= 150) {
                requiredXp = 250 - merchantXp;
            } else if (merchantXp >= 70) {
                requiredXp = 150 - merchantXp;
            } else if (merchantXp >= 10) {
                requiredXp = 70 - merchantXp;
            } else {
                requiredXp = 10 - merchantXp;
            }

            int tradesToLevelUp;
            int highestXPperTrade = getHighestXpPerTrade(offers, startIndex);

            for (int i = 0; i < Math.min(offers.size() - startIndex, 7); i++) {
                TradeOffer offer = offers.get(i + startIndex);
                tradesToLevelUp = (int) Math.ceil((double) (requiredXp) / offer.getMerchantExperience());
                int y = this.height / 2 - 58 + (i * 20);

                int color = (offer.getMerchantExperience() == highestXPperTrade) ? 0x09eb10 : 0xFFFFFF;
                boolean bold = (offer.getMerchantExperience() == highestXPperTrade);

                context.drawText(font, String.valueOf(offer.getMerchantExperience()), x, y, color, bold);
                if (requiredXp != 0) {
                    context.drawText(font, String.valueOf(tradesToLevelUp), x - 120 + (font.getWidth("Trades to next level") - font.getWidth(String.valueOf(tradesToLevelUp))) / 2, y, color, bold);
                }
            }
            /*if(offers.size() > 7 && requiredXp != 0){
                context.drawText(font, "Trades to next level", x - 120, this.height / 2 - 77, 0xFFFFFF, false);
                context.drawBorder(x - 125, this.height / 2 - 83, 135, Math.min(offers.size(), 7) * 20 + 25, 0xFF000000);
            }*/
            if(requiredXp == 0){
                context.drawBorder(x - 5, this.height / 2 - 83, 20, Math.min(offers.size(), 7) * 20 + 25, 0xFF000000);
            }
            else{
                int displacementFit = 0;
                if(offers.size() >= 7){
                    displacementFit = 7;
                }
                context.drawText(font, "Trades to next level", x - 120, this.height / 2 - 77, 0xFFFFFF, false);
                context.drawBorder(x - 125, this.height / 2 - 83, 140, Math.min(offers.size(), 7) * 20 + 18 + displacementFit, 0xFF000000);
            }
            context.drawText(font, "XP", x, this.height / 2 - 77, 0xFFFFFF, true);
            context.drawText(font, "Villager XP: " + handler.getExperience(), (this.width - font.getWidth("Villager XP: " + handler.getExperience())) / 2, this.height / 4 + 35, 0xFFFFFF, true);
        }
    }

    @Unique
    private int getHighestXpPerTrade(TradeOfferList offers, int startIndex) {
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
    private boolean isWanderingTrader(TradeOfferList offers) {
        if (offers.size() <= 4) return false;
        for (TradeOffer offer : offers) {
            if (offer.getMerchantExperience() != 1) {
                return false;
            }
        }
        return true;
    }

}