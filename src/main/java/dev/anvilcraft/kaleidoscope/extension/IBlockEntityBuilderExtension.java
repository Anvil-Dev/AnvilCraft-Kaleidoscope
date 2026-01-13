package dev.anvilcraft.kaleidoscope.extension;

import com.tterrag.registrate.builders.BlockEntityBuilder;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Collection;

public interface IBlockEntityBuilderExtension<T extends BlockEntity, P> {
     BlockEntityBuilder<T, P> anvilcraftKaleidoscope$validBlocks(NonNullSupplier<Collection<? extends Block>> blocks);
}
