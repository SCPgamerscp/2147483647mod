package com.ecrea.maxint2147483647.mixin;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.PotionUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Collection;

@Mixin(PotionUtils.class)
public class PotionUtilsMixin {
    
    /**
     * getColor の計算時に amplifier + 1 がオーバーフローしないように、
     * 計算に使われる amplifier を最大 255 にクランプする。
     */
    @Redirect(method = "getColor(Ljava/util/Collection;)I", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/effect/MobEffectInstance;getAmplifier()I"))
    private static int clampAmplifierForColor(MobEffectInstance instance) {
        int amp = instance.getAmplifier();
        return Math.min(amp, 255);
    }
}
