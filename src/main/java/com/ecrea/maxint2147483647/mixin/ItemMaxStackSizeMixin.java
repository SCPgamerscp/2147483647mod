package com.ecrea.maxint2147483647.mixin;

import com.ecrea.maxint2147483647.config.MaxIntConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * ItemStack に対するMixin。
 *
 * 1. getMaxStackSize() の戻り値を Config値(デフォルト Integer.MAX_VALUE) に変更。
 *    Forge 1.20.1 では ItemStack.getMaxStackSize() が IForgeItemStack → IForgeItem →
 *    Item.getMaxStackSize() というチェーンで呼ばれるため、ItemStack 側で
 *    最終的な戻り値を確実に上書きする。
 *
 * 2. enchant() 完了後に "lvlInt" (int型) を追加保存し、
 *    バニラの "lvl" (short型, -32768〜32767) の範囲を超える
 *    エンチャントレベルの保存に対応する。
 */
@Mixin(ItemStack.class)
public class ItemMaxStackSizeMixin {

    @Inject(method = "getMaxStackSize", at = @At("RETURN"), cancellable = true)
    private void onGetMaxStackSize(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(MaxIntConfig.itemMaxStackSize());
    }

    /**
     * ItemStack.enchant(Enchantment, int) の完了後に、
     * 追加されたエンチャント CompoundTag へ "lvlInt" を書き込む。
     * バニラは putShort("lvl", (short) level) で保存するため、
     * 32767 を超えるレベルは short にキャストされてデータが壊れる。
     * "lvlInt" に int のまま保存し、読み込み時に EnchantmentHelperMixin で復元する。
     */
    @Inject(method = "enchant", at = @At("RETURN"), require = 0)
    private void onEnchant(Enchantment enchantment, int level, CallbackInfo ci) {
        ItemStack self = (ItemStack) (Object) this;
        ListTag list = self.getEnchantmentTags();
        if (!list.isEmpty()) {
            CompoundTag last = list.getCompound(list.size() - 1);
            last.putInt("lvlInt", level);
        }
    }
}
