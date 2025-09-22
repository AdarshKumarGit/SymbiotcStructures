package org.chubby.github.symbioticstructures.common.registry;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.chubby.github.symbioticstructures.Constants;
import org.chubby.github.symbioticstructures.common.blocks.entity.heartstone.HeartStoneBlockEntity;

import java.util.function.Supplier;

public class SSBlockEntities
{
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Constants.MOD_ID);

    public static final RegistryObject<BlockEntityType<HeartStoneBlockEntity>> HEARTSTONE_BLOCK_ENTITY = register("heartstone_be",
            ()->BlockEntityType.Builder.<HeartStoneBlockEntity>of(HeartStoneBlockEntity::new,SSBlocks.HEARTSTONE.get()));

    private static <E extends BlockEntity> RegistryObject<BlockEntityType<E>> register(
            String name,
            Supplier<BlockEntityType.Builder<E>> builderSupplier
    ) {
        return BLOCK_ENTITIES.register(name, () -> builderSupplier.get().build(null));
    }

}
