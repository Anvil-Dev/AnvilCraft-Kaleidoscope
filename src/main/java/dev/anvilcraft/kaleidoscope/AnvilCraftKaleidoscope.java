package dev.anvilcraft.kaleidoscope;

import com.mojang.logging.LogUtils;
import com.tterrag.registrate.Registrate;
import dev.anvilcraft.kaleidoscope.data.AddonDatagen;
import dev.anvilcraft.kaleidoscope.init.AddonBlockEntities;
import dev.anvilcraft.kaleidoscope.init.AddonItemGroups;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(AnvilCraftKaleidoscope.MOD_ID)
public class AnvilCraftKaleidoscope {
    public static final String MOD_ID = "anvilcraft_kaleidoscope";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final Registrate REGISTRATE = Registrate.create(MOD_ID);

    public AnvilCraftKaleidoscope(IEventBus modEventBus, ModContainer modContainer) {
        AddonBlockEntities.init();
        AddonDatagen.init();
        AddonItemGroups.register(modEventBus);
    }

    public static ResourceLocation of(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
