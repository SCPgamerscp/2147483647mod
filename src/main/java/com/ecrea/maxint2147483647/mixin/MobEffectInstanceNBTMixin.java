package com.ecrea.maxint2147483647.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * バニラの MobEffectInstance は amplifier(増幅値) を NBT保存時に
 * byte (-128~127) として書き込むため、127を超える増幅値が
 * 保存・読込時に壊れてしまう。
 *
 * このMixinは保存時に "AmplifierInt" として完全なint値を
 * 追加で書き込み、読込時にその値が存在する場合は
 * amplifierを復元する。
 */
@Mixin(MobEffectInstance.class)
public class MobEffectInstanceNBTMixin {

    @Inject(method = "writeDetailsTo", at = @At("RETURN"))
    private void onWriteDetailsTo(CompoundTag tag, CallbackInfo ci) {
        MobEffectInstance self = (MobEffectInstance) (Object) this;
        tag.putInt("AmplifierInt", self.getAmplifier());
    }

    @Inject(method = "load", at = @At("RETURN"), cancellable = true)
    private static void onLoad(CompoundTag tag, CallbackInfoReturnable<MobEffectInstance> cir) {
        if (!tag.contains("AmplifierInt")) {
            return;
        }

        MobEffectInstance original = cir.getReturnValue();
        int amplifier = tag.getInt("AmplifierInt");

        if (original == null) {
            // もし他のModの干渉やバニラの仕様でパースに失敗し null が返された場合でも、
            // AmplifierInt が存在すれば自力でパースを試みる。
            int id = tag.getInt("Id");
            net.minecraft.world.effect.MobEffect effect = net.minecraft.world.effect.MobEffect.byId(id);
            if (effect != null) {
                int duration = tag.contains("Duration", 99) ? tag.getInt("Duration") : 0;
                boolean ambient = tag.getBoolean("Ambient");
                boolean showParticles = tag.contains("ShowParticles", 1) ? tag.getBoolean("ShowParticles") : true;
                boolean showIcon = tag.contains("ShowIcon", 1) ? tag.getBoolean("ShowIcon") : true;
                
                MobEffectInstance fixed = new MobEffectInstance(
                        effect, duration, amplifier, ambient, showParticles, showIcon, null, java.util.Optional.empty()
                );
                cir.setReturnValue(fixed);
            }
            return;
        }

        if (amplifier == original.getAmplifier()) {
            return;
        }

        MobEffectInstance fixed = new MobEffectInstance(
                original.getEffect(),
                original.getDuration(),
                amplifier,
                original.isAmbient(),
                original.isVisible(),
                original.showIcon(),
                null,
                java.util.Optional.empty()
        );
        cir.setReturnValue(fixed);
    }
}
