package dev.anvilcraft.kaleidoscope.mixin;

import com.github.ysbbbbbb.kaleidoscopedoll.item.CustomDollItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

import static com.github.ysbbbbbb.kaleidoscopedoll.item.CustomDollItem.getModelId;

@Mixin(CustomDollItem.class)
abstract class CustomDollItemMixin {
    @Inject(
        method = "lambda$addCreativeTab$0",
        at = @At(
            value = "INVOKE",
            target = "Lcom/github/ysbbbbbb/kaleidoscopedoll/item/CustomDollItem;setModelId(Lnet/minecraft/world/item/ItemStack;Ljava/lang/String;)V"
        ),
        cancellable = true
    )
    private static void addCreativeTab(CreativeModeTab.Output output, String modelId, CallbackInfo ci) {
        if (modelId.contains(".anvilcraft.")) ci.cancel();
    }

    @Inject(
        method = "appendHoverText",
        at = @At(
            value = "INVOKE",
            target = "Lcom/github/ysbbbbbb/kaleidoscopedoll/client/custom/CustomDollLoader;getLanguage("
                     + "Ljava/lang/String;Ljava/lang/String;"
                     + ")Ljava/lang/String;"
        )
    )
    void appendHoverText(
        ItemStack stack,
        Item.TooltipContext context,
        List<Component> list,
        TooltipFlag flag,
        CallbackInfo ci
    ) {
        String modelId = getModelId(stack);
        if (modelId == null || !modelId.contains(".anvilcraft.")) return;
        String[] split = modelId.split("\\.");
        StringBuilder desc = new StringBuilder();
        for (String s : split) {
            if (s.equals(split[split.length - 1])) break;
            desc.append(s).append(".");
        }
        desc.append("desc");
        list.add(Component.translatable(desc.toString()).withStyle(ChatFormatting.DARK_GRAY));
    }
}
