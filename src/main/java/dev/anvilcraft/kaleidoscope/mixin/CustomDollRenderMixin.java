package dev.anvilcraft.kaleidoscope.mixin;

import com.github.ysbbbbbb.kaleidoscopedoll.block.entity.CustomDollBlockEntity;
import com.github.ysbbbbbb.kaleidoscopedoll.client.render.CustomDollRender;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.anvilcraft.kaleidoscope.extension.IDollBlockEntityExtension;
import net.minecraft.client.model.Model;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CustomDollRender.class)
abstract class CustomDollRenderMixin {
    @WrapOperation(
        method = "render("
                 + "Lcom/github/ysbbbbbb/kaleidoscopedoll/block/entity/CustomDollBlockEntity;"
                 + "FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II"
                 + ")V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/model/Model;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V"
        )
    )
    public void render(
        Model instance,
        PoseStack stack,
        VertexConsumer vertexConsumer,
        int packedLight,
        int packedOverlay,
        int color,
        Operation<Void> original,
        @Local(argsOnly = true) float partialTick,
        @Local(argsOnly = true) CustomDollBlockEntity doll
    ) {
        IDollBlockEntityExtension.renderPush(stack, doll, partialTick);
        original.call(instance, stack, vertexConsumer, packedLight, packedOverlay, color);
        IDollBlockEntityExtension.renderPop(stack, doll);
    }
}
