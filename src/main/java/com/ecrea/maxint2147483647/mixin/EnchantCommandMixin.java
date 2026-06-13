package com.ecrea.maxint2147483647.mixin;

import com.ecrea.maxint2147483647.config.MaxIntConfig;
import net.minecraft.server.commands.EnchantCommand;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * /enchant コマンドのレベル上限チェック (enchantment.getMaxLevel())を
 * Configの値(デフォルト Integer.MAX_VALUE)に置き換える。
 * Modエンチャントを含む全てのエンチャントが対象。
 */
@Mixin(EnchantCommand.class)
public class EnchantCommandMixin {

    @Redirect(method = "enchant", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/Enchantment;getMaxLevel()I"))
    private static int redirectGetMaxLevel(Enchantment enchantment) {
        return MaxIntConfig.enchantMaxLevel();
    }
}
