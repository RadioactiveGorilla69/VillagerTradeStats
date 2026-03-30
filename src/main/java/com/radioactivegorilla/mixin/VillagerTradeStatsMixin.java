package com.radioactivegorilla.mixin;


import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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

    protected VillagerTradeStatsMixin(Component title) {
        super(title);
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void renderBackground(GuiGraphics context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        MerchantScreen merchantScreen = (MerchantScreen) (Object) this;
        MerchantMenu handler = merchantScreen.getMenu();
        MerchantOffers offers = handler.getOffers();

        if (!isWanderingTrader(offers)) {
            int x = this.width/2 - 153;
            drawThinBackground(context, x - 8, this.height/2 - 83, 26, 256);
        }
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void renderXpAndTradeLevelAndTradeUses(GuiGraphics context, int mouseX, int mouseY, float delta, CallbackInfo ci){
        MerchantScreen merchantScreen = (MerchantScreen) (Object) this;
        MerchantMenu handler = merchantScreen.getMenu();
        MerchantOffers offers = handler.getOffers();
        Font font = Minecraft.getInstance().font;
        int startIndex = ((MerchantScreenAccessor) merchantScreen).getIndexStartOffset();

        int x = this.width/2 - 154;

        if (!isWanderingTrader(offers)) {
            int highestXpPerTrade = getHighestXpPerTrade(offers, startIndex);

            drawXpAndNextLevel(context, font, x, this.height/2 - 58, offers, scrollOff, highestXpPerTrade);
            context.drawString(font, "XP", x, this.height/2 - 77, 0xFF404040, false);
            context.drawString(font, "Villager XP: " + handler.getTraderXp(), (this.width - font.width("Villager XP: " + handler.getTraderXp()))/2, this.height/2 - 100, 0xFFFFFFFF, true);
        }
    }

    @Unique
    private int getHighestXpPerTrade(MerchantOffers offers, int startIndex){
        int highestXp = 0;
        for (int i = startIndex; i < Math.min(offers.size(), startIndex + 7); i++) {
            int offerXp = offers.get(i).getXp();
            if (offerXp > highestXp) {
                highestXp = offerXp;
            }
        }
        return highestXp;
    }

    @Unique
    private boolean isWanderingTrader(MerchantOffers offers){
        if (offers.size() <= 4) return false;
        for (MerchantOffer offer : offers) {
            if (offer.getXp() != 1) {
                return false;
            }
        }
        return true;
    }

    @Unique
    private void drawThinBackground(GuiGraphics context, int x, int y, int width, int height){
        ResourceLocation thin_background = ResourceLocation.fromNamespaceAndPath("villagertradestats", "textures/gui/thin_background.png");
        context.blit(RenderType::guiTextured, thin_background, x, y, 0, 0, width, height, width, 256);
    }

    @Unique
    private void drawTradesUntilSoldOut(GuiGraphics context, Font font, int x, int y, MerchantOffers offers, int i){
        context.drawString(font, String.valueOf(offers.get(i).getMaxUses() - offers.get(i).getUses()), x, y, 0xFFFFFFFF, false);
    }

    @Unique
    private void drawXpAndNextLevel(GuiGraphics context, Font font, int x, int y, MerchantOffers offers, int startIndex, int highestXpPerTrade){
        for (int i = 0; i < Math.min(offers.size() - startIndex, 7); i++) {
            MerchantOffer offer = offers.get(i + startIndex);
            if(i != 0) y += 20;

            int color = (offer.getXp() == highestXpPerTrade) ? 0xFF09eb10 : 0xFF404040;
            boolean bold = (offer.getXp() == highestXpPerTrade);
            drawTradesUntilSoldOut(context, font, this.width/2 - 79, y + 5, offers, i + startIndex);
            context.drawString(font, String.valueOf(offer.getXp()), x + (font.width("XP") - font.width(String.valueOf(offer.getXp())))/2, y, color, bold);
        }
    }
}