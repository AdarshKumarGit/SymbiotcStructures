package org.chubby.github.symbioticstructures.client.screen;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import org.chubby.github.symbioticstructures.Constants;

public class ModInfoScreen
{
    public static final IGuiOverlay SCREEN_OVERLAY = (forgeGui, guiGraphics, pPartialTick, packedLight, packedOverlay) ->
    {
        Minecraft mc = Minecraft.getInstance();
        String modName = Constants.MOD_NAME;
        String modVersion = "0.1-alpha";

        String display = modName + " v" + modVersion;
        guiGraphics.drawString(mc.font, Component.literal(modName).withStyle(ChatFormatting.AQUA), 5, 5, 0xFFFFFF, true);
        guiGraphics.drawString(mc.font, Component.literal("v" + modVersion).withStyle(ChatFormatting.GRAY), 5, 15, 0xFFFFFF, true);
    };
}
