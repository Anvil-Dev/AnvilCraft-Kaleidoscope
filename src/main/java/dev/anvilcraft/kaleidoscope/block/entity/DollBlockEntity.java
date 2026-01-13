package dev.anvilcraft.kaleidoscope.block.entity;

import com.github.ysbbbbbb.kaleidoscopedoll.block.DollBlock;
import dev.anvilcraft.kaleidoscope.extension.IDollBlockEntityExtension;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class DollBlockEntity extends BlockEntity implements IDollBlockEntityExtension {
    private int rightClickedTime = 0;

    public DollBlockEntity(
        BlockEntityType<?> type,
        BlockPos pos,
        BlockState blockState
    ) {
        super(type, pos, blockState);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("rightClickedTime", rightClickedTime);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        rightClickedTime = tag.getInt("rightClickedTime");
    }

    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

    @Nullable
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public boolean isValidBlockState(BlockState state) {
        return state.getBlock() instanceof DollBlock;
    }

    @Override
    public int anvilcraftKaleidoscope$getRightClickedTime() {
        return this.rightClickedTime;
    }

    @Override
    public void anvilcraftKaleidoscope$setRightClickedTime(int rightClickedTime) {
        this.rightClickedTime = rightClickedTime;
    }

    @Override
    public void anvilcraftKaleidoscope$setChanged() {
        this.setChanged();
        if (this.level != null) {
            BlockState state = this.level.getBlockState(this.worldPosition);
            this.level.sendBlockUpdated(this.worldPosition, state, state, 3);
        }
    }
}
