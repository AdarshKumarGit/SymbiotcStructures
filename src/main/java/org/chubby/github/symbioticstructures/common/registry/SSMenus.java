package org.chubby.github.symbioticstructures.common.registry;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.chubby.github.symbioticstructures.Constants;
import org.chubby.github.symbioticstructures.common.menu.HeartstoneMenu;

import java.util.function.Supplier;

public class SSMenus
{
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, Constants.MOD_ID);

    public static final RegistryObject<MenuType<HeartstoneMenu>> HEARTSTONE_MENU = MENU_TYPES.register("heartstone_menu",
            ()-> IForgeMenuType.create(HeartstoneMenu::new));
}
