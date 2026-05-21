package dev.anvilcraft.kaleidoscope.init;

import com.github.ysbbbbbb.kaleidoscopedoll.event.ModRegisterEvent;
import dev.anvilcraft.kaleidoscope.block.entity.DollBlockEntity;
import dev.anvilcraft.kaleidoscope.client.renderer.DollBlockEntityRenderer;
import dev.anvilcraft.kaleidoscope.extension.IBlockEntityBuilderExtension;
import dev.anvilcraft.lib.v2.registrum.Registrum;
import dev.anvilcraft.lib.v2.registrum.builders.BlockEntityBuilder;
import dev.anvilcraft.lib.v2.registrum.util.entry.BlockEntityEntry;

import static dev.anvilcraft.kaleidoscope.AnvilCraftKaleidoscope.REGISTRATE;

public class AddonBlockEntities {
    public static final BlockEntityBuilder<DollBlockEntity, Registrum> DOLL_BLOCK_BUILDER;

    static {
        var builder = REGISTRATE.blockEntity(
                "doll_block",
                DollBlockEntity::new
            )
            .renderer(() -> DollBlockEntityRenderer::new);
        //noinspection unchecked
        DOLL_BLOCK_BUILDER = ((IBlockEntityBuilderExtension<DollBlockEntity, Registrum>) builder)
            .anvilcraftKaleidoscope$validBlocks(ModRegisterEvent.DOLL_BLOCKS::values);
    }

    public static final BlockEntityEntry<DollBlockEntity> DOLL_BLOCK = DOLL_BLOCK_BUILDER.register();

    public static void init() {
    }
}
