package com.ecrea.maxint2147483647.mixin;

import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.ecrea.maxint2147483647.config.MaxIntConfig;

@Mixin(BaseContainerBlockEntity.class)
public class BaseContainerBlockEntityMixin {
    @Inject(method = "getMaxStackSize()I", at = @At("HEAD"), cancellable = true)
    private void onGetMaxStackSize(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(MaxIntConfig.itemMaxStackSize());
    }
}
