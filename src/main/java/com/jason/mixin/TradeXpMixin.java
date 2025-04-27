package com.jason.mixin;

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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MerchantScreen.class)
public abstract class TradeXpMixin extends Screen {
	protected TradeXpMixin(Text title) {
		super(title);
	}

	@Inject(method = "render", at = @At("RETURN"))
	private void renderXp(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		MerchantScreenHandler handler = ((MerchantScreen)(Object)this).getScreenHandler();
		TradeOfferList offers = handler.getRecipes();
		TextRenderer font = MinecraftClient.getInstance().textRenderer;

		// Start the XP text next to the second item of each trade offer
		for(int i = 0; i < offers.size(); i++) {
			TradeOffer offer = offers.get(i);
			int xp = offer.getMerchantExperience();

			// Based on how MerchantScreen lays out offers
			int x = this.width / 2 - 168; // Move to the right of the trade result slot
			int y = this.height / 2 - (i * 22) - 38; // Align vertically with trade row

			//TEST TOMORROW, i*x (20-24) ish, 168 38, also add color and config

			context.drawText(
					font,
					"XP: " + xp,
					x,
					y,
					0xFFFFFF, // White color for now
					false
			);
		}
	}
}
