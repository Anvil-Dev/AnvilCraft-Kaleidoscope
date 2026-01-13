package dev.anvilcraft.kaleidoscope.client;

import dev.anvilcraft.kaleidoscope.AnvilCraftKaleidoscope;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(value = AnvilCraftKaleidoscope.MOD_ID, dist = Dist.CLIENT)
public class AnvilCraftKaleidoscopeClient {
    public AnvilCraftKaleidoscopeClient(IEventBus modBus, ModContainer container) {
    }
}
