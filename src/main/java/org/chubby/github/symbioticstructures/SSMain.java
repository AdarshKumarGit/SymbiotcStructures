package org.chubby.github.symbioticstructures;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.chubby.github.symbioticstructures.common.registry.SSBlockEntities;
import org.chubby.github.symbioticstructures.common.registry.SSBlocks;
import org.chubby.github.symbioticstructures.common.registry.SSItems;

@Mod(Constants.MOD_ID)
public class SSMain
{

    public SSMain()
    {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        SSItems.ITEMS.register(eventBus);
        SSBlocks.BLOCKS.register(eventBus);
        SSBlockEntities.BLOCK_ENTITIES.register(eventBus);
    }

    public static ResourceLocation rl (String pPath)
    {
        return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID,pPath);
    }
}
