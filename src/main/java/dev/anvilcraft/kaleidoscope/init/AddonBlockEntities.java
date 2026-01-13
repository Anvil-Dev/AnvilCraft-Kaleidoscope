package dev.anvilcraft.kaleidoscope.init;

import com.github.ysbbbbbb.kaleidoscopedoll.event.ModRegisterEvent;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.builders.BlockEntityBuilder;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import dev.anvilcraft.kaleidoscope.block.entity.DollBlockEntity;
import dev.anvilcraft.kaleidoscope.client.renderer.DollBlockEntityRenderer;
import dev.anvilcraft.kaleidoscope.extension.IBlockEntityBuilderExtension;

import static dev.anvilcraft.kaleidoscope.AnvilCraftKaleidoscope.REGISTRATE;

public class AddonBlockEntities {
    public static final BlockEntityBuilder<DollBlockEntity, Registrate> DOLL_BLOCK_BUILDER;

    static {
        var builder = REGISTRATE.blockEntity(
                "doll_block",
                DollBlockEntity::new
            )
            .renderer(() -> DollBlockEntityRenderer::new);
        //noinspection unchecked
        DOLL_BLOCK_BUILDER = ((IBlockEntityBuilderExtension<DollBlockEntity, Registrate>) builder)
            .anvilcraftKaleidoscope$validBlocks(ModRegisterEvent.DOLL_BLOCKS::values);
    }

    public static final BlockEntityEntry<DollBlockEntity> DOLL_BLOCK = DOLL_BLOCK_BUILDER.register();

    public static void init() {
    }
}
