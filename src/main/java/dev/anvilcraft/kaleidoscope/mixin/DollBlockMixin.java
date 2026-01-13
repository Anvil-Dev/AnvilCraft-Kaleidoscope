package dev.anvilcraft.kaleidoscope.mixin;

import com.github.ysbbbbbb.kaleidoscopedoll.block.DollBlock;
import dev.anvilcraft.kaleidoscope.extension.IDollBlockEntityExtension;
import dev.anvilcraft.kaleidoscope.init.AddonBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DollBlock.class)
abstract class DollBlockMixin extends Block implements EntityBlock {
    public DollBlockMixin(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return AddonBlockEntities.DOLL_BLOCK.create(blockPos, blockState);
    }

    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Inject(
        method = "useWithoutItem",
        at = @At(
            value = "INVOKE",
            target = "Lcom/github/ysbbbbbb/kaleidoscopedoll/block/DollBlock;playDollSound"
                     + "(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;)V"
        )
    )
    public void useWithoutItem(
        BlockState blockState,
        Level level,
        BlockPos blockPos,
        Player player,
        BlockHitResult hitResult,
        CallbackInfoReturnable<InteractionResult> cir
    ) {
        if (!blockState.hasBlockEntity()) return;
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if (!(blockEntity instanceof IDollBlockEntityExtension extension)) return;
        extension.anvilcraftKaleidoscope$setRightClickedTime(15);
        extension.anvilcraftKaleidoscope$setChanged();
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(
        Level level,
        BlockState state,
        BlockEntityType<T> blockEntityType
    ) {
        return IDollBlockEntityExtension::tick;
    }
}
