package dev.anvilcraft.kaleidoscope.init;

import com.github.ysbbbbbb.kaleidoscopedoll.data.custom.ServerCustomDollLoader;
import com.github.ysbbbbbb.kaleidoscopedoll.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopedoll.item.CustomDollItem;
import dev.anvilcraft.kaleidoscope.AnvilCraftKaleidoscope;
import dev.dubhe.anvilcraft.init.item.ModItemGroups;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static dev.anvilcraft.kaleidoscope.AnvilCraftKaleidoscope.REGISTRATE;


public class AddonItemGroups {
    private static final DeferredRegister<CreativeModeTab> DEFERRED_REGISTER = DeferredRegister.create(
        Registries.CREATIVE_MODE_TAB,
        AnvilCraftKaleidoscope.MOD_ID
    );

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> KALEIDOSCOPE_DOLLS = DEFERRED_REGISTER.register(
        "addon_items", () -> CreativeModeTab.builder()
            .icon(() -> {
                ItemStack stack = ModItems.CUSTOM_DOLL.toStack();
                CustomDollItem.setModelId(stack, "geometry.anvilcraft.plan.xe_kr");
                return stack;
            })
            .displayItems((ctx, entries) -> ServerCustomDollLoader.getModels().forEach((modelId) -> {
                if (!modelId.contains(".anvilcraft.")) {
                    return;
                }
                ItemStack dollStack = new ItemStack(ModItems.CUSTOM_DOLL.get());
                CustomDollItem.setModelId(dollStack, modelId);
                entries.accept(dollStack);
            }))
            .title(REGISTRATE.addLang("itemGroup", AnvilCraftKaleidoscope.of("kaleidoscope_dolls"), "AnvilCraft: Kaleidoscope Dolls"))
            .withTabsBefore(ModItemGroups.ANVILCRAFT_BUILD_BLOCK.getId())
            .build()
    );

    public static void register(IEventBus modEventBus) {
        DEFERRED_REGISTER.register(modEventBus);
    }
}
