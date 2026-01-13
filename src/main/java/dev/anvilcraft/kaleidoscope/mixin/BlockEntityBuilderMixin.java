package dev.anvilcraft.kaleidoscope.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.tterrag.registrate.builders.BlockEntityBuilder;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import dev.anvilcraft.kaleidoscope.extension.IBlockEntityBuilderExtension;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.IntFunction;
import java.util.stream.Stream;

@Mixin(BlockEntityBuilder.class)
public class BlockEntityBuilderMixin<T extends BlockEntity, P> implements IBlockEntityBuilderExtension<T, P> {
    @Unique
    @SuppressWarnings("unchecked")
    private final BlockEntityBuilder<T, P> anvilcraftKaleidoscope$self = (BlockEntityBuilder<T, P>) (Object) this;
    @Unique
    private final Set<NonNullSupplier<Collection<? extends Block>>> anvilcraftKaleidoscope$validBlocks = new HashSet<>();

    @Override
    public BlockEntityBuilder<T, P> anvilcraftKaleidoscope$validBlocks(NonNullSupplier<Collection<? extends Block>> blocks) {
        anvilcraftKaleidoscope$validBlocks.add(blocks);
        return anvilcraftKaleidoscope$self;
    }

    @WrapOperation(method = "createEntry()Lnet/minecraft/world/level/block/entity/BlockEntityType;",at = @At(
        value = "INVOKE",
        target = "Ljava/util/stream/Stream;toArray(Ljava/util/function/IntFunction;)[Ljava/lang/Object;"
    ))
    protected <A> A[] createEntry(Stream<? extends Block> instance, IntFunction<A[]> intFunction, Operation<A[]> original) {
        instance = Stream.concat(
            instance,
            anvilcraftKaleidoscope$validBlocks.stream().flatMap(sup -> sup.get().stream())
        );
        return original.call(instance, intFunction);
    }
}
