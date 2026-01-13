package dev.anvilcraft.kaleidoscope.mixin;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.MillstoneBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.StockpotBlockEntity;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.dubhe.anvilcraft.api.itemhandler.FilteredItemStackHandler;
import dev.dubhe.anvilcraft.block.entity.BaseChuteBlockEntity;
import dev.dubhe.anvilcraft.util.dummy.DummyCat;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.HashMap;
import java.util.Map;

@Mixin(BaseChuteBlockEntity.class)
public abstract class BaseChuteBlockEntityMixin extends BlockEntity {
    public BaseChuteBlockEntityMixin(
        BlockEntityType<?> type,
        BlockPos pos,
        BlockState blockState
    ) {
        super(type, pos, blockState);
    }

    @Shadow
    protected abstract Direction getOutputDirection();

    @Unique
    private static final Map<Level, DummyCat> anvilcraftKaleidoscope$dummyCats = new HashMap<>();

    @Shadow
    @Final
    private FilteredItemStackHandler itemHandler;

    @WrapOperation(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;noCollision(Lnet/minecraft/world/phys/AABB;)Z"
        )
    )
    boolean tick(Level instance, AABB aabb, Operation<Boolean> original) {
        BlockPos relative = this.getBlockPos().relative(this.getOutputDirection());
        BlockEntity entity = instance.getBlockEntity(relative);
        boolean success = false;
        if (entity instanceof MillstoneBlockEntity blockEntity) {
            for (int slot = 0; slot < this.itemHandler.getSlots(); slot++) {
                ItemStack stackInSlot = this.itemHandler.getStackInSlot(slot);
                success |= blockEntity.onPutItem(instance, stackInSlot);
            }
        } else if (entity instanceof StockpotBlockEntity blockEntity) {
            DummyCat dummyCat = anvilcraftKaleidoscope$dummyCats.computeIfAbsent(instance, DummyCat::new);
            dummyCat.setPos(relative.getX() + 0.5, relative.getY() + 0.5, relative.getZ() + 0.5);
            for (int slot = 0; slot < this.itemHandler.getSlots(); slot++) {
                ItemStack stackInSlot = this.itemHandler.getStackInSlot(slot);
                success |= blockEntity.addIngredient(instance, dummyCat, stackInSlot);
            }
        }
        return !success && original.call(instance, aabb);
    }
}
