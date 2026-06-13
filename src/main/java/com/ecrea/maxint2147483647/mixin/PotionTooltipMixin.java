package com.ecrea.maxint2147483647.mixin;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.item.alchemy.PotionUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * ポーション効果のツールチップで増幅値(amplifier)を表示する際、
 * バニラは "potion.potency.X" という翻訳キーを使うが、
 * X が大きい(5を超える等)場合は対応する翻訳が存在せず
 * キー名がそのまま表示されてしまう。
 *
 * このMixinは翻訳キーが存在しない場合、ローマ数字ではなく
 * 通常の数字(X)をそのまま表示するようにする。
 */
@Mixin(PotionUtils.class)
public class PotionTooltipMixin {

    @Redirect(method = "addPotionTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/language/I18n;get(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;"))
    private static String redirectPotency(String key, Object[] args) {
        if (key.startsWith("potion.potency.") && !I18n.exists(key)) {
            return key.substring("potion.potency.".length());
        }
        return I18n.get(key, args);
    }
}
