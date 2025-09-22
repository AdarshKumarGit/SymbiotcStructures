package org.chubby.github.symbioticstructures.client;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.chubby.github.symbioticstructures.Constants;
import org.chubby.github.symbioticstructures.client.screen.ModInfoScreen;
import org.chubby.github.symbioticstructures.client.screen.blocks.HeartstoneScreen;
import org.chubby.github.symbioticstructures.common.registry.SSMenus;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents
{
    @SubscribeEvent
    public static void clientSetup (FMLClientSetupEvent event)
    {
        event.enqueueWork(()->{
            MenuScreens.register(SSMenus.HEARTSTONE_MENU.get(), HeartstoneScreen::new);
        });
    }

    @SubscribeEvent
    public static void onRenderOverlay(RegisterGuiOverlaysEvent event)
    {
        event.registerAboveAll("screen_overlay", ModInfoScreen.SCREEN_OVERLAY);
    }
}
