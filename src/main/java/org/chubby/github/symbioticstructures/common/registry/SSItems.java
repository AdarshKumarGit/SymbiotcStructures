package org.chubby.github.symbioticstructures.common.registry;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.chubby.github.symbioticstructures.Constants;

import java.util.function.Supplier;

public class SSItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Constants.MOD_ID);

    public static <I extends Item> RegistryObject<I> registerItem(String name, Supplier<I> itemSup, boolean putInTab)
    {
        RegistryObject<I> toReg = ITEMS.register(name,itemSup);
        return toReg;
    }
}
