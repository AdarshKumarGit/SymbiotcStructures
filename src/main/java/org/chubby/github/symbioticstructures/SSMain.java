package org.chubby.github.symbioticstructures;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class SSMain
{

    public SSMain()
    {

    }

    public static ResourceLocation rl (String pPath)
    {
        return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID,pPath);
    }
}
