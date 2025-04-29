package com.radioactivegorilla.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
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
    private void renderBackground(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        MerchantScreen merchantScreen = (MerchantScreen) (Object) this;
        MerchantScreenHandler handler = merchantScreen.getScreenHandler();
        TradeOfferList offers = handler.getRecipes();

        if (!isWanderingTrader(offers)) {
            int x = this.width/2 - 153;
            int backgroundHeight = Math.min(offers.size(), 7) * 20 + 26;
            //int displacementFit = offers.size() >= 7 ? 7 : 0;

            int merchantXp = handler.getExperience();
            if (merchantXp >= 250) {
                drawThinBackground(context, x - 8, this.height/2 - 83, 26, backgroundHeight);
            } else {
                drawThickBackground(context, x - 62, this.height/2 - 83, 80, 7 * 20 + 26);
            }
        }
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void renderXpAndTradeLevel(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        MerchantScreen merchantScreen = (MerchantScreen) (Object) this;
        MerchantScreenHandler handler = merchantScreen.getScreenHandler();
        TradeOfferList offers = handler.getRecipes();
        TextRenderer font = MinecraftClient.getInstance().textRenderer;
        int startIndex = ((MerchantScreenAccessor) merchantScreen).getIndexStartOffset();

        int x = this.width/2 - 153;
        int merchantXp = handler.getExperience();

        if (!isWanderingTrader(offers)) {
            int requiredXp;

            if (merchantXp >= 250) {
                requiredXp = 0;
            }
            else if (merchantXp >= 150) {
                requiredXp = 250 - merchantXp;
            }
            else if (merchantXp >= 70) {
                requiredXp = 150 - merchantXp;
            }
            else if (merchantXp >= 10) {
                requiredXp = 70 - merchantXp;
            }
            else {
                requiredXp = 10 - merchantXp;
            }

            int highestXPperTrade = getHighestXpPerTrade(offers, startIndex);

            if (requiredXp != 0) {
                context.drawText(font, "Level Up", x - 54, this.height/2 - 77, 0xFF404040, false);
                NumTradesToNextLevelTooltip(context, "Level Up", x - 54, this.height/2 - 77, font, mouseX, mouseY);
            }

            for (int i = 0; i < Math.min(offers.size() - startIndex, 7); i++) {
                TradeOffer offer = offers.get(i + startIndex);
                int tradesToLevelUp = (int) Math.ceil((double)(requiredXp)/offer.getMerchantExperience());
                int y = this.height/2 - 58 + (i * 20);

                int color = (offer.getMerchantExperience() == highestXPperTrade) ? 0x09eb10 : 0xFF404040;
                boolean bold = (offer.getMerchantExperience() == highestXPperTrade);

                context.drawText(font, String.valueOf(offer.getMerchantExperience()), x, y, color, bold);
                if (requiredXp != 0) {
                    context.drawText(font, String.valueOf(tradesToLevelUp), x - 54 + (font.getWidth("Level Up"))/2, y, color, bold);
                }
            }

            context.drawText(font, "XP", x, this.height/2 - 77, 0xFF404040, false);
            context.drawText(font, "Villager XP: " + handler.getExperience(), (this.width - font.getWidth("Villager XP: " + handler.getExperience()))/2, this.height/4 + 35, 0xFFFFFF, true);
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

    @Unique
    private void drawThinBackground(DrawContext context, int x, int y, int width, int height) {
        Identifier thin_background = Identifier.of("villagertradestats", "textures/gui/thin_background.png");
        RenderSystem.setShaderTexture(0, thin_background);
        context.drawTexture(thin_background, x, y, 0, 0, width, height, width, 256);
    }

    @Unique
    private void drawThickBackground(DrawContext context, int x, int y, int width, int height) {
        Identifier thick_background = Identifier.of("villagertradestats", "textures/gui/thick_background.png");
        RenderSystem.setShaderTexture(0, thick_background);
        context.drawTexture(thick_background, x, y, 0, 0, width, height, width, 256);
    }

    @Unique
    private void NumTradesToNextLevelTooltip(DrawContext context, String text, int x, int y, TextRenderer font, int mouseX, int mouseY) {
        int textWidth = font.getWidth(text);
        int textHeight = 10;

        if(mouseX >= x && mouseX <= x + textWidth && mouseY >= y && mouseY <= y + textHeight) {
            context.drawTooltip(MinecraftClient.getInstance().textRenderer, Text.of("Number of trades until the villager levels up"), mouseX, mouseY);
        }
    }
}