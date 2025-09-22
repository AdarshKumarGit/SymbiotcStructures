package org.chubby.github.symbioticstructures.common.registry;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.chubby.github.symbioticstructures.Constants;
import org.chubby.github.symbioticstructures.common.blocks.heartstone.HeartStoneBlock;

import java.util.function.Supplier;

public class SSBlocks
{
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Constants.MOD_ID);

    public static final RegistryObject<Block> HEARTSTONE = registerBlock("heartstone",()-> new HeartStoneBlock(BlockBehaviour.Properties.copy(Blocks.STONE)));

    public static <I extends Block> RegistryObject<I> registerBlock(String name, Supplier<I> blockSup)
    {
        RegistryObject<I> toReg = BLOCKS.register(name,blockSup);
        SSItems.registerItem(name,()->new BlockItem(toReg.get(),new Item.Properties()),false);
        return toReg;
    }
}
