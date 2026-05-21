package dev.anvilcraft.kaleidoscope.extension;

import dev.anvilcraft.lib.v2.registrum.builders.BlockEntityBuilder;
import dev.anvilcraft.lib.v2.util.nullness.NonNullSupplier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Collection;

public interface IBlockEntityBuilderExtension<T extends BlockEntity, P> {
     BlockEntityBuilder<T, P> anvilcraftKaleidoscope$validBlocks(NonNullSupplier<Collection<? extends Block>> blocks);
}
