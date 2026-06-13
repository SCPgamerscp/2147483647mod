package com.ecrea.maxint2147483647.mixin;

import com.ecrea.maxint2147483647.config.MaxIntConfig;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * 金床でのエンチャント本合成時、合成後レベルが
 * enchantment.getMaxLevel() でクランプされる処理を
 * Configの値(デフォルト Integer.MAX_VALUE)に置き換える。
 * Modエンチャントを含む全てのエンチャントが対象。
 */
@Mixin(AnvilMenu.class)
public class AnvilMenuMixin {

    @Redirect(method = "createResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/Enchantment;getMaxLevel()I"))
    private int redirectGetMaxLevel(Enchantment enchantment) {
        return MaxIntConfig.enchantMaxLevel();
    }
}
