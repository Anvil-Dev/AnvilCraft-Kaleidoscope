package dev.anvilcraft.kaleidoscope.data.lang;

import dev.anvilcraft.lib.v2.registrum.providers.RegistrumLangProvider;

public class AddonLangHandler {

    /**
     * 语言文件初始化
     *
     * @param provider 提供器
     */
    public static void init(RegistrumLangProvider provider) {
        provider.add("geometry.anvilcraft.plan.desc", "AnvilCraft (Plan/Designer)");
        provider.add("geometry.anvilcraft.developer.desc", "AnvilCraft (Developer)");
        provider.add("geometry.anvilcraft.contributor.desc", "AnvilCraft (Contributor)");
        provider.add("geometry.anvilcraft.supporter.desc", "AnvilCraft (Supporter)");;
        provider.add("geometry.anvilcraft.mascot.desc", "AnvilCraft (Mascot)");
    }
}
