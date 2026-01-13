package dev.anvilcraft.kaleidoscope.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.anvilcraft.kaleidoscope.block.entity.DollBlockEntity;
import dev.anvilcraft.kaleidoscope.extension.IDollBlockEntityExtension;
import dev.anvilcraft.kaleidoscope.mixin.BlockRenderDispatcherAccessor;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.RenderTypeHelper;
import net.neoforged.neoforge.client.model.data.ModelData;

public class DollBlockEntityRenderer implements BlockEntityRenderer<DollBlockEntity> {
    private final BlockRenderDispatcher dispatcher;

    public DollBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        dispatcher = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(
        DollBlockEntity dollBlockEntity,
        float partialTick,
        PoseStack poseStack,
        MultiBufferSource multiBufferSource,
        int packedLight,
        int packedOverlay
    ) {
        BlockState state = dollBlockEntity.getBlockState();
        BakedModel bakedmodel = dispatcher.getBlockModel(state);
        int i = ((BlockRenderDispatcherAccessor) dispatcher).getBlockColors().getColor(state, null, null, 0);
        float f = (float) (i >> 16 & 0xFF) / 255.0F;
        float f1 = (float) (i >> 8 & 0xFF) / 255.0F;
        float f2 = (float) (i & 0xFF) / 255.0F;
        IDollBlockEntityExtension.renderPush(poseStack, dollBlockEntity, partialTick);
        for (RenderType rt : bakedmodel.getRenderTypes(state, RandomSource.create(42), ModelData.EMPTY)) {
            dispatcher.getModelRenderer()
                .renderModel(
                    poseStack.last(),
                    multiBufferSource.getBuffer(RenderTypeHelper.getEntityRenderType(rt, false)),
                    state,
                    bakedmodel,
                    f,
                    f1,
                    f2,
                    packedLight,
                    packedOverlay,
                    ModelData.EMPTY,
                    rt
                );
        }
        IDollBlockEntityExtension.renderPop(poseStack, dollBlockEntity);
    }
}
