package dev.anvilcraft.kaleidoscope.mixin;

import com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen.PotBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.MillstoneBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.PotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.StockpotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.FoodBiteRegistry;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import dev.dubhe.anvilcraft.api.itemhandler.FilteredItemStackHandler;
import dev.dubhe.anvilcraft.block.entity.BaseChuteBlockEntity;
import dev.dubhe.anvilcraft.util.dummy.DummyCat;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

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

    @Shadow
    protected abstract Direction getInputDirection();

    @Shadow
    public abstract FilteredItemStackHandler getItemHandler();

    @WrapOperation(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;noCollision(Lnet/minecraft/world/phys/AABB;)Z"
        )
    )
    boolean input(Level instance, AABB aabb, Operation<Boolean> original, @Local(name = "resetCD") LocalBooleanRef resetCD) {
        BlockPos relative = this.getBlockPos().relative(this.getOutputDirection());
        BlockEntity entity = instance.getBlockEntity(relative);
        boolean success = false;
        if (entity instanceof MillstoneBlockEntity blockEntity) {
            for (int slot = 0; slot < this.itemHandler.getSlots(); slot++) {
                ItemStack stackInSlot = this.itemHandler.getStackInSlot(slot);
                success |= blockEntity.onPutItem(instance, stackInSlot);
            }
        } else if ((entity instanceof StockpotBlockEntity blockEntity)) {
            DummyCat dummyCat = anvilcraftKaleidoscope$dummyCats.computeIfAbsent(instance, DummyCat::new);
            dummyCat.setPos(relative.getX() + 0.5, relative.getY() + 0.5, relative.getZ() + 0.5);
            for (int slot = 0; slot < this.itemHandler.getSlots(); slot++) {
                ItemStack stackInSlot = this.itemHandler.getStackInSlot(slot);
                success |= blockEntity.addIngredient(instance, dummyCat, stackInSlot);
            }
        } else if (entity instanceof PotBlockEntity blockEntity) {
            DummyCat dummyCat = anvilcraftKaleidoscope$dummyCats.computeIfAbsent(instance, DummyCat::new);
            dummyCat.setPos(relative.getX() + 0.5, relative.getY() + 0.5, relative.getZ() + 0.5);
            BlockState state = instance.getBlockState(relative);
            boolean hasOil = state.getValue(PotBlock.HAS_OIL);
            if (!hasOil) {
                for (int slot = 0; slot < this.itemHandler.getSlots(); slot++) {
                    ItemStack stackInSlot = this.itemHandler.getStackInSlot(slot);
                    success = blockEntity.onPlaceOil(instance, dummyCat, stackInSlot);
                    if (success) {
                        break;
                    }
                }
            } else {
                for (int slot = 0; slot < this.itemHandler.getSlots(); slot++) {
                    ItemStack stackInSlot = this.itemHandler.getStackInSlot(slot);
                    success |= blockEntity.addIngredient(instance, dummyCat, stackInSlot);
                }
            }
        }
        if (success) resetCD.set(true);
        return !success && original.call(instance, aabb);
    }


    @WrapOperation(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;getEntitiesOfClass(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;",
            ordinal = 1
        )
    )
    <T extends Entity> List<T> output(Level instance, Class<T> aClass, AABB aabb, Predicate<T> predicate, Operation<List<T>> original) {
        boolean success = false;
        BlockPos relative = this.getBlockPos().relative(this.getInputDirection());
        BlockEntity entity = instance.getBlockEntity(relative);
        insert:
        if (entity instanceof PotBlockEntity blockEntity && (blockEntity.getStatus() == 2 || blockEntity.getStatus() == 3)) {
            ItemStack finallyResult = blockEntity.getStatus() == 2
                                      ? blockEntity.getResult()
                                      : FoodBiteRegistry.getItem(FoodBiteRegistry.DARK_CUISINE).getDefaultInstance();
            if (finallyResult.is(FoodBiteRegistry.getItem(FoodBiteRegistry.SUSPICIOUS_STIR_FRY))) break insert;
            ItemStack remaining = ItemHandlerHelper.insertItem(this.itemHandler, finallyResult, true);
            if (remaining.getCount() > 0) break insert;
            success = true;
            ItemHandlerHelper.insertItem(this.itemHandler, finallyResult, false);
            blockEntity.reset();
        }
        return success ? List.of() : original.call(instance, aClass, aabb, predicate);
    }
}
