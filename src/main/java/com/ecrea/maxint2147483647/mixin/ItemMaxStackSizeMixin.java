package com.ecrea.maxint2147483647.mixin;

import com.ecrea.maxint2147483647.config.MaxIntConfig;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 全アイテム(バニラ・他Mod含む)のスタック上限をConfigの値
 * (デフォルト Integer.MAX_VALUE) に変更する。
 * ItemStack#getMaxStackSize() は Item#getMaxStackSize() を参照するため、
 * クラフト/ホッパー/チェスト等のアイテム移動処理も自動で対応する。
 */
@Mixin(Item.class)
public class ItemMaxStackSizeMixin {

    @Inject(method = "getMaxStackSize", at = @At("RETURN"), cancellable = true)
    private void onGetMaxStackSize(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(MaxIntConfig.itemMaxStackSize());
    }
}
