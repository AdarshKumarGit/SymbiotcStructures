package org.chubby.github.symbioticstructures.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.chubby.github.symbioticstructures.Constants;
import org.chubby.github.symbioticstructures.client.screen.ModInfoScreen;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents
{
    @SubscribeEvent
    public static void onRenderOverlay(RegisterGuiOverlaysEvent event)
    {
        event.registerAboveAll("screen_overlay", ModInfoScreen.SCREEN_OVERLAY);
    }
}
