package com.ecrea.maxint2147483647.mixin;

import net.minecraft.server.commands.GiveCommand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * /give コマンドの整数オーバーフローを修正。
 *
 * バニラの GiveCommand.giveItem() 内では
 *   int maxAllowed = maxStackSize * MAX_ALLOWED_ITEMSTACKS (100)
 * として、一度に与えられるアイテム数の上限を計算する。
 *
 * maxStackSize が Integer.MAX_VALUE の場合、
 * 2147483647 * 100 = 整数オーバーフロー → 負数となり、
 * count > maxAllowed が常に true → /give が常に失敗する。
 *
 * getMaxStackSize() の戻り値を Integer.MAX_VALUE / 100 に
 * クランプすることで、maxAllowed の計算が安全に行われるようにする。
 * これにより最大 2,147,483,600 個のアイテムを一度に付与できる。
 */
@Mixin(GiveCommand.class)
public class GiveCommandMixin {

    @Redirect(method = "giveItem",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/world/item/ItemStack;getMaxStackSize()I"),
              require = 0)
    private static int fixOverflow(ItemStack stack) {
        int real = stack.getMaxStackSize();
        // Integer.MAX_VALUE / 100 = 21,474,836
        // 21,474,836 * 100 = 2,147,483,600 (オーバーフローしない)
        return Math.min(real, Integer.MAX_VALUE / 100);
    }
}
