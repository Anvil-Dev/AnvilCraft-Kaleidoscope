package dev.anvilcraft.kaleidoscope.data.lang;

import com.tterrag.registrate.providers.RegistrateLangProvider;

public class AddonLangHandler {

    /**
     * 语言文件初始化
     *
     * @param provider 提供器
     */
    public static void init(RegistrateLangProvider provider) {
        provider.add("geometry.anvilcraft.plan.desc", "AnvilCraft (Plan/Designer)");
        provider.add("geometry.anvilcraft.developer.desc", "AnvilCraft (Developer)");
        provider.add("geometry.anvilcraft.contributor.desc", "AnvilCraft (Contributor)");
        provider.add("geometry.anvilcraft.supporter.desc", "AnvilCraft (Supporter)");
    }
}
