package dev.anvilcraft.kaleidoscope.data;

import dev.anvilcraft.lib.v2.registrum.providers.ProviderType;
import dev.anvilcraft.kaleidoscope.AnvilCraftKaleidoscope;
import dev.anvilcraft.kaleidoscope.data.lang.AddonLangHandler;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import static dev.anvilcraft.kaleidoscope.AnvilCraftKaleidoscope.REGISTRATE;

@EventBusSubscriber(modid = AnvilCraftKaleidoscope.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class AddonDatagen {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {}

    /**
     * 初始化生成器
     */
    public static void init() {
        REGISTRATE.addDataGenerator(ProviderType.LANG, AddonLangHandler::init);
    }
}
