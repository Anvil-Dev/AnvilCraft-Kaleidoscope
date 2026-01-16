package dev.anvilcraft.kaleidoscope.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalCharRef;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RegistrateLangProvider.class)
abstract class RegistrateLangProviderMixin {
    @Unique
    private static final String NORMAL_CHARS = "(){}[]<>";
    @Unique
    private static final String UPSIDE_DOWN_CHARS = ")(}{][><";
    @Inject(method = "toUpsideDown", at = @At(value = "INVOKE", target = "Ljava/lang/String;indexOf(I)I"))
    private void toUpsideDown(String normal, CallbackInfoReturnable<String> cir, @Local(name = "c") LocalCharRef c) {
        int lookup = NORMAL_CHARS.indexOf(c.get());
        if (lookup >= 0) {
            c.set(UPSIDE_DOWN_CHARS.charAt(lookup));
        }
    }
}
