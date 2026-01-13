package dev.anvilcraft.kaleidoscope.event;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopedoll.KaleidoscopeDoll;
import dev.anvilcraft.kaleidoscope.AnvilCraftKaleidoscope;
import dev.dubhe.anvilcraft.api.event.CheckIntegrationLoadedEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = AnvilCraftKaleidoscope.MOD_ID)
public class CheckIntegrationLoadedEventListener {
    @SubscribeEvent
    public static void onCheckIntegrationLoaded(CheckIntegrationLoadedEvent event) {
        if (event.getId().equals(KaleidoscopeCookery.MOD_ID)) {
            event.setLoaded();
        } else if (event.getId().equals(KaleidoscopeDoll.MOD_ID)) {
            event.setLoaded();
        }
    }
}
