package dev.anvilcraft.kaleidoscope.mixin;

import com.github.ysbbbbbb.kaleidoscopedoll.block.entity.CustomDollBlockEntity;
import dev.anvilcraft.kaleidoscope.extension.IDollBlockEntityExtension;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CustomDollBlockEntity.class)
abstract class CustomDollBlockEntityMixin implements IDollBlockEntityExtension {
    @Shadow public abstract void refresh();

    @Unique
    int anvilcraftKaleidoscope$rightClickedTime = 0;

    @Override
    public int anvilcraftKaleidoscope$getRightClickedTime() {
        return anvilcraftKaleidoscope$rightClickedTime;
    }

    @Override
    public void anvilcraftKaleidoscope$setRightClickedTime(int rightClickedTime) {
        this.anvilcraftKaleidoscope$rightClickedTime = rightClickedTime;
    }

    @Override
    public void anvilcraftKaleidoscope$setChanged() {
        this.refresh();
    }

    @Inject(
        method = "saveAdditional",
        at = @At("RETURN")
    )
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries, CallbackInfo ci) {
        tag.putInt("rightClickedTime", anvilcraftKaleidoscope$rightClickedTime);
    }

    @Inject(
        method = "loadAdditional",
        at = @At("RETURN")
    )
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries, CallbackInfo ci) {
        anvilcraftKaleidoscope$rightClickedTime = tag.getInt("rightClickedTime");
    }
}
