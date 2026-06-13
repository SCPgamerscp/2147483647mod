package com.ecrea.maxint2147483647.mixin;

import net.minecraftforge.items.SlotItemHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.ecrea.maxint2147483647.config.MaxIntConfig;

@Mixin(value = SlotItemHandler.class, remap = false)
public class SlotItemHandlerMixin {
    @Inject(method = "getMaxStackSize()I", at = @At("HEAD"), cancellable = true, require = 0)
    private void onGetMaxStackSize(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(MaxIntConfig.itemMaxStackSize());
    }

    @Inject(method = "getMaxStackSize(Lnet/minecraft/world/item/ItemStack;)I", at = @At("HEAD"), cancellable = true, require = 0)
    private void onGetMaxStackSizeWithItem(net.minecraft.world.item.ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(MaxIntConfig.itemMaxStackSize());
    }
}
