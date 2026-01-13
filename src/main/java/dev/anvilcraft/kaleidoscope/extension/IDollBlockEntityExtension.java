package dev.anvilcraft.kaleidoscope.extension;

import com.github.ysbbbbbb.kaleidoscopedoll.block.entity.CustomDollBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public interface IDollBlockEntityExtension {
    default int anvilcraftKaleidoscope$getRightClickedTime() {
        throw new AssertionError();
    }

    default void anvilcraftKaleidoscope$setRightClickedTime(int rightClickedTime) {
        throw new AssertionError();
    }

    default void anvilcraftKaleidoscope$setChanged() {
        throw new AssertionError();
    }

    static <T extends BlockEntity> void tick(Level level, BlockPos pos, BlockState state, @Nullable T blockEntity) {
        if (blockEntity == null) return;
        if (!(blockEntity instanceof IDollBlockEntityExtension extension)) return;
        int rightClickedTime = extension.anvilcraftKaleidoscope$getRightClickedTime();
        if (rightClickedTime > 0) {
            extension.anvilcraftKaleidoscope$setRightClickedTime(rightClickedTime - 1);
        }
    }

    @OnlyIn(Dist.CLIENT)
    static <T extends BlockEntity> void renderPush(PoseStack stack, @Nullable T blockEntity, float partialTick) {
        if (blockEntity == null) return;
        if (!(blockEntity instanceof IDollBlockEntityExtension extension)) return;
        stack.pushPose();
        float rightClickedTime = Math.max(0.0f, extension.anvilcraftKaleidoscope$getRightClickedTime() - partialTick);
        float clicked = (15 - rightClickedTime) / 15.0f;
        if (blockEntity instanceof CustomDollBlockEntity) {
            stack.translate(0.0f, 0.75 * (1 - clicked), 0.0f);
        }
        stack.scale(1.0f, 0.5f + 0.5f * clicked, 1.0f);
    }

    @OnlyIn(Dist.CLIENT)
    static <T extends BlockEntity> void renderPop(PoseStack stack, @Nullable T blockEntity) {
        if (blockEntity == null) return;
        if (!(blockEntity instanceof IDollBlockEntityExtension)) return;
        stack.popPose();
    }
}
